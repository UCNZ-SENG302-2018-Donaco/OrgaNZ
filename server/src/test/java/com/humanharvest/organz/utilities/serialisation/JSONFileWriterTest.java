package com.humanharvest.organz.utilities.serialisation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.humanharvest.organz.Client;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JSONFileWriterTest {

    private final List<Client> testClients = Arrays.asList(
            new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1),
            new Client("Second", null, "Sur", LocalDate.of(2001, 5, 8), 2),
            new Client("Third", "Bobby", "Man", LocalDate.of(1996, 3, 4), 3)
    );

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private static List<Client> readClientsFromFile(File file) throws IOException {
        return JSONMapper.Mapper.readValue(file, new TypeReference<List<Client>>() {
        });
    }

    @Test
    public void writeToNewFileTest() throws IOException {
        File file = new File(tempFolder.getRoot(), "new_file.json");
        try (JSONFileWriter<Client> clientJSONFileWriter = new JSONFileWriter<>(file, Client.class)) {
            clientJSONFileWriter.overwriteWith(testClients);
            clientJSONFileWriter.close();

            List<Client> readClients = readClientsFromFile(file);
            assertEquals(testClients, readClients);
        }
    }

    @Test
    public void appendOneToExistingFileTest() throws IOException {
        File file = new File(tempFolder.getRoot(), "existing_file.json");
        FileWriter fw = new FileWriter(file);
        JSONMapper.Mapper.writeValue(fw, testClients.subList(0, 2));
        fw.close();

        try (JSONFileWriter<Client> clientJSONFileWriter = new JSONFileWriter<>(file, Client.class)) {
            clientJSONFileWriter.appendOne(testClients.get(2));
            clientJSONFileWriter.close();

            List<Client> readClients = readClientsFromFile(file);
            assertEquals(testClients, readClients);
        }
    }

    @Test
    public void appendManyToExistingFileTest() throws IOException {
        File file = new File(tempFolder.getRoot(), "existing_file.json");
        FileWriter fw = new FileWriter(file);
        JSONMapper.Mapper.writeValue(fw, testClients.subList(0, 1));
        fw.close();

        try (JSONFileWriter<Client> clientJSONFileWriter = new JSONFileWriter<>(file, Client.class)) {
            clientJSONFileWriter.appendMany(testClients.subList(1, 3));
            clientJSONFileWriter.close();

            List<Client> readClients = readClientsFromFile(file);
            assertEquals(testClients, readClients);
        }
    }
}
