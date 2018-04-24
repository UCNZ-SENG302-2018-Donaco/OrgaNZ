package seng302.Utilities;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seng302.Client;
import seng302.HistoryItem;
import seng302.State.ClientManager;
import seng302.State.State;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

/**
 * Uses GSON to convert Java objects into JSON files and from JSON files
 * to Java objects.
 */
public final class JSONConverter {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .create();

    private JSONConverter() {
    } // To ensure that this UTILITY class cannot be instantiated.

    /**
     * If the given file does not exist, creates an empty JSON array in that file.
     * If the given file does exist, does nothing.
     * @param file The file to check/create.
     * @throws IOException If an error occurs while creating the file.
     */
    public static void createEmptyJSONFileIfNotExists(File file) throws IOException {
        try {
            if (file.createNewFile()) {
                FileWriter writer = new FileWriter(file);
                writer.write("[]\n");
                writer.flush();
                writer.close();
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
    public static void saveToFile(File file) throws IOException {
        Writer writer = new FileWriter(file);
        ClientManager clientManager = State.getClientManager();
        gson.toJson(clientManager.getClients(), writer);
        writer.close();
    }

    /**
     * Loads the clients from a specified file. Overwrites any current clients
     * @param file The file to be loaded from
     * @throws IOException Throws IOExceptions
     */
    public static void loadFromFile(File file) throws IOException {
        Reader reader = new FileReader(file);
        ArrayList<Client> clients;
        Type collectionType = new TypeToken<ArrayList<Client>>() {
        }.getType();

        clients = gson.fromJson(reader, collectionType);
        ClientManager clientManager = State.getClientManager();
        clientManager.setClients(clients);

        for (Client client : clients) {
            if (client.getUid() >= clientManager.getUid()) {
                clientManager.setUid(client.getUid() + 1);
            }
        }
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
        try (JsonReader reader = new JsonReader(new FileReader(filename))) {
            HistoryItem[] historyItems = gson.fromJson(reader, HistoryItem[].class);
            ArrayList<HistoryItem> historyList = new ArrayList<>(Arrays.asList(historyItems));
            historyList.add(historyItem);

            writeHistoryToJSON(historyList, filename);

        } catch (IOException | IllegalStateException exc) {
            System.err.println(
                    "An error occurred when reading historyItem history from the JSON file: \n" + exc.getMessage());
        }
    }

    /**
     * Helper function for updateActionHistoryFromJSON; writes the historyHistoryItemList to a
     * JSON file.
     * @param filename The file to save the history to
     * @param historyHistoryItemList An ArrayList of all history the system has recorded.
     */
    private static void writeHistoryToJSON(ArrayList<HistoryItem> historyHistoryItemList, String filename) {
        try (Writer writer = new FileWriter(filename)) {
            gson.toJson(historyHistoryItemList, writer);
        } catch (IOException | IllegalStateException exc) {
            System.out.println(exc);
        }
    }

    public static List<HistoryItem> loadJSONtoHistory(File filename) throws IOException {
        Reader reader = new FileReader(filename);
        ArrayList<HistoryItem> historyItemList;
        Type historyCollection = new TypeToken<ArrayList<HistoryItem>>() {
        }.getType();
        historyItemList = gson.fromJson(reader, historyCollection);
        return historyItemList;
    }

}
