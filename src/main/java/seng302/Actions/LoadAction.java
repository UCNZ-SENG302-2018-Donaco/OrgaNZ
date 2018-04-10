package seng302.Actions;

import java.util.ArrayList;

import seng302.Person;
import seng302.State.PersonManager;

/**
 * A reversible PersonManager person list modification Action
 */
public class LoadAction implements Action {

    private PersonManager manager;

    private ArrayList<Person> oldState;
    private ArrayList<Person> newState;

    /**
     * Create a new Action
     * @param oldState The initial state of the Person array
     * @param newState The new state of the Person array
     * @param manager The PersonManager to apply changes to
     */
    public LoadAction(ArrayList<Person> oldState, ArrayList<Person> newState, PersonManager manager) {
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
