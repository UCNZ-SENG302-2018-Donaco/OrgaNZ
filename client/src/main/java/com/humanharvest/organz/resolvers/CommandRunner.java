package com.humanharvest.organz.resolvers;

@FunctionalInterface
public interface CommandRunner {

    /**
     * Executes a command.
     *
     * @param commandText The text of the command to execute.
     * @return The output of the command.
     */
    String execute(String commandText);
}
