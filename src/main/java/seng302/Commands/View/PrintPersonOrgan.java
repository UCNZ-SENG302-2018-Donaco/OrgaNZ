package seng302.Commands.View;


import seng302.Person;
import seng302.HistoryItem;
import seng302.State.PersonManager;
import seng302.State.State;
import seng302.Utilities.JSONConverter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command line to print the donation information of a user.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 05/03/2018
 */

@Command(name = "printuserorgan", description = "Print a single user with their organ information.", sortOptions = false)
public class PrintPersonOrgan implements Runnable {

    private PersonManager manager;

    public PrintPersonOrgan() {
        manager = State.getPersonManager();
    }

    public PrintPersonOrgan(PersonManager manager) {
        this.manager = manager;
    }

    @Option(names = {"--id", "-u"}, description = "User ID", required = true)
    private int uid;

    @Override
    public void run() {
        Person person = manager.getPersonByID(uid);
        if (person == null) {
            System.out.println("No person exists with that user ID");
            return;
        }
        System.out.println(person.getPersonOrganStatusString());
        HistoryItem printUserOrgan = new HistoryItem("PRINT USER ORGAN",
                "The organ information was printed for person " + uid);
        JSONConverter.updateHistory(printUserOrgan, "action_history.json");
    }
}