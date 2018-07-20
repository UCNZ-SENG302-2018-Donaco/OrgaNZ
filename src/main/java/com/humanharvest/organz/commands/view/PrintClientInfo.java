package com.humanharvest.organz.commands.view;

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

    private ClientManager manager;

    public PrintClientInfo() {
        manager = State.getClientManager();
    }

    public PrintClientInfo(ClientManager manager) {
        this.manager = manager;
    }

    @Option(names = {"--id", "-u"}, description = "User ID", required = true)
    private int uid;

    @Override
    public void run() {
        Client client = manager.getClientByID(uid);
        if (client == null) {
            System.out.println("No client exists with that user ID");
            return;
        }
        System.out.println(client.getClientInfoString());
    }
}

