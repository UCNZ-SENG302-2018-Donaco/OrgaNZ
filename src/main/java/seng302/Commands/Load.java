package seng302.Commands;


import picocli.CommandLine.Command;
import seng302.HistoryItem;
import seng302.State;
import seng302.DonorManager;
import seng302.Utilities.JSONConverter;

import java.io.*;

/**
 * Command line to load the information of all the donors from a JSON file,
 *
 *@author Dylan Carlyle, Jack Steel
 *@version sprint 1.
 *date 05/03/2018
 */

@Command(name = "load", description = "Load donors from file", sortOptions = false)
public class Load implements Runnable {

    private DonorManager manager;

    public Load() {
        manager = State.getDonorManager();
    }

    public Load(DonorManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        try {
            JSONConverter.loadFromFile(new File("savefile.json"));
            System.out.println(String.format("Loaded %s users from file",manager.getDonors().size()));
            HistoryItem load = new HistoryItem("LOAD", "The systems state was loaded from " + "savefile.json"); // Are we going to allow them to load from different files?
            JSONConverter.updateHistory(load, "action_history.json");
        } catch (FileNotFoundException e) {
            System.out.println("No save file found");
        } catch (IOException e) {
            System.out.println("Could not load from file");
        }
    }
}