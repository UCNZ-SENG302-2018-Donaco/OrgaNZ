package seng302;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import seng302.Utilities.JSONConverter;

import com.google.gson.stream.JsonReader;

public abstract class HistoryManager {
    public static HistoryManager INSTANCE = new HistoryManagerImpl();

    private static final String HISTORY_FILE = "action_history.json";

    public static void createTestManager() {
        INSTANCE = new HistoryManager() {
            private List<HistoryItem> historyItems = new ArrayList<>();

            @Override
            public void updateHistory(HistoryItem historyItem) {
                historyItems.add(historyItem);
            }

            @Override
            public List<HistoryItem> loadHistory() {
                return Collections.unmodifiableList(historyItems);
            }
        };
    }

    /**
     * Updates the history list with a new history item.
     */
    public abstract void updateHistory(HistoryItem historyItem);

    /**
     * Returns an immutable list of all the history items.
     */
    public abstract List<HistoryItem> loadHistory();

    private static class HistoryManagerImpl extends HistoryManager {
        private List<HistoryItem> historyItems;

        @Override
        public void updateHistory(HistoryItem historyItem) {
            loadHistoryFile();
            historyItems.add(historyItem);
            saveHistoryFile();
        }

        @Override
        public List<HistoryItem> loadHistory() {
            loadHistoryFile();
            return Collections.unmodifiableList(historyItems);
        }

        private void loadHistoryFile() {
            if (historyItems == null) {
                File historyFile = new File(HISTORY_FILE);
                try {
                    JSONConverter.createEmptyJSONFileIfNotExists(historyFile);

                    try (JsonReader reader = new JsonReader(new FileReader(historyFile))) {
                        historyItems = new ArrayList<>(
                                Arrays.asList(JSONConverter.getGson().fromJson(reader, HistoryItem[].class)));
                    }
                } catch (IOException | IllegalStateException exc) {
                    throw new IllegalArgumentException("An error occurred when reading historyItem history from the "
                            + "JSON file", exc);
                }
            }
        }

        private void saveHistoryFile() {
            try (Writer writer = new FileWriter(HISTORY_FILE)) {
                JSONConverter.getGson().toJson(historyItems, writer);
            } catch (IOException | IllegalStateException exc) {
                throw new IllegalArgumentException(exc);
            }
        }
    }
}
