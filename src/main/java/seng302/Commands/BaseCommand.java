package seng302.Commands;

import seng302.Commands.Modify.*;
import seng302.Commands.View.*;

import picocli.CommandLine.Command;

/**
 * The main command hub used to access the other commands within the program such as save, help, createclient etc.
 */
@Command(name = "OrgaNZ",
        description = "OrgaNZ is a command based management tool for the team-700 ODMS.",
        subcommands = {
                CreateClient.class,
                CreateClinician.class,
                SetAttribute.class,
                ModifyClinician.class,
                SetOrganStatus.class,
                DeleteClient.class,
                DeleteClinician.class,
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
