package seng302;

import seng302.commands.Commands;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {

    public void parseCommand(String input) {
        ArrayList<String> inputs = new ArrayList<>();

        //Regex matcher that separates on space but allows for double quoted strings to be considered single strings
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(input);
        while (m.find())
            inputs.add(m.group(1));

        String command = inputs.get(0);
        inputs.remove(0);

        switch(command) {
            case "createuser": Commands.createuser(inputs);
                            break;
            case "help":    Commands.help(inputs);
                            break;
            default: System.out.println("Command not found");


        }
    }
}
