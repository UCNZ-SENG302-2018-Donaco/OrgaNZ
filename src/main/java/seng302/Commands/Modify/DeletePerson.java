package seng302.Commands.Modify;

import java.util.Scanner;

import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.Person.DeletePersonAction;
import seng302.Person;
import seng302.HistoryItem;
import seng302.State.PersonManager;
import seng302.State.State;
import seng302.Utilities.JSONConverter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "deleteuser", description = "Deletes a user.")
public class DeletePerson implements Runnable {

    private PersonManager manager;
    private ActionInvoker invoker;

    public DeletePerson() {
        manager = State.getPersonManager();
        invoker = State.getInvoker();
    }

    public DeletePerson(PersonManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
    }

    @Option(names = {"-u", "--uid"}, description = "User ID", required = true)
    private int uid;

    public void run() {
        Person person = manager.getPersonByID(uid);
        if (person == null) {
            System.out.println("No person exists with that user ID");
        } else {
            System.out.println(
                    String.format("Removing user: %s %s %s, with date of birth: %s, would you like to proceed? (y/n)",
                            person.getFirstName(), person.getMiddleName(), person.getLastName(), person.getDateOfBirth()));
            Scanner scanner = new Scanner(System.in);
            String response = scanner.next();

            if (response.equals("y")) {
                Action action = new DeletePersonAction(person, manager);
                invoker.execute(action);

                System.out.println("Person " + uid
                        + " removed. This removal will only be permanent once the 'save' command is used");
                HistoryItem printAllOrgan = new HistoryItem("DELETE", "Person " + uid + " deleted.");
                JSONConverter.updateHistory(printAllOrgan, "action_history.json");
            } else {
                System.out.println("User not removed");
            }
        }
    }
}
