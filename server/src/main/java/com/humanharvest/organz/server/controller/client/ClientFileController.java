package com.humanharvest.organz.server.controller.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.serialisation.CSVReadClientStrategy;
import com.humanharvest.organz.utilities.serialisation.ClientImporter;
import com.humanharvest.organz.utilities.serialisation.JSONFileWriter;
import com.humanharvest.organz.utilities.serialisation.JSONReadClientStrategy;
import com.humanharvest.organz.utilities.serialisation.ReadClientStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides handlers for requests to these endpoints:
 * - GET /clients/file
 * - POST /clients/file
 * Both endpoints require administrator access.
 */
@RestController
public class ClientFileController {

    @GetMapping
    public ResponseEntity<byte[]> exportClients(
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException {

        // Check request has authorization to export all clients
        State.getAuthenticationManager().verifyAdminAccess(authToken);

        try {
            // Write all clients to a temporary file
            File tmpDataFile = File.createTempFile("tmp", null);
            try (JSONFileWriter<Client> clientWriter = new JSONFileWriter<>(tmpDataFile, Client.class)) {
                clientWriter.overwriteWith(State.getClientManager().getClients());
            }
            // Return the file's data
            byte[] fileData = Files.readAllBytes(tmpDataFile.toPath());
            return new ResponseEntity<>(fileData, HttpStatus.OK);

        } catch (IOException exc) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<String> importClients(
            @RequestBody byte[] data,
            @RequestHeader(value = "Content-Type") String mimeType,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException {

        // Check request has authorization to import clients
        State.getAuthenticationManager().verifyAdminAccess(authToken);

        try {
            // Write the data to a temporary file
            File tmpDataFile = File.createTempFile("tmp", null);
            FileOutputStream outputStream = new FileOutputStream(tmpDataFile);
            outputStream.write(data);
            outputStream.close();

            // Attempt to load data from the file using the given file type
            String message = loadData(tmpDataFile, mimeType);

            //State.getSession().addToSessionHistory(new HistoryItem("LOAD", message)); TODO figure out how

            // Return OK with message detailing how many clients were valid and reasons some were invalid
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (IOException exc) {
            // Return BAD_REQUEST with error message
            return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private String loadData(File file, String mimeType) throws IOException {
        ReadClientStrategy strategy;
        switch (mimeType) {
            case "text/csv":
                strategy = new CSVReadClientStrategy();
                break;
            case "application/json":
                strategy = new JSONReadClientStrategy();
                break;
            default:
                throw new IOException(String.format("Unknown file format: '%s'", mimeType));
        }

        ClientImporter importer = new ClientImporter(file, strategy);
        importer.importAll();

        // Add all valid clients to the system
        State.getClientManager().setClients(importer.getValidClients());

        String errorSummary = importer.getErrorSummary();
        if (errorSummary.length() > 500) {
            errorSummary = errorSummary.substring(0, 500).concat("...");
        }

        return String.format("Loaded clients from file."
                        + "\n%d were valid, "
                        + "\n%d were invalid."
                        + "\n\n%s",
                importer.getValidCount(), importer.getInvalidCount(), errorSummary);
    }
}
