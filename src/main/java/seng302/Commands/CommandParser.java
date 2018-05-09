package seng302.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {

    /**
     * Takes a string line in the form of a command, and returns the space separated items. Double
     * quoted strings are considered one item
     * @param input The string to parse
     * @return A string list of space separated items
     */
    public static String[] parseCommands(String input) {
        List<String> inputs = new ArrayList<>();
        //Regex matcher that separates on space but allows for double quoted strings to be considered single strings
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(input);
        while (m.find()) {
            inputs.add(m.group(1).replace("\"", ""));
        }
        return inputs.toArray(new String[0]);
    }

}
