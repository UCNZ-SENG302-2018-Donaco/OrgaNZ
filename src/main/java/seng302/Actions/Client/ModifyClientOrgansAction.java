package seng302.Actions.Client;

import java.util.HashMap;
import java.util.Map;

import seng302.Actions.Action;
import seng302.Client;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;

/**
 * A reversible client organ modification Action
 */
public class ModifyClientOrgansAction implements Action {

    private Map<Organ, Boolean> changes = new HashMap<>();
    private Client client;

    /**
     * Create a new Action
     * @param client The client to be modified
     */
    public ModifyClientOrgansAction(Client client) {
        this.client = client;
    }

    /**
     * Add a organ change to the client. Should check the value is not already set before adding the change
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
     * Loops through the list of changes and applies them to the client
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
                client.setOrganStatus(organ, newState);
            } catch (OrganAlreadyRegisteredException e) {
                e.printStackTrace();
            }
        }
    }
}