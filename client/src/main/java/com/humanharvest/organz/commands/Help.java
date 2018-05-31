package com.humanharvest.organz.commands;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

@Command(name = "help", description = "Get help on commands.", sortOptions = false)
public class Help implements Runnable {

    @ParentCommand
    private BaseCommand parent;

    @Parameters(paramLabel = "command", description = "The command to display the usage help message for.")
    private String[] commands = new String[0];


    public void run() {
        CommandLine commandLine = new CommandLine(parent);
        if (commands.length > 0) {
            CommandLine subcommand = commandLine.getSubcommands().get(commands[0]);
            if (subcommand != null) {
                subcommand.usage(System.out);
            } else {
                System.err.println("Unknown subcommand '" + commands[0] + "'.");
                commandLine.usage(System.err);
            }
        } else {
            CommandLine.Help help = new CommandLine.Help(parent).addAllSubcommands(commandLine.getSubcommands());
            StringBuilder sb = new StringBuilder()
                    .append(help.headerHeading())
                    .append(help.header())
                    .append(help.synopsisHeading())      //e.g. Usage:
                    .append(help.synopsis(
                            help.synopsisHeadingLength()))             //e.g. &lt;main class&gt; [OPTIONS] &lt;command&gt; [COMMAND-OPTIONS] [ARGUMENTS]
                    .append(help.descriptionHeading())   //e.g. %nDescription:%n%n
                    .append(help
                            .description())          //e.g. {"Converts foos to bars.", "Use options to control conversion mode."}
                    .append(help.parameterListHeading()) //e.g. %nPositional parameters:%n%n
                    .append(help.parameterList())        //e.g. [FILE...] the files to convert
                    .append(help.optionListHeading())    //e.g. %nOptions:%n%n
                    .append(help.optionList())           //e.g. -h, --help   displays this help and exits
                    .append(help.commandListHeading())   //e.g. %nCommands:%n%n
                    .append('\n');

            for (CommandLine command : commandLine.getSubcommands().values()) {
                CommandLine.Help cmdHelp = new CommandLine.Help(command.getCommand());
                sb.append(cmdHelp.description());
                sb.append(cmdHelp.synopsis(cmdHelp.synopsisHeadingLength()));
                sb.append('\n');
            }
            sb.append(help.footerHeading())
                    .append(help.footer());
            System.out.print(sb);
        }
    }
}