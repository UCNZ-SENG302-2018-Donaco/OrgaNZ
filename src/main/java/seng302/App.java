package seng302;

import picocli.CommandLine;
import seng302.Commands.BaseCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App
{
    private static DonorManager donorManager;

    public static void main( String[] args )
    {


        String input;

        donorManager = new DonorManager();
        CommandHandler commandHandler = new CommandHandler(donorManager);

        Scanner sc = new Scanner(System.in);

        BaseCommand command = new BaseCommand();

        while (!(input = sc.nextLine()).equals("exit")) {

            //Regex matcher that separates on space but allows for double quoted strings to be considered single strings
            ArrayList<String> inputs = new ArrayList<>();
            Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(input);
            while (m.find()) {
                inputs.add(m.group(1).replace("\"", ""));
            }
            String[] currArgs = inputs.toArray(new String[0]);
            CommandLine.run(command, System.out, currArgs);

            //CommandLine c = new CommandLine(command).parse(currArgs).get(1);
            //System.out.println(c.getCommandName());
            //commandHandler.parseCommand(input);
        }
    }

    public static DonorManager getManager() {
        return donorManager;
    }
}
