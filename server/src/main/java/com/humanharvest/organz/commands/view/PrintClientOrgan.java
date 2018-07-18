package com.humanharvest.organz.commands.view;


import java.util.Optional;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.JSONConverter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command line to print the donation information of a client.
 */

@Command(name = "printclientorgan", description = "Print a single client with their organ information.", sortOptions =
        false)
public class PrintClientOrgan implements Runnable {

    private ClientManager manager;

    public PrintClientOrgan() {
        manager = State.getClientManager();
    }

    public PrintClientOrgan(ClientManager manager) {
        this.manager = manager;
    }

    @Option(names = {"--id", "-u"}, description = "User ID", required = true)
    private int uid;

    @Option(names = {"-t", "-type"}, description = "Organ donations or requests", required = true)
    private String type;

    @Override
    public void run() {
        // printuserorgan -u=1 -t=requests
        Optional<Client> client = manager.getClientByID(uid);
        if (!client.isPresent()) {
            System.out.println("No client exists with that user ID");
            return;
        }
        if (type.equals("requests") || type.equals("donations")) {
            System.out.println(client.get().getClientOrganStatusString(type));
            HistoryItem printUserOrgan = new HistoryItem("PRINT USER ORGAN",
                    "The organ information was printed for client " + uid);
            JSONConverter.updateHistory(printUserOrgan, "action_history.json");
        } else {
            System.out.println("Define if organs to print are donations or requests e.g. 'printuserorgan "
                    + "-uid=1 -t=requests'");
        }
    }
}