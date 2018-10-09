package com.humanharvest.organz.server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.humanharvest.organz.state.State;
import com.humanharvest.organz.state.State.DataStorageType;
import com.humanharvest.organz.utilities.LoggerSetup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main class for the Spring Boot web server.
 */
@SpringBootApplication
public class Application {

    /**
     * The main method for the server - handles all setup and runs the application.
     *
     * @param args The arguments given to the server.
     */
    public static void main(String[] args) {
        // Retrieve named arguments
        Map<String, String> namedArgs = new HashMap<>();
        for (String arg : args) {
            if (arg.contains("=")) {
                namedArgs.put(arg.substring(0, arg.indexOf('=')), arg.substring(arg.indexOf('=') + 1));
            }
        }

        // Initialize storage with storage argument
        initStorage(namedArgs.get("storage"));

        String imageDirectory = namedArgs.get("imagedir");
        if (imageDirectory != null && !imageDirectory.isEmpty()) {
            State.setImageDirectory(imageDirectory);
        }

        LoggerSetup.setup("organz-server.log", Level.INFO);

        // Run Spring Boot Application (server)
        SpringApplication.run(Application.class, args);
    }

    /**
     * Determines which storage type should be used (from the argument given to the server), and initalizes the
     * {@link State} of the server using that. The default storage type is {@link DataStorageType#MEMORY}.
     *
     * @param storageArg The value of the storage argument given to the server.
     */
    private static void initStorage(String storageArg) {
        try {
            DataStorageType storageType;
            if (storageArg == null) {
                storageType = DataStorageType.PUREDB;
            } else {
                storageType = DataStorageType.valueOf(storageArg);
            }
            State.init(storageType);

        } catch (IllegalArgumentException exc) {
            System.err.println(String.format(
                    "FATAL: '%s' is an invalid argument for storage. Valid arguments are: %s",
                    storageArg,
                    Arrays.stream(DataStorageType.values())
                            .map(Enum::toString)
                            .collect(Collectors.joining(", "))
            ));
            System.exit(1);
        }
    }
}
