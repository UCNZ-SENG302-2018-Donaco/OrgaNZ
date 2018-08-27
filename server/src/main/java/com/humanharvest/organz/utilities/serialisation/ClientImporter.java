package com.humanharvest.organz.utilities.serialisation;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.validators.ClientValidator;

/**
 * A class that can handle importing serialized clients from a file using a given {@link ReadClientStrategy}.
 * Provides functionality to count the number of valid and invalid records, and retrieve only the valid ones.
 */
public class ClientImporter {

    private static final Logger LOGGER = Logger.getLogger(ClientImporter.class.getName());

    private final ReadClientStrategy readStrategy;

    private final StringBuilder errorSummary = new StringBuilder();
    private final List<Client> validClients = new ArrayList<>();

    private boolean imported;
    private long validCount;
    private long invalidCount;

    public ClientImporter(File file, ReadClientStrategy readStrategy) throws IOException {
        this.readStrategy = readStrategy;
        readStrategy.setup(file);
    }

    /**
     * Sets the given client as "owner" on all records belonging to that client, such as {@link TransplantRequest}s and
     * {@link MedicationRecord}s. This is necessary because the relationships are referenced from both sides, but are
     * only serialized in terms of clients "owning" records (to avoid infinite recursion in serialized form).
     *
     * @param client The client to set as "owner" for all their records.
     */
    private static void setOwnerOnRelatedRecords(Client client) {
        for (TransplantRequest request : client.getTransplantRequests()) {
            request.setClient(client);
        }
        for (IllnessRecord record : client.getIllnesses()) {
            record.setClient(client);
        }
        for (ProcedureRecord record : client.getProcedures()) {
            record.setClient(client);
        }
        for (MedicationRecord record : client.getMedications()) {
            record.setClient(client);
        }
    }

    /**
     * Imports all valid clients from the file using the given {@link ReadClientStrategy}. These valid clients can
     * then be retrieved using {@link #getValidClients()}.
     *
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
                        // Check that retrieved client was valid
                        String errors = ClientValidator.validate(client);
                        if (errors != null) {
                            throw new InvalidObjectException(errors);
                        }

                        // Client is fully validated, add to results
                        setOwnerOnRelatedRecords(client);
                        validCount++;
                        validClients.add(client);
                    }

                } catch (InvalidObjectException e) {
                    // The client that was read was invalid
                    LOGGER.log(Level.WARNING, e.getMessage(), e);
                    invalidCount++;
                    errorSummary.append(String.format("Record #%d was invalid because: %n%s%n",
                            validCount + invalidCount, e.getMessage()));
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
     *
     * @return The number of valid records in the file.
     */
    public long getValidCount() {
        return validCount;
    }

    /**
     * Returns the number of records in this file that were parsed incorrectly. Should only be used after calling
     * {@link #importAll()}, otherwise will simply return 0 (as no records have been read yet).
     *
     * @return The number of invalid records in the file.
     */
    public long getInvalidCount() {
        return invalidCount;
    }

    /**
     * Returns the clients from this file that were parsed correctly. Should only be used after calling
     * {@link #importAll()}, otherwise will simply return an empty list (as no records have been read yet).
     *
     * @return The list of valid clients from the file.
     */
    public List<Client> getValidClients() {
        return validClients;
    }

    /**
     * Returns a summary of all the errors that were found when importing clients from the file.
     *
     * @return A string containing a summary of all the errors found when parsing the file. May be empty if there
     * were no errors, or if {@link #importAll()} has not yet been called.
     */
    public String getErrorSummary() {
        return errorSummary.toString();
    }
}
