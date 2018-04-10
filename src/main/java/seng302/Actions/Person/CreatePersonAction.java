package seng302.Actions.Person;

import seng302.Actions.Action;
import seng302.Person;
import seng302.State.PersonManager;

/**
 * A reversible person creation action
 */
public class CreatePersonAction implements Action {


    private Person person;
    private PersonManager manager;


    /**
     * Create a new Action
     * @param person The Person to be created
     * @param manager The PersonManager to apply changes to
     */
    public CreatePersonAction(Person person, PersonManager manager) {
        this.person = person;
        this.manager = manager;
    }


    /**
     * Simply add the person to the PersonManager
     */
    @Override
    public void execute() {
        manager.addPerson(person);
    }

    /**
     * Simply remove the person from the PersonManager
     */
    @Override
    public void unExecute() {
        manager.removePerson(person);
    }
}
