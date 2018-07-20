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
 * Command line to print all of the information of a single client.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 06/03/2018
 */

@Command(name = "printclientinfo", description = "Print a single client with their personal information.", sortOptions =
        false)
public class PrintClientInfo implements Runnable {

    private ClientManager manager;

    @Option(names = {"--id", "-u"}, description = "User ID", required = true)
    private int uid;

    public PrintClientInfo() {
        manager = State.getClientManager();
    }

    public PrintClientInfo(ClientManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        Optional<Client> client = manager.getClientByID(uid);
        if (!client.isPresent()) {
            System.out.println("No client exists with that user ID");
            return;
        }
        System.out.println(client.get().getClientInfoString());
        HistoryItem printUserInfo = new HistoryItem("PRINT CLIENT INFO", "Information was printed about client " + uid);
        JSONConverter.updateHistory(printUserInfo, "action_history.json");
    }
}

