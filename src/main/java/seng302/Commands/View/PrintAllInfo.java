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
 * date 05/03/2018
 */

@Command(name = "printallinfo", description = "Print all users with their personal information.", sortOptions = false)
public class PrintAllInfo implements Runnable {

    private PersonManager manager;

    public PrintAllInfo() {
        manager = State.getPersonManager();
    }

    public PrintAllInfo(PersonManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        ArrayList<Person> people = manager.getPeople();

        if (people.size() == 0) {
            System.out.println("No people exist");
        } else {
            for (Person person : people) {
                System.out.println(person.getPersonInfoString());
            }
            HistoryItem printAllInfo = new HistoryItem("PRINT ALL INFO", "All people information printed.");
            JSONConverter.updateHistory(printAllInfo, "action_history.json");
        }
    }
}
