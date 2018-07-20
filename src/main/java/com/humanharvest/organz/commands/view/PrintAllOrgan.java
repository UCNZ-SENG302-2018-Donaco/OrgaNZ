package com.humanharvest.organz.commands.view;

import java.util.List;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command line to print all of the information of all the client, including their ID. Not Sorted.
 */

@Command(name = "printallorgan", description = "Print all clients with their organ donation status.", sortOptions =
        false)
public class PrintAllOrgan implements Runnable {

    private ClientManager manager;

    public PrintAllOrgan() {
        manager = State.getClientManager();
    }

    public PrintAllOrgan(ClientManager manager) {
        this.manager = manager;
    }

    @Option(names = {"-t", "-type"}, description = "Organ donations or requests", required = true)
    private String type;

    @Override
    public void run() {
        List<Client> clients = manager.getClients();

        if (clients.size() == 0) {
            System.out.println("No clients exist");
        } else if(type.equals("donations") || type.equals("requests")) {
            for (Client client : clients) {
                System.out.println(client.getClientOrganStatusString(type));

            }
        } else {
            System.out.println("Define if organs to print are donations or requests e.g. 'printuserorgan "
                    + "-uid=1 -t=requests'");
        }
    }
}
