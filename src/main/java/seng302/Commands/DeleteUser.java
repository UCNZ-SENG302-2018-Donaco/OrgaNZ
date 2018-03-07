package seng302.Commands;

import picocli.CommandLine.Option;
import picocli.CommandLine.Command;
import seng302.App;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.*;

import java.time.LocalDate;
import java.util.Scanner;

import static java.util.Optional.ofNullable;

@Command(name = "deleteuser", description = "Deletes a user.")
public class DeleteUser implements Runnable {

    private DonorManager manager;

    public DeleteUser() {
        manager = App.getManager();
    }

    public DeleteUser(DonorManager manager) {
        this.manager = manager;
    }

    @Option(names = {"-u", "--uid"}, description = "User ID", required = true)
    private int uid;

    public void run() {
        Donor donor = manager.getDonorByID(uid);
        if (donor == null) {
            System.out.println("No donor exists with that user ID");
        } else {
            System.out.println(String.format("Removing user: %s %s %s, with date of birth: %s, would you like to proceed? (y/n)", donor.getFirstName(), donor.getMiddleName(), donor.getLastName(), donor.getDateOfBirth()));
            Scanner scanner = new Scanner(System.in);
            String response = scanner.next();
            if (response.equals("y")) {
                manager.removeDonor(donor);
                System.out.println("Donor " + uid + " removed. This removal will only be permanent once the 'save' command is used");
            } else {
                System.out.println("User not removed");
            }
        }
    }
}
