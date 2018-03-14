package seng302.Utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import seng302.HistoryItem;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Uses GSON to convert Java objects into JSON files and from JSON files
 * to Java objects.
 */
public final class JSONConverter {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private JSONConverter() {} // To ensure that this UTILITY class cannot be instantiated.


    /**
     * Read's the action_history.json file into an ArrayList, appends the historyItem to the list and
     * calls the writeHistoryToJSON to save the update.
     * @param historyItem The HistoryItem to add to the JSON history file.
     */
    public static void updateHistory(HistoryItem historyItem, String filename) {
        File historyFile = new File(filename);
        try {
            if (historyFile.createNewFile()) {
                FileWriter historyWriter = new FileWriter(historyFile);
                historyWriter.write("[]\n");
                historyWriter.flush();
                historyWriter.close();
            }
        } catch (IOException exc) {
            System.out.println("An error occurred when creating the history file: " + exc.getMessage());
        }
        try (JsonReader reader = new JsonReader(new FileReader(filename))) {
            HistoryItem[] historyItems = gson.fromJson(reader, HistoryItem[].class);
            ArrayList<HistoryItem> historyList = new ArrayList<>(Arrays.asList(historyItems));
            historyList.add(historyItem);

            writeHistoryToJSON(historyList, filename);

        } catch (IOException | IllegalStateException exc) {
            System.out.println("An error occurred when reading historyItem history from the JSON file: \n" + exc.getMessage());
        }
    }

    /**
     * Helper function for updateActionHistoryFromJSON; writes the historyHistoryItemList to a
     * JSON file.
     * @param historyHistoryItemList An ArrayList of all history the system has recorded.
     */
    private static void writeHistoryToJSON(ArrayList<HistoryItem> historyHistoryItemList, String filename) {
        try (Writer writer = new FileWriter(filename)) {
            gson.toJson(historyHistoryItemList, writer);
        } catch (IOException | IllegalStateException exc) {
            System.out.println(exc);
        }
    }
}
