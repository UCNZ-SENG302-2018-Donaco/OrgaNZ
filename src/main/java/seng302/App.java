package seng302;

import picocli.CommandLine;
import seng302.Actions.ActionInvoker;
import seng302.Commands.BaseCommand;
import seng302.Utilities.ConsoleScanner;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The main class that runs the whole program. Calls the Base command and the Donor manager.
 *
 *@author Dylan Carlyle, Jack Steel
 *@version sprint 1.
 *date 06/03/2018
 */

public class App
{
    private static DonorManager donorManager;
    private static ActionInvoker actionInvoker;

    public static void main( String[] args )
    {
        String input;

        actionInvoker = new ActionInvoker();
        donorManager = new DonorManager();

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

    public static DonorManager getManager() {
        return donorManager;
    }

    public static ActionInvoker getInvoker() {
        return actionInvoker;
    }
}
