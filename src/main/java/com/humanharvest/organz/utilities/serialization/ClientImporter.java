package com.humanharvest.organz.utilities.serialization;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

import com.humanharvest.organz.Client;

/**
 * A class that can handle importing serialized clients from a file using a given {@link ReadClientStrategy}.
 * Provides functionality to count the number of valid and invalid records, and retrieve only the valid ones.
 */
public class ClientImporter {

    private final ReadClientStrategy readStrategy;

    private boolean imported = false;
    private long validCount = 0;
    private long invalidCount = 0;
    private List<Client> validClients = new ArrayList<>();

    public ClientImporter(File file, ReadClientStrategy readStrategy) throws IOException {
        this.readStrategy = readStrategy;
        readStrategy.setup(file);
    }

    /**
     * Imports all valid clients from the file using the given {@link ReadClientStrategy}. These valid clients can
     * then be retrieved using {@link #getValidClients()}.
     * @throws IOException If a critical error occurrs which ends reading of the file (invalid syntax or input stream
     * broken)
     */
    public void importAll() throws IOException {
        if (imported) {
            throw new IllegalStateException("Cannot import from the same file more than once.");
        }

        boolean finished = false;
        try {
            while (!finished) {
                try {
                    Client client = readStrategy.readNext();

                    if (client == null) {
                        // No more clients to read
                        finished = true;
                    } else {
                        // Client is fully validated, add to results
                        validCount++;
                        validClients.add(client);
                    }

                } catch (InvalidObjectException exc) {
                    // The client that was read was invalid
                    invalidCount++;
                }
            }
        } finally {
            readStrategy.close();
            imported = true;
        }
    }

    /**
     * Returns the number of records in this file that were parsed correctly. Should only be used after calling
     * {@link #importAll()}, otherwise will simply return 0 (as no records have been read yet).
     * @return The number of valid records in the file.
     */
    public long getValidCount() {
        return validCount;
    }

    /**
     * Returns the number of records in this file that were parsed incorrectly. Should only be used after calling
     * {@link #importAll()}, otherwise will simply return 0 (as no records have been read yet).
     * @return The number of invalid records in the file.
     */
    public long getInvalidCount() {
        return invalidCount;
    }

    /**
     * Returns the clients from this file that were parsed correctly. Should only be used after calling
     * {@link #importAll()}, otherwise will simply return an empty list (as no records have been read yet).
     * @return The list of valid clients from the file.
     */
    public List<Client> getValidClients() {
        return validClients;
    }
}
