package seng302.Commands;


import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import seng302.HistoryItem;
import seng302.State;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.JSONConverter;

/**
 * Command line to print all of the information of a single user.
 *
 *@author Dylan Carlyle, Jack Steel
 *@version sprint 1.
 *date 06/03/2018
 */

@Command(name = "printuserinfo", description = "Print a single user with their personal information.", sortOptions = false)
public class PrintUserInfo implements Runnable {

    private DonorManager manager;

    public PrintUserInfo() {
        manager = State.getDonorManager();
    }

    PrintUserInfo(DonorManager manager) {
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
        System.out.println(donor.getDonorInfoString());
        HistoryItem printUserInfo = new HistoryItem("PRINT DONOR INFO", "Information was printed about donor " + uid);
        JSONConverter.updateHistory(printUserInfo, "action_history.json");
    }
}

