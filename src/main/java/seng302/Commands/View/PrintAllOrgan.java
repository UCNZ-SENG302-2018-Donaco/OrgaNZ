package seng302.Commands.View;

import java.util.ArrayList;

import seng302.Person;
import seng302.HistoryItem;
import seng302.State.PersonManager;
import seng302.State.State;
import seng302.Utilities.JSONConverter;

import picocli.CommandLine.Command;

/**
 * Command line to print all of the information of all the users, including their ID. Not Sorted.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 08/03/2018
 */

@Command(name = "printallorgan", description = "Print all users with their organ donation status.", sortOptions = false)
public class PrintAllOrgan implements Runnable {

    private PersonManager manager;

    public PrintAllOrgan() {
        manager = State.getPersonManager();
    }

    public PrintAllOrgan(PersonManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        ArrayList<Person> people = manager.getPeople();

        if (people.size() == 0) {
            System.out.println("No people exist");
        } else {
            for (Person person : people) {
                System.out.println(person.getPersonOrganStatusString());
            }
            HistoryItem printAllOrgan = new HistoryItem("PRINT ALL ORGAN", "All person organ information printed.");
            JSONConverter.updateHistory(printAllOrgan, "action_history.json");
        }
    }
}
