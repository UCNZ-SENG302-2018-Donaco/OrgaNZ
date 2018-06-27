package com.humanharvest.organz.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.ClientManagerMemory;
import com.humanharvest.organz.state.State;

import com.humanharvest.organz.state.State.DataStorageType;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;
import com.humanharvest.organz.utilities.exceptions.OrganAlreadyRegisteredException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class JSONConverterTest extends BaseTest {

    private int uid;
    private Client client;
    private Organ organ;
    private TransplantRequest request;
    private int nextYear;
    private int currentYear;
    private int lastYear;
    private String medicationName;
    private MedicationRecord medicationRecord;
    private LocalDate yesterday;
    private LocalDate tomorrow;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private boolean compareFiles(String file1, String file2) {
        try {
            byte[] contents1 = Files.readAllBytes(Paths.get(file1));
            byte[] contents2 = Files.readAllBytes(Paths.get(file2));
            return Arrays.equals(contents1, contents2);
        } catch (IOException ex) {
            System.out.println("Error comparing file contents: " + ex);
            return false;
        }
    }

    @Before
    public void initialise() {
        State.init(DataStorageType.MEMORY);
        uid = 5;
        client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), uid);
        organ = Organ.CORNEA;
        try {
            client.setOrganDonationStatus(organ, true);
        } catch (OrganAlreadyRegisteredException e) {
            fail();
        }
        request = new TransplantRequest(client, organ);
        client.addTransplantRequest(request);
        currentYear = LocalDate.now().getYear();
        nextYear = currentYear + 1;
        lastYear = currentYear - 1;

        yesterday = LocalDate.now().minusDays(1);
        tomorrow = LocalDate.now().plusDays(1);
        medicationName = "my medication";
        medicationRecord = new MedicationRecord(medicationName, yesterday, LocalDate.now());
        client.addMedicationRecord(medicationRecord);
    }

    @Test
    public void saveToFileTest() throws Exception {
        File file = folder.newFile("filled_clients.json");

        ArrayList<Client> clients = new ArrayList<>();
        clients.add(client);
        State.reset(false);
        ClientManager manager = State.getClientManager();
        manager.setClients(clients);

        JSONConverter.saveToFile(file);

        StringBuilder builder = new StringBuilder();

        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            builder.append(line);
        }

        String json = builder.toString();

        assertTrue(json.contains("\"firstName\": \"First\""));
        assertTrue(json.contains("\"lastName\": \"Last\""));
    }

    private void saveClientToFile(Client client, File file) throws IOException {
        ClientManager manager = State.getClientManager();
        manager.addClient(client);

        JSONConverter.saveToFile(file);

        manager.removeClient(client); // empty the client manager
    }

    private void saveClientToAndLoadFromFile(Client client) throws IOException {
        File file = folder.newFile("testfile.json");
        saveClientToFile(client, file);
        JSONConverter.loadFromFile(file);
    }

    private void replaceAllInFile(File file, String regex, String replacement) throws IOException {
        Path path = Paths.get(file.getPath());
        Charset charset = StandardCharsets.UTF_8;
        String content = new String(Files.readAllBytes(path), charset);
        content = content.replaceAll(regex, replacement);
        Files.write(path, content.getBytes(charset));
    }

    @Test
    public void loadFromFileTest() throws Exception {
        ClientManager manager = State.getClientManager();
        saveClientToAndLoadFromFile(client);

        assertEquals(manager.getClients().size(), 1);
        assertEquals("First", manager.getClients().get(0).getFirstName());
    }

    @Test
    public void failToLoadFromFileNegativeUidTest() throws Exception {
        uid = -1;
        client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), uid);
        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileZeroUidTest() throws Exception {
        uid = 0;
        client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), uid);
        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("zero"));
        }
    }

    @Test
    public void failToLoadFromFileEmptyFirstNameTest() throws Exception {
        client.setFirstName("");
        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileEmptyLastNameTest() throws Exception {
        client.setLastName("");
        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileNullDateOfBirthTest() throws Exception {
        client.setDateOfBirth(null);
        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileInvalidDateOfBirthTest() throws Exception {
        client.setDateOfBirth(LocalDate.of(1970, 2, 22));
        File file = folder.newFile("testfile.json");
        saveClientToFile(client, file);

        // Replace day of month (22) with 30, which is invalid (30 February does not exist)
        replaceAllInFile(file, "22", "30");

        try {
            JSONConverter.loadFromFile(file);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileFutureDateOfBirthTest() throws Exception {
        client.setDateOfBirth(LocalDate.of(nextYear, 2, 22));
        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileNegativeHeightTest() throws Exception {
        client.setHeight(-8);

        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileNegativeWeightTest() throws Exception {
        client.setWeight(-8);

        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileInvalidDateOfDeathTest() throws Exception {
        client.setDateOfDeath(LocalDate.of(2018, 2, 22));
        File file = folder.newFile("testfile.json");
        saveClientToFile(client, file);

        // Replace day of month (22) with 30, which is invalid (30 February does not exist)
        replaceAllInFile(file, "22", "30");

        try {
            JSONConverter.loadFromFile(file);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileFutureDateOfDeathTest() throws Exception {
        client.setDateOfDeath(LocalDate.of(nextYear, 2, 22));

        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileFutureCreationTimestampTest() throws Exception {
        File file = folder.newFile("testfile.json");
        saveClientToFile(client, file);

        // Replace year of creation (current year) with next year - future creation dates are invalid
        String createdTimestampYearPrefixRegex = "\"createdTimestamp\":\\s*\\{\n"
                + "\\s*\"date\":\\s*\\{\n"
                + "\\s*\"year\":\\s*";
        String createdTimestampYearPrefix = "\"createdTimestamp\":\\{\n"
                + "\"date\":\\{\n"
                + "\"year\":";
        replaceAllInFile(file, createdTimestampYearPrefixRegex + String.valueOf(currentYear),
                createdTimestampYearPrefix + String.valueOf(nextYear));

        try {
            JSONConverter.loadFromFile(file);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileFutureLastModifiedTimestampTest() throws Exception {
        File file = folder.newFile("testfile.json");
        client.setLastName("Alt Last"); //a modification
        saveClientToFile(client, file);

        // Replace year of last modification (current year) with next year - future last modification dates are invalid
        String modifiedTimestampYearPrefixRegex = "\"modifiedTimestamp\":\\s*\\{\n"
                + "\\s*\"date\":\\s*\\{\n"
                + "\\s*\"year\":\\s*";
        String modifiedTimestampYearPrefix = "\"modifiedTimestamp\":\\{\n"
                + "\"date\":\\{\n"
                + "\"year\":";
        replaceAllInFile(file, modifiedTimestampYearPrefixRegex + String.valueOf(currentYear),
                modifiedTimestampYearPrefix + String.valueOf(nextYear));

        try {
            JSONConverter.loadFromFile(file);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileLastModifiedTimestampBeforeCreationTest() throws Exception {
        File file = folder.newFile("testfile.json");
        client.setLastName("Alt Last"); //a modification
        saveClientToFile(client, file);

        // Replace year of last modification (current year) with last year - this will be before creation
        String modifiedTimestampYearPrefixRegex = "\"modifiedTimestamp\":\\s*\\{\n"
                + "\\s*\"date\":\\s*\\{\n"
                + "\\s*\"year\":\\s*";
        String modifiedTimestampYearPrefix = "\"modifiedTimestamp\":\\{\n"
                + "\"date\":\\{\n"
                + "\"year\":";
        replaceAllInFile(file, modifiedTimestampYearPrefixRegex + String.valueOf(currentYear),
                modifiedTimestampYearPrefix + String.valueOf(lastYear));

        try {
            JSONConverter.loadFromFile(file);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileInvalidOrganDonatingTest() throws Exception {
        File file = folder.newFile("testfile.json");
        saveClientToFile(client, file);
        replaceAllInFile(file, organ.name(), "INVALID_ORGAN");

        try {
            JSONConverter.loadFromFile(file);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileInvalidOrganRequestOrganTest() throws Exception {
        File file = folder.newFile("testfile.json");
        saveClientToFile(client, file);
        replaceAllInFile(file, organ.name(), "INVALID_ORGAN");

        try {
            JSONConverter.loadFromFile(file);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileInvalidOrganRequestDateTest() throws Exception {
        File file = folder.newFile("testfile.json");
        saveClientToFile(client, file);

        String partialJsonTimestampRegex = "\"transplantRequests\":\\s*\\[\n"
                + "\\s*\\{\n"
                + "\\s*\"requestedOrgan\": \"" + organ.name() + "\",\n"
                + "\\s*\"requestDate\":\\s*\\{\n"
                + "\\s*\"date\":\\s*\\{\n"
                + "\\s*\"year\":\\s*[0-9]+,\n"
                + "\\s*\"month\":\\s*[0-9]+,\n"
                + "\\s*\"day\":";
        String partialJsonTimestampModified = "\"transplantRequests\": [\n"
                + "      {\n"
                + "        \"requestedOrgan\": \"LIVER\",\n"
                + "        \"requestDate\": {\n"
                + "          \"date\": {\n"
                + "            \"day\":";
        replaceAllInFile(file, partialJsonTimestampRegex, partialJsonTimestampModified);

        try {
            JSONConverter.loadFromFile(file);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileFutureOrganRequestDateTest() throws Exception {
        File file = folder.newFile("testfile.json");
        saveClientToFile(client, file);

        String partialJsonTimestampRegex = "\"transplantRequests\":\\s*\\[\n"
                + "\\s*\\{\n"
                + "\\s*\"requestedOrgan\": \"" + organ.name() + "\",\n"
                + "\\s*\"requestDate\":\\s*\\{\n"
                + "\\s*\"date\":\\s*\\{\n"
                + "\\s*\"year\": " + String.valueOf(currentYear);
        String partialJsonTimestampModified = "\"transplantRequests\": [\n"
                + "      {\n"
                + "        \"requestedOrgan\": \"LIVER\",\n"
                + "        \"requestDate\": {\n"
                + "          \"date\": {\n"
                + "            \"year\": " + String.valueOf(nextYear);
        replaceAllInFile(file, partialJsonTimestampRegex, partialJsonTimestampModified);

        try {
            JSONConverter.loadFromFile(file);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileInvalidTransplantRequestStatusTest() throws Exception {
        TransplantRequestStatus status = TransplantRequestStatus.WAITING;
        File file = folder.newFile("testfile.json");
        saveClientToFile(client, file);
        replaceAllInFile(file, status.name(), "INVALID_STATUS");

        try {
            JSONConverter.loadFromFile(file);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileInvalidResolveDateTest() throws Exception {
        File file = folder.newFile("testfile.json");
        request.setStatus(TransplantRequestStatus.COMPLETED);
        request.setResolvedDate(LocalDateTime.now());
        saveClientToFile(client, file);

        String partialJsonTimestampRegex = "\"resolvedDate\":\\s*\\{\n"
                + "\\s*\"date\":\\s*\\{\n"
                + "\\s*\"year\":\\s*[0-9]+,\n"
                + "\\s*\"month\":\\s*[0-9]+,\n"
                + "\\s*\"day\":";
        String partialJsonTimestampModified = "\"resolvedDate\": {\n"
                + "          \"date\": {\n"
                + "            \"day\":";
        replaceAllInFile(file, partialJsonTimestampRegex, partialJsonTimestampModified);

        try {
            JSONConverter.loadFromFile(file);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    private void failToLoadFromFileByChangingResolveDateTest(int year) throws Exception {
        File file = folder.newFile("testfile.json");
        request.setStatus(TransplantRequestStatus.COMPLETED);
        request.setResolvedDate(LocalDateTime.now());
        saveClientToFile(client, file);

        String partialJsonTimestampRegex = "\"resolvedDate\":\\s*\\{\n"
                + "\\s*\"date\":\\s*\\{\n"
                + "\\s*\"year\": " + String.valueOf(currentYear);
        String partialJsonTimestampModified = "\"resolvedDate\": {\n"
                + "          \"date\": {\n"
                + "            \"year\": " + String.valueOf(year);
        replaceAllInFile(file, partialJsonTimestampRegex, partialJsonTimestampModified);

        try {
            JSONConverter.loadFromFile(file);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileFutureResolveDateTest() throws Exception {
        failToLoadFromFileByChangingResolveDateTest(nextYear);
    }

    @Test
    public void failToLoadFromFileResolveDateBeforeRequestDateTest() throws Exception {
        failToLoadFromFileByChangingResolveDateTest(lastYear);
    }

    @Test
    public void failToLoadFromFileNullMedicationNameTest() throws Exception {
        MedicationRecord medicationRecordNullName = new MedicationRecord(null, LocalDate.now(), tomorrow);
        client.addMedicationRecord(medicationRecordNullName);

        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileEmptyMedicationNameTest() throws Exception {
        MedicationRecord medicationRecordNullName = new MedicationRecord("", LocalDate.now(), tomorrow);
        client.addMedicationRecord(medicationRecordNullName);

        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileNoMedicationStartDateTest() throws Exception {
        medicationRecord.setStarted(null);

        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileInvalidMedicationStartDateTest() throws Exception {
        File file = folder.newFile("testfile.json");
        saveClientToFile(client, file);

        String partialJsonTimestampRegex = "\"medicationName\":\\s*\"" + medicationName + "\",\n"
                + "\\s*\"started\":\\s*\\{\n"
                + "\\s*\"year\":\\s*[0-9]+,\n"
                + "\\s*\"month\":\\s*[0-9]+,\n"
                + "\\s*\"day\":";
        String partialJsonTimestampModified = "\"medicationName\": \"" + medicationName + "\",\n"
                + "        \"started\": {\n"
                + "          \"day\":";
        replaceAllInFile(file, partialJsonTimestampRegex, partialJsonTimestampModified);

        try {
            JSONConverter.loadFromFile(file);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileInvalidMedicationStoppedDateTest() throws Exception {
        File file = folder.newFile("testfile.json");
        saveClientToFile(client, file);

        String partialJsonTimestampRegex = "\"medicationName\":\\s*\"" + medicationName + "\","
                + "\\s*\"started\":\\s*\\{\n"
                + "\\s*\"year\":\\s*[0-9]+,\n"
                + "\\s*\"month\":\\s*[0-9]+,\n"
                + "\\s*\"day\":\\s*[0-9]+\n"
                + "\\s*},\n"
                + "\\s*\"stopped\":\\s*\\{\n"
                + "\\s*\"year\":\\s*[0-9]+,\n"
                + "\\s*\"month\":\\s*[0-9]+,\n"
                + "\\s*\"day\":";
        String partialJsonTimestampModified = "\"medicationName\": \"" + medicationName + "\",\n"
                + "        \"started\": {\n"
                + "          \"year\": " + medicationRecord.getStarted().getYear() + ",\n"
                + "          \"month\": " + medicationRecord.getStarted().getMonthValue() + ",\n"
                + "          \"day\": " + medicationRecord.getStarted().getDayOfMonth() + "\n"
                + "        },\n"
                + "        \"stopped\": {\n"
                + "          \"day\":";
        replaceAllInFile(file, partialJsonTimestampRegex, partialJsonTimestampModified);

        try {
            JSONConverter.loadFromFile(file);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileFutureMedicationStartDateTest() throws Exception {
        medicationRecord.setStarted(tomorrow);

        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileFutureMedicationStopDateTest() throws Exception {
        medicationRecord.setStopped(tomorrow);

        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileMedicationStopDateBeforeStartDateTest() throws Exception {
        medicationRecord.setStopped(LocalDate.now().minusYears(1));

        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void LoadFromFileNoMedicationStopDateTest() throws Exception {
        medicationRecord.setStopped(null);
        saveClientToAndLoadFromFile(client);
    }

    // ********* Templates *********

    //TODO
    @Test
    public void failToLoadFromFileTest() throws Exception {
        client.setFirstName(null);

        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    //TODO
    @Test
    public void failToLoadFromFileRegexTest() throws Exception {
        File file = folder.newFile("testfile.json");
        saveClientToFile(client, file);
        replaceAllInFile(file, "a", "b");

        try {
            JSONConverter.loadFromFile(file);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

}