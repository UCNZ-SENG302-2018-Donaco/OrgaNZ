package seng302.Commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import seng302.App;
import seng302.Donor;
import seng302.DonorManager;

import java.util.ArrayList;

@Command(name = "printallinfo", description = "Print all users with their personal information.", sortOptions = false)
public class PrintAllInfo implements Runnable {

    private DonorManager manager;

    public PrintAllInfo() {
        manager = App.getManager();
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
        }
    }
}
