package seng302.Commands.View;


import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import seng302.Donor;
import seng302.State.DonorManager;
import seng302.HistoryItem;
import seng302.State.State;
import seng302.Utilities.JSONConverter;

/**
 * Command line to print the donation information of a user.
 *
 *@author Dylan Carlyle, Jack Steel
 *@version sprint 1.
 *date 05/03/2018
 */

@Command(name = "printuserorgan", description = "Print a single user with their organ information.", sortOptions = false)
public class PrintDonorOrgan implements Runnable {

    private DonorManager manager;

    public PrintDonorOrgan() {
        manager = State.getDonorManager();
    }

    public PrintDonorOrgan(DonorManager manager) {
        this.manager = manager;
    }

    @Option(names = {"--id", "-u"}, description = "User ID", required = true)
    private int uid;

    @Override
    public void run() {
        Donor donor = manager.getDonorByID(uid);
        if (donor == null) {
            System.out.println("No donor exists with that user ID");
            return;
        }
        System.out.println(donor.getDonorOrganStatusString());
        HistoryItem printUserOrgan = new HistoryItem("PRINT USER ORGAN", "The organ information was printed for donor " + uid);
        JSONConverter.updateHistory(printUserOrgan, "action_history.json");
    }
}