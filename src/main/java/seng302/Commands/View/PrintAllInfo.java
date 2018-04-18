package seng302.Commands.View;

import java.util.ArrayList;

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
 * date 05/03/2018
 */

@Command(name = "printallinfo", description = "Print all users with their cliental information.", sortOptions = false)
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
        ArrayList<Client> people = manager.getPeople();

        if (people.size() == 0) {
            System.out.println("No people exist");
        } else {
            for (Client client : people) {
                System.out.println(client.getClientInfoString());
            }
            HistoryItem printAllInfo = new HistoryItem("PRINT ALL INFO", "All people information printed.");
            JSONConverter.updateHistory(printAllInfo, "action_history.json");
        }
    }
}
