package com.humanharvest.organz.commands.view;

import java.io.PrintStream;
import java.util.List;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command line to print all of the information of all the client, including their ID. Not Sorted.
 */
@Command(name = "printallorgan",
        description = "Print all clients with their organ donation status.",
        sortOptions = false)
public class PrintAllOrgan implements Runnable {

    private final ClientManager manager;
    private final PrintStream outputStream;

    public PrintAllOrgan() {
        manager = State.getClientManager();
        outputStream = System.out;
    }

    public PrintAllOrgan(PrintStream outputStream) {
        manager = State.getClientManager();
        this.outputStream = outputStream;
    }

    public PrintAllOrgan(ClientManager manager) {
        this.manager = manager;
        outputStream = System.out;
    }

    @Option(names = {"-t", "-type"}, description = "Organ donations or requests", required = true)
    private String type;

    @Override
    public void run() {
        List<Client> clients = manager.getClients();

        if (clients.isEmpty()) {
            outputStream.println("No clients exist");
        } else if("donations".equals(type) || "requests".equals(type)) {
            for (Client client : clients) {
                outputStream.println(client.getClientOrganStatusString(type));
            }
        } else {
            outputStream.println("Define if organs to print are donations or requests e.g. 'printuserorgan "
                    + "-uid=1 -t=requests'");
        }
    }
}
