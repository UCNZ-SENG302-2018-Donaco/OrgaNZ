package seng302.Commands;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import seng302.HistoryItem;
import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.DeleteUserAction;
import seng302.App;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.JSONConverter;

import java.util.Scanner;

@CommandLine.Command(name = "deleteuser", description = "Deletes a user.")
public class DeleteUser implements Runnable {

    private DonorManager manager;
    private ActionInvoker invoker;

    public DeleteUser() {
        manager = App.getManager();
        invoker = App.getInvoker();
    }

    public DeleteUser(DonorManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
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
                Action action = new DeleteUserAction(donor, manager);
                invoker.execute(action);

                System.out.println("Donor " + uid + " removed. This removal will only be permanent once the 'save' command is used");
                HistoryItem printAllOrgan = new HistoryItem("DELETE", "Donor " + uid + " deleted.");
                JSONConverter.updateHistory(printAllOrgan, "action_history.json");
            } else {
                System.out.println("User not removed");
            }
        }
    }
}
