package com.humanharvest.organz.commands.view;

import java.util.List;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.JSONConverter;

import picocli.CommandLine.Command;

/**
 * Command line to print all of the information of all the clients, including their ID. Not Sorted.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 05/03/2018
 */

@Command(name = "printallinfo", description = "Print all clients with their personal information.", sortOptions = false)
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

        if (clients.size() == 0) {
            System.out.println("No clients exist");
        } else {
            for (Client client : clients) {
                System.out.println(client.getClientInfoString());
            }
            HistoryItem printAllInfo = new HistoryItem("PRINT ALL INFO", "All clients information printed.");
            JSONConverter.updateHistory(printAllInfo, "action_history.json");
        }
    }
}
