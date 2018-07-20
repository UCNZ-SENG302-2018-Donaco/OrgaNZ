package com.humanharvest.organz;

import static com.humanharvest.organz.commands.CommandParser.parseCommands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.humanharvest.organz.commands.BaseCommand;

import com.humanharvest.organz.state.State;
import com.humanharvest.organz.state.State.DataStorageType;
import com.humanharvest.organz.utilities.ConsoleScanner;
import com.humanharvest.organz.utilities.LoggerSetup;

import picocli.CommandLine;

/**
 * The main class that runs the whole program. Calls the Base command and the Client manager.
 * @author Dylan Carlyle, Jack Steel, Alex Tompkins, James Toohey
 * @version sprint 2.
 * date: 2018-03-22
 */
public class App {

    public static void main(String[] args) {
        LoggerSetup.setup(Level.INFO);
        LoggerSetup.enableConsole(Level.WARNING);

        Map<String, String> namedArgs = new HashMap<>();
        for (String arg : args) {
            if (arg.contains("=")) {
                namedArgs.put(arg.substring(0, arg.indexOf('=')), arg.substring(arg.indexOf('=') + 1));
            }
        }

        try {
            String storageArg = namedArgs.get("storage");
            DataStorageType storageType;

            if (storageArg == null) {
                storageType = DataStorageType.PUREDB;
            } else {
                storageType = DataStorageType.valueOf(storageArg);
            }

            State.init(storageType);

        } catch (IllegalArgumentException exc) {
            System.err.println(String.format(
                    "FATAL: invalid argument for storage. Valid arguments are: %s",
                    Arrays.stream(DataStorageType.values())
                            .map(Enum::toString)
                            .collect(Collectors.joining(", "))
            ));
            System.exit(1);
        }

        String input;
        ConsoleScanner scanIn = new ConsoleScanner();

        BaseCommand command = new BaseCommand();

        CommandLine.usage(command, System.out);

        while (!(input = scanIn.readLine()).equals("exit")) {

            CommandLine.run(command, System.out, parseCommands(input));
        }
    }
}
