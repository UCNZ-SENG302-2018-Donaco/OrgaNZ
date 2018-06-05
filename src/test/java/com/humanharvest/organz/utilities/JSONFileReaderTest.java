package com.humanharvest.organz.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;

import org.junit.Test;

public class JSONFileReaderTest {

    private void checkDeserializedClientsFromValidFile(List<Client> outputClients) {
        // Check that all 95 Clients were deserialized.
        assertEquals(95, outputClients.size());

        // Tests on the 50th Client in the file to check that all visible fields were deserialized correctly.
        Client testClient = outputClients.get(50);
        assertEquals("Jaeden", testClient.getFirstName());
        assertEquals("Cade", testClient.getMiddleName());
        assertEquals("Nelson", testClient.getLastName());
        assertEquals(LocalDate.of(1966, 7, 1), testClient.getDateOfBirth());
        assertNull(testClient.getDateOfDeath());
        assertEquals(Gender.MALE, testClient.getGender());
        assertEquals(210.0, testClient.getHeight(), 0.0001);
        assertEquals(143.0, testClient.getWeight(), 0.0001);
        assertEquals(BloodType.B_NEG, testClient.getBloodType());
        assertEquals("145 Old Lane", testClient.getCurrentAddress());
        assertEquals(Region.NELSON, testClient.getRegion());
        assertEquals(LocalDateTime.of(2018, 3, 21, 20, 55, 46, 275000000), testClient.getCreatedTimestamp());

        // Test that the Client's organs set was also deserialized correctly.
        List<Organ> expectedOrgans = Arrays.asList(
                Organ.HEART, Organ.LUNG, Organ.SKIN, Organ.PANCREAS, Organ.KIDNEY,Organ.BONE
        );
        Map<Organ, Boolean> testOrgans = testClient.getOrganDonationStatus();
        for (Organ organ : testOrgans.keySet()) {
            if (expectedOrgans.contains(organ)) {
                assertTrue(testOrgans.get(organ));
            } else {
                assertFalse(testOrgans.get(organ));
            }
        }
    }

    @Test
    public void readAllFromValidFileTest() throws Exception {
        File inputFile = new File("src/test/resources/many_clients.json");
        JSONFileReader<Client> ClientFileReader = new JSONFileReader<>(inputFile, Client.class);
        List<Client> outputClients = ClientFileReader.getAll();
        ClientFileReader.close();

        checkDeserializedClientsFromValidFile(outputClients);
    }

    @Test
    public void readAsStreamFromValidFileTest() throws Exception {
        File inputFile = new File("src/test/resources/many_clients.json");
        JSONFileReader<Client> ClientFileReader = new JSONFileReader<>(inputFile, Client.class);
        List<Client> outputClients = new ArrayList<>();
        Client current;

        assertEquals(0, ClientFileReader.getFilePosition());

        ClientFileReader.startStream();
        while ((current = ClientFileReader.getNext()) != null) {
            /*System.out.println(
                    String.format("%d/%d bytes read", ClientFileReader.getFilePosition(), ClientFileReader.getFileSize())
            );*/
            outputClients.add(current);
        }

        assertEquals(ClientFileReader.getFileSize(), ClientFileReader.getFilePosition());
        ClientFileReader.close();

        checkDeserializedClientsFromValidFile(outputClients);
    }

    @Test(expected = IllegalStateException.class)
    public void readAsStreamWithoutStartingStreamTest() throws Exception {
        File inputFile = new File("src/test/resources/many_clients.json");
        JSONFileReader<Client> ClientFileReader = new JSONFileReader<>(inputFile, Client.class);

        ClientFileReader.getNext();
    }

    @Test(expected = IllegalStateException.class)
    public void readAllAfterStartingStreamTest() throws Exception {
        File inputFile = new File("src/test/resources/many_clients.json");
        JSONFileReader<Client> ClientFileReader = new JSONFileReader<>(inputFile, Client.class);

        ClientFileReader.startStream();
        ClientFileReader.getAll();
    }
}
