package com.humanharvest.organz.utilities.serialisation;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientImporterTest {
    @Test
    public void testImportJson() throws IOException {
        ClientImporter importer = new ClientImporter(new File("src/test/resources/test.json"),
                new JSONReadClientStrategy());
        importer.importAll();
        assertEquals(1, importer.getValidCount());
        assertEquals(0, importer.getInvalidCount());
        assertEquals("", importer.getErrorSummary());
        assertEquals(1, importer.getValidClients().size());
    }

    @Test
    public void testImportCsv() throws IOException {
        ClientImporter importer = new ClientImporter(new File("src/test/resources/test.csv"),
                new CSVReadClientStrategy());
        importer.importAll();
        assertEquals(1, importer.getValidCount());
        assertEquals(0, importer.getInvalidCount());
        assertEquals("", importer.getErrorSummary());
        assertEquals(1, importer.getValidClients().size());
    }
}
