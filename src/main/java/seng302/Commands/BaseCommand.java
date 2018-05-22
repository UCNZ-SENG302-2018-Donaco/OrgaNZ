package seng302.Commands;

import seng302.Commands.Modify.*;
import seng302.Commands.View.GetChanges;
import seng302.Commands.View.PrintAllInfo;
import seng302.Commands.View.PrintAllOrgan;
import seng302.Commands.View.PrintClientInfo;
import seng302.Commands.View.PrintClientOrgan;

import picocli.CommandLine.Command;

/**
 * The main command hub used to access the other commands within the program such as save, help, createuser etc.
 */

@Command(name = "ClientCLI", description = "ClientCLI is a command based management tool for the team-700 ODMS.",
        subcommands = {
                CreateClient.class,
                SetAttribute.class,
                SetOrganStatus.class,
                DeleteClient.class,
                RequestOrgan.class,
                ResolveOrgan.class,
                PrintAllInfo.class,
                PrintAllOrgan.class,
                PrintClientInfo.class,
                PrintClientOrgan.class,
                GetChanges.class,
                Save.class,
                Load.class,
                Help.class,
                Undo.class,
                Redo.class
        })

public class BaseCommand implements Runnable {

    public void run() {
        System.out.println("Invalid command");
    }
}
