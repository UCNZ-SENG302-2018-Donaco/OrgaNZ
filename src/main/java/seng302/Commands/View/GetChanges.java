package seng302.Commands.View;


import seng302.Person;
import seng302.HistoryItem;
import seng302.State.PersonManager;
import seng302.State.State;
import seng302.Utilities.JSONConverter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command line to print all of the information of a single user.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 06/03/2018
 */

@Command(name = "getchanges", description = "Print a single users update history.", sortOptions = false)
public class GetChanges implements Runnable {

    private PersonManager manager;

    public GetChanges() {
        manager = State.getPersonManager();
    }

    GetChanges(PersonManager manager) {
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
        System.out.println(person.getUpdatesString());
        HistoryItem printAllHistory = new HistoryItem("PRINT UPDATE HISTORY", "All person's history printed.");
        JSONConverter.updateHistory(printAllHistory, "action_history.json");
    }
}

