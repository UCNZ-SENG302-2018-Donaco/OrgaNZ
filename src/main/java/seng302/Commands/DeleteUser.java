package seng302.Commands;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import seng302.Action;
import seng302.App;
import seng302.Command.Command;
import seng302.Command.CommandInvoker;
import seng302.Command.DeleteUserCommand;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.JSONConverter;

import java.util.Scanner;

@CommandLine.Command(name = "deleteuser", description = "Deletes a user.")
public class DeleteUser implements Runnable {

    private DonorManager manager;
    private CommandInvoker invoker;

    public DeleteUser() {
        manager = App.getManager();
        invoker = App.getInvoker();
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
                Command command = new DeleteUserCommand(donor, manager);
                invoker.execute(command);

                System.out.println("Donor " + uid + " removed. This removal will only be permanent once the 'save' command is used");
                Action printAllOrgan = new Action("DELETE", "Donor " + uid + " deleted.");
                JSONConverter.updateActionHistory(printAllOrgan, "action_history.json");
            } else {
                System.out.println("User not removed");
            }
        }
    }
}
