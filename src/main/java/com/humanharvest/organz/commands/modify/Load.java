package com.humanharvest.organz.commands.modify;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.JSONConverter;
import com.humanharvest.organz.utilities.serialization.CSVReadClientStrategy;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command line to load the information of all the clients from a JSON file,
 */
@Command(name = "load", description = "Load clients from file", sortOptions = false)
public class Load implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Load.class.getName());

    private final ClientManager manager;

    @Option(names = {"-f", "-format"}, description = "File format")
    private String format;

    @Parameters(arity = "1", paramLabel = "FILE", description = "File to load.")
    private File fileName;

    public Load() {
        manager = State.getClientManager();
    }

    public Load(ClientManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        if (format == null) {
            format = getFileExtension(fileName.getName());
        }

        try {
            if ("csv".equalsIgnoreCase(format)) {
                loadCsv();
            } else if ("json".equalsIgnoreCase(format)) {
                loadJson();
            } else {
                System.out.println("Unknown file format or extension: " + format);
            }

            LOGGER.log(Level.INFO, String.format("Loaded %s clients from file", manager.getClients().size()));
            HistoryItem load = new HistoryItem("LOAD",
                    "The systems state was loaded from " + fileName.getAbsolutePath());
            JSONConverter.updateHistory(load, "action_history.json");
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.WARNING, "Could not find file: " + fileName.getAbsolutePath(), e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not load from file: " + fileName.getAbsolutePath(), e);
        }
    }

    /**
     * Loads a json file from the stored fileName field.
     */
    private void loadJson() throws IOException {
        JSONConverter.loadFromFile(fileName);
    }

    /**
     * Loads a csv file from the stored fileName field.
     */
    private void loadCsv() throws IOException {
        int valid = 0;
        int invalid = 0;

        try (CSVParser parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(new FileReader(fileName))) {
            CSVReadClientStrategy strategy = new CSVReadClientStrategy();
            List<Client> clients = new ArrayList<>();

            for (CSVRecord record : parser) {
                try {
                    clients.add(strategy.deserialise(record));
                    valid++;
                } catch (IllegalArgumentException exc) {
                    invalid++;
                }
            }
            manager.setClients(clients);
        }

        System.out.println(String.format(
                "Clients loaded from CSV file:" +
                        "\n%d were valid," +
                        "\n%d were invalid.",
                valid, invalid));
    }

    private static String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex >= 0) {
            return fileName.substring(lastIndex + 1);
        }
        return "";
    }
}