package com.humanharvest.organz.commands;

import java.io.PrintStream;

import com.humanharvest.organz.commands.modify.CreateClient;
import com.humanharvest.organz.commands.modify.CreateClinician;
import com.humanharvest.organz.commands.modify.DeleteClient;
import com.humanharvest.organz.commands.modify.DeleteClinician;
import com.humanharvest.organz.commands.modify.Load;
import com.humanharvest.organz.commands.modify.ModifyClinician;
import com.humanharvest.organz.commands.modify.Redo;
import com.humanharvest.organz.commands.modify.RequestOrgan;
import com.humanharvest.organz.commands.modify.ResolveOrgan;
import com.humanharvest.organz.commands.modify.Save;
import com.humanharvest.organz.commands.modify.SetAttribute;
import com.humanharvest.organz.commands.modify.SetOrganStatus;
import com.humanharvest.organz.commands.modify.Undo;
import com.humanharvest.organz.commands.view.GetChanges;
import com.humanharvest.organz.commands.view.PrintAllInfo;
import com.humanharvest.organz.commands.view.PrintAllOrgan;
import com.humanharvest.organz.commands.view.PrintClientInfo;
import com.humanharvest.organz.commands.view.PrintClientOrgan;
import com.humanharvest.organz.commands.view.SQL;

import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;

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
                HelpCommand.class,
                Undo.class,
                Redo.class,
                SQL.class
        })

public class BaseCommand implements Runnable {

    private final PrintStream outputStream;

    public BaseCommand() {
        outputStream = System.out;
    }

    public BaseCommand(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        outputStream.println("Invalid command");
    }
}
