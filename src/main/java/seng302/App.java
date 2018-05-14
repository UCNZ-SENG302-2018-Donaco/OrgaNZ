package seng302;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        LoggerSetup.enableConsole();

        State.init();

        String input;
        ConsoleScanner scanIn = new ConsoleScanner();

        BaseCommand command = new BaseCommand();

        CommandLine.usage(command, System.out);

        while (!(input = scanIn.readLine()).equals("exit")) {

            //Regex matcher that separates on space but allows for double quoted strings to be considered single strings
            ArrayList<String> inputs = new ArrayList<>();
            Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(input);
            while (m.find()) {
                inputs.add(m.group(1).replace("\"", ""));
            }
            String[] currArgs = inputs.toArray(new String[0]);
            CommandLine.run(command, System.out, currArgs);
        }
    }
}
