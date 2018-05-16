package seng302.Commands.View;

import java.util.List;

import seng302.Client;
import seng302.HistoryItem;
import seng302.HistoryManager;
import seng302.State.ClientManager;
import seng302.State.State;

import picocli.CommandLine.Command;

/**
 * Command line to print all of the information of all the users, including their ID. Not Sorted.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 05/03/2018
 */

@Command(name = "printallinfo", description = "Print all users with their personal information.", sortOptions = false)
public class PrintAllInfo implements Runnable {

    private ClientManager manager;

    public PrintAllInfo() {
        manager = State.getClientManager();
    }

    public PrintAllInfo(ClientManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        List<Client> clients = manager.getClients();

        if (clients.isEmpty()) {
            System.out.println("No clients exist");
        } else {
            for (Client client : clients) {
                System.out.println(client.getClientInfoString());
            }
            HistoryItem historyItem = new HistoryItem("PRINT ALL INFO", "All clients information printed.");
            HistoryManager.INSTANCE.updateHistory(historyItem);
        }
    }
}
