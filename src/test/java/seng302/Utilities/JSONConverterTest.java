package seng302.Utilities;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import seng302.Client;
import seng302.State.ClientManager;
import seng302.State.State;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class JSONConverterTest {

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


    @Test
    public void saveToFileTest() throws Exception {
        File file = new File("src/test/resources/filled_clients.json");

        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        ArrayList<Client> people = new ArrayList<>();
        people.add(client);
        State.init();
        ClientManager manager = State.getClientManager();
        manager.setPeople(people);

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

//    @Test
//    public void loadFromFileTest() throws Exception {
//        //File file = folder.newFile("testfile.json");
//
//        Client client = new Client("First", null, "Last", LocalDate.of(1970,1, 1), 1);
//        ArrayList<Client> clients = new ArrayList<>();
//        clients.add(client);
//        //manager = new ClientManager(clients);
//
//        manager.saveToFile(file);
//
//        manager = new ClientManager();
//
//        manager.loadFromFile(file);
//
//        assertTrue(manager.getPeople().size() == 1);
//        assertEquals("First", manager.getPeople().get(0).getFirstName());
//    }

}