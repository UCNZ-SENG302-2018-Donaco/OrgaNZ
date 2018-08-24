package com.humanharvest.organz.commands.modify;

import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.serialisation.CSVReadClientStrategy;
import com.humanharvest.organz.utilities.serialisation.ClientImporter;
import com.humanharvest.organz.utilities.serialisation.JSONReadClientStrategy;
import com.humanharvest.organz.utilities.serialisation.ReadClientStrategy;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Command line to load the information of all the clients from a JSON file,
 */
@Command(name = "load", description = "Load clients from file", sortOptions = false)
public class Load implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Load.class.getName());

    private final ClientManager manager;
    private final PrintStream outputStream;

    @Option(names = {"-f", "-format"}, description = "File format")
    private String format;

    @Parameters(arity = "1", paramLabel = "FILE", description = "File to load.")
    private File file;

    public Load() {
        manager = State.getClientManager();
        outputStream = System.out;
    }

    public Load(ClientManager manager) {
        this.manager = manager;
        outputStream = System.out;
    }

    @Override
    public void run() {
        if (format == null) {
            format = getFileExtension(file.getName());
        }

        try {
            ReadClientStrategy strategy;
            switch (format) {
                case "csv":
                    strategy = new CSVReadClientStrategy();
                    break;
                case "json":
                    strategy = new JSONReadClientStrategy();
                    break;
                default:
                    throw new IOException(String.format("Unknown file format or extension: '%s'", format));
            }

            ClientImporter importer = new ClientImporter(file, strategy);
            importer.importAll();
            manager.setClients(importer.getValidClients());

            String errorSummary = importer.getErrorSummary();
            if (errorSummary.length() > 500) {
                errorSummary = errorSummary.substring(0, 500).concat("...");
            }

            String message = String.format("Loaded clients from file '%s'."
                            + "%n%d were valid, "
                            + "%n%d were invalid."
                            + "%n%n%s",
                    file.getName(), importer.getValidCount(), importer.getInvalidCount(), errorSummary);

            outputStream.println(message);
            //TODO: State.getSession().addToSessionHistory(new HistoryItem("LOAD", message));

        } catch (FileNotFoundException e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            outputStream.println(String.format("Could not find file: '%s'.", file.getAbsolutePath()));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            outputStream.println(String.format("An IO error occurred when loading from file: '%s'%n%s",
                    file.getName(), e.getMessage()));
        }
    }

    /**
     * Returns the file extension of the given file name string (in lowercase). The file extension is defined as the
     * characters after the last "." in the file name.
     *
     * @param fileName The file name string.
     * @return The file extension of the given file name.
     */
    private static String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex >= 0) {
            return fileName.substring(lastIndex + 1).toLowerCase();
        } else {
            return "";
        }
    }
}
