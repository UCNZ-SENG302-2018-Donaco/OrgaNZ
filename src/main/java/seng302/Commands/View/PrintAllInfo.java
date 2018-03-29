package seng302.Commands.View;

import java.util.ArrayList;

import seng302.Donor;
import seng302.HistoryItem;
import seng302.State.DonorManager;
import seng302.State.State;
import seng302.Utilities.JSONConverter;

import picocli.CommandLine.Command;

/**
 * Command line to print all of the information of all the users, including their ID. Not Sorted.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 05/03/2018
 */

@Command(name = "printallinfo", description = "Print all users with their personal information.", sortOptions = false)
public class PrintAllInfo implements Runnable {

    private DonorManager manager;

    public PrintAllInfo() {
        manager = State.getDonorManager();
    }

    public PrintAllInfo(DonorManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        ArrayList<Donor> donors = manager.getDonors();

        if (donors.size() == 0) {
            System.out.println("No donors exist");
        } else {
            for (Donor donor : donors) {
                System.out.println(donor.getDonorInfoString());
            }
            HistoryItem printAllInfo = new HistoryItem("PRINT ALL INFO", "All donors information printed.");
            JSONConverter.updateHistory(printAllInfo, "action_history.json");
        }
    }
}
