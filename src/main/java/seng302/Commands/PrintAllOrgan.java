package seng302.Commands;

import picocli.CommandLine.Command;
import seng302.HistoryItem;
import seng302.App;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.JSONConverter;

import java.util.ArrayList;

/**
 * Command line to print all of the information of all the users, including their ID. Not Sorted.
 *
 *@author Dylan Carlyle, Jack Steel
 *@version sprint 1.
 *date 08/03/2018
 */

@Command(name = "printallorgan", description = "Print all users with their organ donation status.", sortOptions = false)
public class PrintAllOrgan implements Runnable {

    private DonorManager manager;

    public PrintAllOrgan() {
        manager = App.getManager();
    }

    PrintAllOrgan(DonorManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        ArrayList<Donor> donors = manager.getDonors();

        if (donors.size() == 0) {
            System.out.println("No donors exist");
        } else {
            for (Donor donor : donors) {
                System.out.println(donor.getDonorOrganStatusString());
            }
            HistoryItem printAllOrgan = new HistoryItem("PRINT ALL ORGAN", "All donor organ information printed.");
            JSONConverter.updateHistory(printAllOrgan, "action_history.json");
        }
    }
}
