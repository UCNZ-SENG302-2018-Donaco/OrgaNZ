package seng302.Commands.View;


import seng302.Client;
import seng302.HistoryItem;
import seng302.State.ClientManager;
import seng302.State.State;
import seng302.Utilities.JSONConverter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command line to print all of the information of a single user.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 06/03/2018
 */

@Command(name = "printuserinfo", description = "Print a single user with their personal information.", sortOptions =
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
        HistoryItem printUserInfo = new HistoryItem("PRINT CLIENT INFO", "Information was printed about client " + uid);
        JSONConverter.updateHistory(printUserInfo, "action_history.json");
    }
}

