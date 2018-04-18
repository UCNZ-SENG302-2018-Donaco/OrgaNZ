package seng302.Actions.Client;

import java.util.HashMap;
import java.util.Map;

import seng302.Actions.Action;
import seng302.Client;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;

public class ModifyOrganRequestAction implements Action {

    private Map<Organ, Boolean> changes = new HashMap<>();
    private Client client;

    /**
     * Create a new Action
     * @param client The client to be modified
     */
    public ModifyOrganRequestAction(Client client) {
        this.client = client;
    }

    /**
     * Add a organ request change to the client. Should check the value is not already set before adding the change.
     * This change is added to the transplant request list.
     * @param organ The organ to be updated
     * @param newValue The new value
     */
    public void addChange(Organ organ, Boolean newValue) {
        TransplantRequest transplantRequest = new TransplantRequest(organ, newValue);
        client.transplantRequestsUpdate(transplantRequest);
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
                client.setOrganRequestStatus(organ, newState);
            } catch (OrganAlreadyRegisteredException e) {
                e.printStackTrace();
            }
        }
    }
}
