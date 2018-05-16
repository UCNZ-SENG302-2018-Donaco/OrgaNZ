package seng302.Commands.View;


import seng302.Client;
import seng302.HistoryItem;
import seng302.State.ClientManager;
import seng302.State.State;
import seng302.Utilities.JSONConverter;

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

    @Option(names = {"-t", "-type"}, description = "Organ donations or requests", required = true)
    private String type;

    @Override
    public void run() {
        // printuserorgan -u=1 -t=requests
        Client client = manager.getClientByID(uid);
        if (client == null) {
            System.out.println("No client exists with that user ID");
            return;
        }
        if (type.equals("requests") || type.equals("donations")) {
            System.out.println(client.getClientOrganStatusString(type));
            HistoryItem printUserOrgan = new HistoryItem("PRINT USER ORGAN",
                    "The organ information was printed for client " + uid);
            JSONConverter.updateHistory(printUserOrgan, "action_history.json");
        } else {
            System.out.println("Define if organs to print are donations or requests e.g. 'printuserorgan "
                    + "-uid=1 -t=requests'");
        }

    }
}