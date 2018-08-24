package com.humanharvest.organz.commands;

import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.server.controller.client.ClientController;
import picocli.CommandLine;
import picocli.CommandLine.Help.Ansi;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CommandsHelper {

    private static final Logger LOGGER = Logger.getLogger(CommandsHelper.class.getName());

    private CommandsHelper() {
    }

    /**
     * Takes a string line in the form of a command, and returns the space separated items. Double
     * quoted strings are considered one item. Also allows double quotes to be escaped using a backslash
     *
     * @param input The string to parse
     * @return A string list of space separated items
     */
    public static String[] parseCommands(String input) {
        List<String> inputs = new ArrayList<>();

        if (input.endsWith("\r")) {
            input = input.substring(0, input.length() - 1);
        }
        String currentItem = "";
        boolean betweenQuotes = false;
        boolean lastCharWasBackSlash = false;

        for (char ch : input.toCharArray()) {
            if (ch == ' ' && !betweenQuotes) {
                if (Objects.equals(currentItem, "")) {
                    continue;
                }
                inputs.add(currentItem);
                currentItem = "";
            } else if (ch == '"') {
                //If the previous character was a backslash, the backslash is removed and the quote included in the
                // actual string rather than counting as a quote for a multi word string
                if (lastCharWasBackSlash) {
                    currentItem = currentItem.substring(0, currentItem.length() - 1);
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
        if (!Objects.equals(currentItem, "")) {
            inputs.add(currentItem.trim());
        }
        return inputs.toArray(new String[0]);
    }

    /**
     * Takes a string of command text and
     *
     * @param commands The string separated list of commands to execute
     * @param invoker  The ActionInvoker to apply changes to if applicable
     * @return The output of the command. This includes help and error text if applicable
     */
    public static String executeCommandAndReturnOutput(String[] commands, ActionInvoker invoker) {

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteStream);

        try {
            CommandLine.run(
                    BaseCommand.class,
                    new CommandFactory(printStream, invoker),
                    printStream,
                    printStream,
                    Ansi.AUTO,
                    commands);
        } catch (IllegalStateException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }

        return byteStream.toString();
    }
}
