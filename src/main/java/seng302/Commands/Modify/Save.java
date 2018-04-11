package seng302.Commands.Modify;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import seng302.Person;
import seng302.HistoryItem;
import seng302.State.PersonManager;
import seng302.State.State;
import seng302.Utilities.JSONConverter;

import picocli.CommandLine.Command;

/**
 * Command line to save the current information of the Persons onto a JSON file using the GSON API.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 06/03/2018
 */

@Command(name = "save", description = "Save persons to file", sortOptions = false)
public class Save implements Runnable {

    private PersonManager manager;

    public Save() {
        manager = State.getPersonManager();
    }

    public Save(PersonManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        ArrayList<Person> people = manager.getPeople();
        if (people.size() == 0) {
            System.out.println("No people exist, nothing to save");
            return;
        }
        try {
            JSONConverter.saveToFile(new File("savefile.json"));
            System.out.println(String.format("Saved %s users to file", manager.getPeople().size()));
            HistoryItem save = new HistoryItem("SAVE", "The systems current state was saved.");
            JSONConverter.updateHistory(save, "action_history.json");
        } catch (IOException e) {
            System.out.println("Could not save to file");
        }
    }
}