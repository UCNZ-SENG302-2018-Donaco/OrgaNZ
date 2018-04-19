package seng302.Actions;

import java.util.ArrayList;

import seng302.Person;
import seng302.State.PersonManager;

public class SaveAction implements Action {

    private PersonManager manager;

    private ArrayList<Person> oldState;
    private ArrayList<Person> newState;

    public SaveAction(ArrayList<Person> oldState, ArrayList<Person> newState, PersonManager manager) {
        this.manager = manager;
        this.oldState = oldState;
        this.newState = newState;
    }

    @Override
    public void execute() {
        manager.setPeople(newState);
    }

    @Override
    public void unExecute() {
        manager.setPeople(oldState);
    }
}
