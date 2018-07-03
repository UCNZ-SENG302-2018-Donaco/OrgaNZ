package com.humanharvest.organz.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.HistoryItem;

/**
 * Uses GSON to convert Java objects into JSON files and from JSON files
 * to Java objects.
 */
public final class JSONConverter {

    private static final Logger LOGGER = Logger.getLogger(JSONConverter.class.getName());

    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setVisibility(PropertyAccessor.ALL, Visibility.NONE)
            .setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());

    private JSONConverter() {
        // To ensure that this UTILITY class cannot be instantiated.
    }

    /**
     * If the given file does not exist, creates an empty JSON array in that file.
     * If the given file does exist, does nothing.
     * @param file The file to check/create.
     * @throws IOException If an error occurs while creating the file.
     */
    public static void createEmptyJSONFileIfNotExists(File file) throws IOException {
        try {
            if (file.createNewFile()) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("[]\n");
                    writer.flush();
                }
            }
        } catch (IOException exc) {
            throw new IOException(String.format("An error occurred when creating this file: %s\n%s",
                    file.getName(), exc.getMessage()));
        }
    }

    /**
     * Saves the current clients list to a specified file
     * @param file The file to be saved to
     * @throws IOException Throws IOExceptions
     */
    //TODO: Fix this to use a list of clients not clientManager or something
    public static void saveToFile(File file) throws IOException {
//        ClientManager clientManager = State.getClientManager();
//        try {
//            mapper.writeValue(file, clientManager.getClients());
//        } catch (IOException e) {
//            LOGGER.severe(e.getMessage());
//            throw e;
//        }
    }

    /**
     * Loads the clients from a specified file. Overwrites any current clients
     * @param file The file to be loaded from
     * @throws IOException Throws IOExceptions
     */
    //TODO: Fix this to use a list of clients not clientManager or something
    public static void loadFromFile(File file) throws IOException {
//        TypeReference type = new TypeReference<ArrayList<Client>>() {
//        };
//        try {
//            ArrayList<Client> clients = mapper.readValue(file, type);
//            ClientManager clientManager = State.getClientManager();
//            clientManager.setClients(clients);
//        } catch (JsonParseException | JsonMappingException e) {
//            //Only a warning as an invalid file could have been selected by the user
//            LOGGER.warning(e.getMessage());
//            throw new IllegalArgumentException("Not a valid json file", e);
//        }
    }

    /**
     * Read's the action_history.json file into an ArrayList, appends the historyItem to the list and
     * calls the writeHistoryToJSON to save the update.
     * @param historyItem The HistoryItem to add to the JSON history file.
     * @param filename The file location to be saved to
     */
    public static void updateHistory(HistoryItem historyItem, String filename) {
        File historyFile = new File(filename);
        try {
            createEmptyJSONFileIfNotExists(historyFile);
        } catch (IOException exc) {
            System.err.println(exc.getMessage());
        }

        try {
            HistoryItem[] historyItems = mapper.readValue(historyFile, HistoryItem[].class);
            ArrayList<HistoryItem> historyList = new ArrayList<>(Arrays.asList(historyItems));
            historyList.add(historyItem);

            writeHistoryToJSON(historyList, filename);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "An error occurred when reading historyItem history from the JSON file", e);
        }
    }

    /**
     * Helper function for updateActionHistoryFromJSON; writes the historyHistoryItemList to a
     * JSON file.
     * @param filename The file to save the history to
     * @param historyItemList An ArrayList of all history the system has recorded.
     */
    private static void writeHistoryToJSON(ArrayList<HistoryItem> historyItemList, String filename) {
        File historyFile = new File(filename);
        try {
            mapper.writeValue(historyFile, historyItemList);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing history to JSON", e);
        }
    }

    public static List<HistoryItem> loadJSONtoHistory(File filename) throws IOException {
        TypeReference type = new TypeReference<ArrayList<HistoryItem>>() {
        };
        return mapper.<ArrayList<HistoryItem>>readValue(filename, type);
    }

    public static ObjectMapper getObjectMapper() {
        return mapper;
    }
}
