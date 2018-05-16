package seng302.Commands.View;


import seng302.Client;
import seng302.HistoryItem;
import seng302.HistoryManager;
import seng302.State.ClientManager;
import seng302.State.State;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command line to print the donation information of a user.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 05/03/2018
 */

@Command(name = "printuserorgan", description = "Print a single user with their organ information.", sortOptions = false)
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

    @Override
    public void run() {
        Client client = manager.getClientByID(uid);
        if (client == null) {
            System.out.println("No client exists with that user ID");
            return;
        }
        System.out.println(client.getClientOrganStatusString("donations"));
        HistoryItem historyItem = new HistoryItem("PRINT USER ORGAN",
                "The organ information was printed for client " + uid);
        HistoryManager.INSTANCE.updateHistory(historyItem);
    }
}