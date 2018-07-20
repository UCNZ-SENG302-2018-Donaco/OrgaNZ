package com.humanharvest.organz.serialization;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.utilities.serialization.JSONFileWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class JSONFileWriterTest {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .create();

    private final List<Client> testClients = Arrays.asList(
            new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1),
            new Client("Second", null, "Sur", LocalDate.of(2001, 5, 8), 2),
            new Client("Third", "Bobby", "Man", LocalDate.of(1996, 3, 4), 3)
    );

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private List<Client> readClientsFromFile(File file) throws Exception {
        JsonReader reader = new JsonReader(new FileReader(file));
        List<Client> readClients = gson.fromJson(reader, new TypeToken<List<Client>>() {}.getType());
        reader.close();

        return readClients;
    }

    @Test
    public void writeToNewFileTest() throws Exception {
        File file = new File(tempFolder.getRoot(), "new_file.json");
        JSONFileWriter<Client> ClientFileWriter = new JSONFileWriter<>(file, Client.class);
        ClientFileWriter.overwriteWith(testClients);
        ClientFileWriter.close();

        List<Client> readClients = readClientsFromFile(file);
        assertEquals(testClients, readClients);
    }

    @Test
    public void appendOneToExistingFileTest() throws Exception {
        File file = new File(tempFolder.getRoot(), "existing_file.json");
        FileWriter fw = new FileWriter(file);
        gson.toJson(testClients.subList(0, 2), TypeToken.getParameterized(List.class, Client.class).getType(), fw);
        fw.close();

        JSONFileWriter<Client> ClientFileWriter = new JSONFileWriter<>(file, Client.class);
        ClientFileWriter.appendOne(testClients.get(2));
        ClientFileWriter.close();

        List<Client> readClients = readClientsFromFile(file);
        assertEquals(testClients, readClients);
    }

    @Test
    public void appendManyToExistingFileTest() throws Exception {
        File file = new File(tempFolder.getRoot(), "existing_file.json");
        FileWriter fw = new FileWriter(file);
        gson.toJson(testClients.subList(0, 1), TypeToken.getParameterized(List.class, Client.class).getType(), fw);
        fw.close();

        JSONFileWriter<Client> ClientFileWriter = new JSONFileWriter<>(file, Client.class);
        ClientFileWriter.appendMany(testClients.subList(1, 3));
        ClientFileWriter.close();

        List<Client> readClients = readClientsFromFile(file);
        assertEquals(testClients, readClients);
    }
}
