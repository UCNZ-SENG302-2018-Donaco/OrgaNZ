package seng302;

import static seng302.Commands.CommandParser.parseCommands;

import java.util.logging.Level;

import seng302.Commands.BaseCommand;
import seng302.State.State;
import seng302.Utilities.ConsoleScanner;
import seng302.Utilities.LoggerSetup;

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

        State.init();

        String input;
        ConsoleScanner scanIn = new ConsoleScanner();

        BaseCommand command = new BaseCommand();

        CommandLine.usage(command, System.out);

        while (!(input = scanIn.readLine()).equals("exit")) {

            CommandLine.run(command, System.out, parseCommands(input));
        }
    }
}
