package seng302.Actions.Person;

import seng302.Actions.Action;
import seng302.Person;
import seng302.State.PersonManager;

/**
 * A reversible person deletion action
 */
public class DeletePersonAction implements Action {

    private Person person;
    private PersonManager manager;

    /**
     * Create a new Action
     * @param person The person to be removed
     * @param manager The PersonManager to apply changes to
     */
    public DeletePersonAction(Person person, PersonManager manager) {
        this.person = person;
        this.manager = manager;
    }

    @Override
    public void execute() {
        manager.removePerson(person);
    }

    @Override
    public void unExecute() {
        manager.addPerson(person);
    }
}