package com.humanharvest.organz.commands.view;

import java.io.PrintStream;
import java.util.Optional;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command line to print all of the information of a single client.
 */

@Command(name = "printclientinfo", description = "Print a single client with their personal information.", sortOptions =
        false)
public class PrintClientInfo implements Runnable {

    private final ClientManager manager;
    private final PrintStream outputStream;

    @Option(names = {"--id", "-u"}, description = "User ID", required = true)
    private int uid;

    public PrintClientInfo() {
        manager = State.getClientManager();
        outputStream = System.out;
    }

    public PrintClientInfo(PrintStream outputStream) {
        manager = State.getClientManager();
        this.outputStream = outputStream;
    }

    public PrintClientInfo(ClientManager manager) {
        this.manager = manager;
        outputStream = System.out;
    }

    @Override
    public void run() {
        Optional<Client> client = manager.getClientByID(uid);
        if (!client.isPresent()) {
            outputStream.println("No client exists with that user ID");
            return;
        }
        outputStream.println(client.get().getClientInfoString());
    }
}

