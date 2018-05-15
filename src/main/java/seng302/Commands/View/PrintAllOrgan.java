package seng302.Commands.View;

import java.util.ArrayList;
import java.util.List;

import seng302.Client;
import seng302.HistoryItem;
import seng302.State.ClientManager;
import seng302.State.State;
import seng302.Utilities.JSONConverter;

import picocli.CommandLine.Command;

/**
 * Command line to print all of the information of all the users, including their ID. Not Sorted.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 08/03/2018
 */

@Command(name = "printallorgan", description = "Print all users with their organ donation status.", sortOptions = false)
public class PrintAllOrgan implements Runnable {

    private ClientManager manager;

    public PrintAllOrgan() {
        manager = State.getClientManager();
    }

    public PrintAllOrgan(ClientManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        List<Client> clients = manager.getClients();

        if (clients.isEmpty()) {
            System.out.println("No clients exist");
        } else {
            for (Client client : clients) {
                System.out.println(client.getClientOrganStatusString("donations"));
            }
            HistoryItem printAllOrgan = new HistoryItem("PRINT ALL ORGAN", "All client organ information printed.");
            JSONConverter.updateHistory(printAllOrgan, "action_history.json");
        }
    }
}
