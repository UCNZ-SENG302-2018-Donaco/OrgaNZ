package seng302.Actions.Person;

import java.util.ArrayList;

import seng302.Actions.Action;
import seng302.Actions.ModifyObjectByFieldAction;
import seng302.Person;

/**
 * A reversible person modification Action
 */
public class ModifyPersonAction implements Action {

    private ArrayList<ModifyObjectByFieldAction> actions = new ArrayList<>();
    private Person person;

    /**
     * Create a new Action
     * @param person The person to be modified
     */
    public ModifyPersonAction(Person person) {
        this.person = person;
    }

    /**
     * Add a modification to the person
     * @param field The setter field of the person. Must match a valid setter in the Person object
     * @param oldValue The object the field initially had. Should be taken from the Persons equivalent getter
     * @param newValue The object the field should be update to. Must match the setters Object type
     * @throws NoSuchMethodException Thrown if the Person does not have the specified setter
     * @throws NoSuchFieldException Thrown if the Persons specified setter does not take the same type as given in one of
     * the values
     */
    public void addChange(String field, Object oldValue, Object newValue)
            throws NoSuchMethodException, NoSuchFieldException {
        actions.add(new ModifyObjectByFieldAction(person, field, oldValue, newValue));
    }

    @Override
    public void execute() {
        for (ModifyObjectByFieldAction action : actions) {
            action.execute();
        }
    }

    @Override
    public void unExecute() {
        for (ModifyObjectByFieldAction action : actions) {
            action.unExecute();
        }
    }
}
