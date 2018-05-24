package seng302.Commands.Modify;

import java.io.File;
import java.io.IOException;
import java.util.List;

import seng302.Client;
import seng302.HistoryItem;
import seng302.State.ClientManager;
import seng302.State.State;
import seng302.Utilities.JSONConverter;

import picocli.CommandLine.Command;

/**
 * Command line to save the current information of the Clients onto a JSON file using the GSON API.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 06/03/2018
 */

@Command(name = "save", description = "Save clients to file", sortOptions = false)
public class Save implements Runnable {

    private ClientManager manager;

    public Save() {
        manager = State.getClientManager();
    }

    public Save(ClientManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        List<Client> clients = manager.getClients();
        if (clients.size() == 0) {
            System.out.println("No clients exist, nothing to save");
            return;
        }
        try {
            JSONConverter.saveToFile(new File("savefile.json"));
            System.out.println(String.format("Saved %s clients to file", manager.getClients().size()));
            HistoryItem save = new HistoryItem("SAVE", "The systems current state was saved.");
            JSONConverter.updateHistory(save, "action_history.json");
        } catch (IOException e) {
            System.out.println("Could not save to file");
        }
    }
}