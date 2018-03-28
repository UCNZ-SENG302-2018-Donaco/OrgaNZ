package seng302.Commands.Modify;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import seng302.Donor;
import seng302.HistoryItem;
import seng302.State.DonorManager;
import seng302.State.State;
import seng302.Utilities.JSONConverter;

import picocli.CommandLine.Command;

/**
 * Command line to save the current information of the Donors onto a JSON file using the GSON API.
 *
 *@author Dylan Carlyle, Jack Steel
 *@version sprint 1.
 *date 06/03/2018
 */

@Command(name = "save", description = "Save donors to file", sortOptions = false)
public class Save implements Runnable {

    private DonorManager manager;

    public Save() {
        manager = State.getDonorManager();
    }

    public Save(DonorManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        ArrayList<Donor> donors = manager.getDonors();
        if (donors.size() == 0) {
            System.out.println("No donors exist, nothing to save");
            return;
        }
        try {
            JSONConverter.saveToFile(new File("savefile.json"));
            System.out.println(String.format("Saved %s users to file",manager.getDonors().size()));
            HistoryItem save = new HistoryItem("SAVE", "The systems current state was saved.");
            JSONConverter.updateHistory(save, "action_history.json");
        } catch (IOException e) {
            System.out.println("Could not save to file");
        }
    }
}