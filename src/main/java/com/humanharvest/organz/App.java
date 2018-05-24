package com.humanharvest.organz;

import static com.humanharvest.organz.commands.CommandParser.parseCommands;

import java.util.logging.Level;

import com.humanharvest.organz.commands.BaseCommand;

import com.humanharvest.organz.state.State;
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
