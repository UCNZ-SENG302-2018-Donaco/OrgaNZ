package seng302.Commands.View;

import java.util.List;

import seng302.Client;
import seng302.HistoryItem;
import seng302.State.ClientManager;
import seng302.State.State;
import seng302.Utilities.JSONConverter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

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
            HistoryItem printAllOrgan = new HistoryItem("PRINT ALL ORGAN", "All client organ information printed.");
            JSONConverter.updateHistory(printAllOrgan, "action_history.json");
        } else {
            System.out.println("Define if organs to print are donations or requests e.g. 'printuserorgan "
                    + "-uid=1 -t=requests'");
        }
    }
}
