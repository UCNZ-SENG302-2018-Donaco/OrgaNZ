package seng302.Utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import seng302.Action;
import seng302.Donor;
import seng302.DonorManager;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import static seng302.App.getManager;

/**
 * Uses GSON to convert Java objects into JSON files and from JSON files
 * to Java objects.
 */
public final class JSONConverter {
    private static final Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.enableComplexMapKeySerialization()
			.create();

    private JSONConverter() {} // To ensure that this UTILITY class cannot be instantiated.

	/**
	 * Saves the current donors list to a specified file
	 * @param file The file to be saved to
	 * @throws IOException Throws IOExceptions
	 */
	public static void saveToFile(File file) throws IOException {
		Writer writer = new FileWriter(file);
		DonorManager donorManager = getManager();
		System.out.println(donorManager.getDonors());
		gson.toJson(donorManager.getDonors(), writer);
		writer.close();
	}

	/**
	 * Loads the donors from a specified file. Overwrites any current donors
	 * @param file The file to be loaded from
	 * @throws IOException Throws IOExceptions
	 */
	public static void loadFromFile(File file) throws IOException {
		Reader reader = new FileReader(file);
		ArrayList<Donor> donors;
		Type collectionType = new TypeToken<ArrayList<Donor>>() {}.getType();

		donors = gson.fromJson(reader, collectionType);
		DonorManager donorManager = getManager();
		donorManager.setDonors(donors);

		for (Donor donor : donors) {
			if (donor.getUid() >= donorManager.getUid()) {
				donorManager.setUid(donor.getUid() + 1);
			}
		}
	}


    /**
     * Read's the action_history.json file into an ArrayList, appends the action to the list and
     * calls the writeActionHistoryToJSON to save the update.
     * @param action The Action to add to the JSON history file.
     */
    public static void updateActionHistory(Action action, String filename) {
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
            Action[] actionHistory = gson.fromJson(reader, Action[].class);
            ArrayList<Action> actionHistoryList = new ArrayList<>(Arrays.asList(actionHistory));
            actionHistoryList.add(action);

            writeActionHistory(actionHistoryList, filename);

        } catch (IOException | IllegalStateException exc) {
            System.out.println("An error occurred when reading action history from the JSON file: \n" + exc.getMessage());
        }
    }

    /**
     * Helper function for updateActionHistoryFromJSON; writes the actionHistoryList to a
     * JSON file.
     * @param actionHistoryList An ArrayList of all history the system has recorded.
     */
    private static void writeActionHistory(ArrayList<Action> actionHistoryList, String filename) {
        try (Writer writer = new FileWriter(filename)) {
            gson.toJson(actionHistoryList, writer);
        } catch (IOException | IllegalStateException exc) {
            System.out.println(exc);
        }
    }
}
