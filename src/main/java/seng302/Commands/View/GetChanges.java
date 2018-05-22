package seng302.Commands.View;


import seng302.Client;
import seng302.HistoryItem;
import seng302.State.ClientManager;
import seng302.State.State;
import seng302.Utilities.JSONConverter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command line to print all of the information of a single client.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 06/03/2018
 */

@Command(name = "getchanges", description = "Print a single clients update history.", sortOptions = false)
public class GetChanges implements Runnable {

    private ClientManager manager;

    public GetChanges() {
        manager = State.getClientManager();
    }

    GetChanges(ClientManager manager) {
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
        System.out.println(client.getUpdatesString());
        HistoryItem printAllHistory = new HistoryItem("PRINT UPDATE HISTORY", "All client's history printed.");
        JSONConverter.updateHistory(printAllHistory, "action_history.json");
    }
}

