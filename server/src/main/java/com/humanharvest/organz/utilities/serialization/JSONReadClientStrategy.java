package com.humanharvest.organz.utilities.serialization;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.humanharvest.organz.Client;

/**
 * An implementation of {@link ReadClientStrategy} that can be used for reading clients serialized to JSON.
 */
public class JSONReadClientStrategy implements ReadClientStrategy {

    private JSONFileReader<Client> jsonFileReader;

    @Override
    public void setup(File inputFile) throws IOException {
        jsonFileReader = new JSONFileReader<>(inputFile, Client.class);
        try {
            jsonFileReader.startStream();
        } catch (IllegalStateException exc) {
            // Error if JSON file doesn't start with '{'
            throw new IOException(exc);
        }
    }

    @Override
    public Client readNext() throws InvalidObjectException, IOException {
        try {
            return jsonFileReader.getNext();
        } catch (JsonSyntaxException exc) {
            throw new InvalidObjectException(exc.getMessage());
        } catch (JsonIOException exc) {
            throw new IOException(exc);
        }
    }

    @Override
    public void close() throws IOException {
        jsonFileReader.close();
    }
}
