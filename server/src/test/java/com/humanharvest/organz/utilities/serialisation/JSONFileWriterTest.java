package com.humanharvest.organz.utilities.serialisation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.humanharvest.organz.Client;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JSONFileWriterTest {

    private final List<Client> testClients = Arrays.asList(
            new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1),
            new Client("Second", null, "Sur", LocalDate.of(2001, 5, 8), 2),
            new Client("Third", "Bobby", "Man", LocalDate.of(1996, 3, 4), 3)
    );

//    @Rule
//    public TemporaryFolder tempFolder = new TemporaryFolder();

    private static List<Client> readClientsFromFile(InputStream inputStream) throws IOException {
        TypeReference<ArrayList<Client>> type = new TypeReference<ArrayList<Client>>() {
        };
        return JSONMapper.Mapper.readValue(inputStream, type);
    }

    @Test
    public void writeToNewFileTest() throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            try (JSONFileWriter<Client> fileWriter = new JSONFileWriter<>(output)) {
                fileWriter.overrideWith(testClients);
            }

            try (InputStream input = new ByteArrayInputStream(output.toByteArray())) {
                List<Client> readClients = readClientsFromFile(input);
                Assertions.assertEquals(testClients, readClients);
            }
        }
    }
}
