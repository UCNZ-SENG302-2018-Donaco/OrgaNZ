package seng302.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {

    /**
     * Takes a string line in the form of a command, and returns the space separated items. Double
     * quoted strings are considered one item. Also allows double quotes to be escaped using a backslash
     * @param input The string to parse
     * @return A string list of space separated items
     */
    public static String[] parseCommands(String input) {
        List<String> inputs = new ArrayList<>();

        String currentItem = "";
        boolean betweenQuotes = false;
        boolean lastCharWasBackSlash = false;

        for (char ch : input.toCharArray()) {
            if ((ch == ' ') && !betweenQuotes) {
                if (currentItem.equals("")) {
                    continue;
                }
                inputs.add(currentItem);
                currentItem = "";
            } else if (ch == '"') {
                //If the previous character was a backslash, the backslash is removed and the quote included in the
                // actual string rather than counting as a quote for a multi word string
                if (lastCharWasBackSlash) {
                    currentItem = currentItem.substring(0, currentItem.length() -1);
                    currentItem += ch;
                    continue;
                }
                if (betweenQuotes) {
                    betweenQuotes = false;
                    inputs.add(currentItem);
                    currentItem = "";
                } else {
                    betweenQuotes = true;
                }
            } else {
                currentItem += ch;
            }
            lastCharWasBackSlash = ch == '\\';
        }
        if (!currentItem.equals("")) {
            inputs.add(currentItem);
        }
        return inputs.toArray(new String[0]);
    }

}
