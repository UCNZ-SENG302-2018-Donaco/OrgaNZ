package com.humanharvest.organz.utilities.serialisation;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.humanharvest.organz.Client;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;

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
    public Client readNext() throws IOException {
        try {
            return jsonFileReader.getNext();
        } catch (InvalidFormatException e) {
            throw new InvalidObjectException(e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        jsonFileReader.close();
    }
}