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
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.JSONConverter;
import com.humanharvest.organz.utilities.serialization.CSVReadClientStrategy;
import com.humanharvest.organz.utilities.serialization.JSONFileReader;
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
            switch (format) {
                case "csv":
                    loadCsv(fileName);
                    break;
                case "json":
                    loadJson(fileName);
                    break;
                default:
                    System.out.println("Unknown file format or extension: " + format);
                    break;
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
     * Loads Clients from the given JSON file.
     */
    private void loadJson(File file) throws IOException {
        List<Client> clients;

        try (JSONFileReader<Client> clientReader = new JSONFileReader<>(file, Client.class)) {
            clients = clientReader.getAll();

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

            manager.setClients(clients);
        }

        System.out.println(String.format("%d clients loaded from JSON file.", clients.size()));
    }

    /**
     * Loads Clients from the given CSV file.
     */
    private void loadCsv(File file) throws IOException {
        int valid = 0;
        int invalid = 0;

        try (CSVParser parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(new FileReader(file))) {
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
                        "\n%d records were valid," +
                        "\n%d records were invalid.",
                valid, invalid));
    }

    private static String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex >= 0) {
            return fileName.substring(lastIndex + 1).toLowerCase();
        } else {
            return "";
        }
    }
}