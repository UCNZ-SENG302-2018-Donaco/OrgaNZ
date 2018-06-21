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

import javax.sql.DataSource;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.ClientManagerMemory;
import com.humanharvest.organz.state.State;

import com.humanharvest.organz.state.State.DataStorageType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class JSONConverterTest extends BaseTest {

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
    }

    @Test
    public void saveToFileTest() throws Exception {
        File file = folder.newFile("filled_clients.json");

        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
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
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        saveClientToAndLoadFromFile(client);

        assertEquals(manager.getClients().size(), 1);
        assertEquals("First", manager.getClients().get(0).getFirstName());
    }

    @Test
    public void failToLoadFromFileNegativeUidTest() throws Exception {
        int uid = -1;
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), uid);
        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileZeroUidTest() throws Exception {
        int uid = 0;
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), uid);
        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("zero"));
        }
    }

    @Test
    public void failToLoadFromFileEmptyFirstNameTest() throws Exception {
        int uid = 5;
        Client client = new Client("", null, "Last", LocalDate.of(1970, 1, 1), uid);
        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileEmptyLastNameTest() throws Exception {
        int uid = 5;
        Client client = new Client("First", null, "", LocalDate.of(1970, 1, 1), uid);
        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileNullDateOfBirthTest() throws Exception {
        int uid = 5;
        Client client = new Client("First", null, "Last", null, uid);
        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileInvalidDateOfBirthTest() throws Exception {
        int uid = 5;
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 2, 22), uid);
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
        int uid = 5;
        int nextYear = LocalDate.now().getYear() + 1;
        Client client = new Client("First", null, "Last", LocalDate.of(nextYear, 2, 22), uid);
        try {
            saveClientToAndLoadFromFile(client);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

    @Test
    public void failToLoadFromFileFutureCreationTimestampTest() throws Exception {
        int uid = 5;
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), uid);
        File file = folder.newFile("testfile.json");
        saveClientToFile(client, file);

        // Replace year of creation (current year) with next year - future creation dates are invalid
        int currentYear = LocalDate.now().getYear();
        int nextYear = currentYear + 1;
        replaceAllInFile(file, String.valueOf(currentYear), String.valueOf(nextYear));

        try {
            JSONConverter.loadFromFile(file);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(uid)));
        }
    }

}