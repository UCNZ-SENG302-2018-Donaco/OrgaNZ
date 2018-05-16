package seng302.Commands.Modify;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import seng302.HistoryItem;
import seng302.HistoryManager;
import seng302.State.ClientManager;
import seng302.State.State;
import seng302.Utilities.JSONConverter;

import picocli.CommandLine.Command;

/**
 * Command line to load the information of all the clients from a JSON file,
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 05/03/2018
 */

@Command(name = "load", description = "Load clients from file", sortOptions = false)
public class Load implements Runnable {

    private ClientManager manager;

    public Load() {
        manager = State.getClientManager();
    }

    public Load(ClientManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        try {
            JSONConverter.loadFromFile(new File("savefile.json"));
            System.out.println(String.format("Loaded %s users from file", manager.getClients().size()));
            HistoryItem historyItem = new HistoryItem("LOAD", "The systems state was loaded from "
                    + "savefile.json"); // Are we going to allow them to load from different files?
            HistoryManager.INSTANCE.updateHistory(historyItem);
        } catch (FileNotFoundException e) {
            System.out.println("No save file found");
        } catch (IOException e) {
            System.out.println("Could not load from file");
        }
    }
}