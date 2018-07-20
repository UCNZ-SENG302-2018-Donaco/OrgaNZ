package com.humanharvest.organz.commands.view;

import java.io.PrintStream;
import java.util.List;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import picocli.CommandLine.Command;

/**
 * Command line to print all of the information of all the clients, including their ID. Not Sorted.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 05/03/2018
 */

@Command(name = "printallinfo", description = "Print all clients with their personal information.", sortOptions = false)
public class PrintAllInfo implements Runnable {

    private final ClientManager manager;
    private final PrintStream outputStream;

    public PrintAllInfo() {
        manager = State.getClientManager();
        outputStream = System.out;
    }

    public PrintAllInfo(PrintStream outputStream) {
        manager = State.getClientManager();
        this.outputStream = outputStream;
    }

    public PrintAllInfo(ClientManager manager) {
        this.manager = manager;
        outputStream = System.out;
    }

    @Override
    public void run() {
        List<Client> clients = manager.getClients();

        if (clients.size() == 0) {
            outputStream.println("No clients exist");
        } else {
            for (Client client : clients) {
                outputStream.println(client.getClientInfoString());
            }
        }
    }
}
