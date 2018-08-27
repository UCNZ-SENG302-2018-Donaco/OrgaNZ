package com.humanharvest.organz.utilities.serialisation;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.humanharvest.organz.Client;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

/**
 * An implementation of {@link ReadClientStrategy} that can be used for reading clients serialized to JSON.
 */
public class JSONReadClientStrategy implements ReadClientStrategy {

    private static final Logger LOGGER = Logger.getLogger(ClientImporter.class.getName());

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
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new InvalidObjectException(e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        jsonFileReader.close();
    }
}