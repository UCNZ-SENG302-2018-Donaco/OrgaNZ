package com.humanharvest.organz.utilities.serialisation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.humanharvest.organz.Client;

/**
 * An implementation of {@link ReadClientStrategy} that can be used for reading clients serialized to JSON.
 */
public class JSONReadClientStrategy implements ReadClientStrategy {

    private Iterator<Client> clients;

    @Override
    public void setup(File inputFile) throws IOException {
        TypeReference<ArrayList<Client>> type = new TypeReference<ArrayList<Client>>() {
        };
        clients = JSONMapper.Mapper.<ArrayList<Client>>readValue(inputFile, type).iterator();
    }

    @Override
    public Client readNext() {
        if (clients.hasNext()) {
            return clients.next();
        }
        return null;
    }

    @Override
    public void close() {
    }

    public List<Client> readAll() {
        List<Client> result = new ArrayList<>();
        while (true) {
            Client client = readNext();
            if (client == null) {
                break;
            }

            result.add(client);
        }
        return result;
    }
}