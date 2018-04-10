package seng302.Actions.Person;

import java.util.HashMap;
import java.util.Map;

import seng302.Actions.Action;
import seng302.Person;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;

/**
 * A reversible person organ modification Action
 */
public class ModifyPersonOrgansAction implements Action {

    private Map<Organ, Boolean> changes = new HashMap<>();
    private Person person;

    /**
     * Create a new Action
     * @param person The person to be modified
     */
    public ModifyPersonOrgansAction(Person person) {
        this.person = person;
    }

    /**
     * Add a organ change to the person. Should check the value is not already set before adding the change
     * @param organ The organ to be updated
     * @param newValue The new value
     */
    public void addChange(Organ organ, Boolean newValue) {
        changes.put(organ, newValue);
    }


    @Override
    public void execute() {
        runChanges(false);
    }

    @Override
    public void unExecute() {
        runChanges(true);
    }

    /**
     * Loops through the list of changes and applies them to the person
     * @param isUndo If true, negate all booleans
     */
    private void runChanges(boolean isUndo) {
        for (Map.Entry<Organ, Boolean> entry : changes.entrySet()) {
            try {
                Organ organ = entry.getKey();
                boolean newState = entry.getValue();
                if (isUndo) {
                    newState = !newState;
                }
                person.setOrganStatus(organ, newState);
            } catch (OrganAlreadyRegisteredException e) {
                e.printStackTrace();
            }
        }
    }
}