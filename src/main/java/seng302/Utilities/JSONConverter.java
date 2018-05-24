package seng302.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import seng302.Client;
import seng302.HistoryItem;
import seng302.IllnessRecord;
import seng302.MedicationRecord;
import seng302.ProcedureRecord;
import seng302.State.ClientManager;
import seng302.State.State;
import seng302.TransplantRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

/**
 * Uses GSON to convert Java objects into JSON files and from JSON files
 * to Java objects.
 */
public final class JSONConverter {

    private static final Logger LOGGER = Logger.getLogger(JSONConverter.class.getName());

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
        try(OutputStream outputStream = new FileOutputStream(file)) {
            try(Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                ClientManager clientManager = State.getClientManager();
                gson.toJson(clientManager.getClients(), writer);
            }
        }
    }

    /**
     * Loads the clients from a specified file. Overwrites any current clients
     * @param file The file to be loaded from
     * @throws IOException Throws IOExceptions
     */
    public static void loadFromFile(File file) throws IOException {
        try(InputStream fileStream = new FileInputStream(file)) {
            try(Reader reader = new InputStreamReader(fileStream, StandardCharsets.UTF_8)) {
                try {
                    ArrayList<Client> clients;
                    Type collectionType = new TypeToken<ArrayList<Client>>() {
                    }.getType();

                    clients = gson.fromJson(reader, collectionType);
                    for (Client client : clients) {
                        for (TransplantRequest request : client.getTransplantRequests()) {
                            request.setClient(client);
                        }
                        for (IllnessRecord record : client.getCurrentIllnesses()) {
                            record.setClient(client);
                        }
                        for (IllnessRecord record : client.getPastIllnesses()) {
                            record.setClient(client);
                        }
                        for (ProcedureRecord record : client.getPastProcedures()) {
                            record.setClient(client);
                        }
                        for (ProcedureRecord record : client.getPendingProcedures()) {
                            record.setClient(client);
                        }
                        for (MedicationRecord record : client.getCurrentMedications()) {
                            record.setClient(client);
                        }
                        for (MedicationRecord record : client.getPastMedications()) {
                            record.setClient(client);
                        }
                    }
                    ClientManager clientManager = State.getClientManager();
                    clientManager.setClients(clients);
                }  catch (JsonSyntaxException e) {
                    throw new IllegalArgumentException("Not a valid json file", e);
                }
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
            LOGGER.severe("Error writing history to JSON");
            LOGGER.severe(exc.getMessage());
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
