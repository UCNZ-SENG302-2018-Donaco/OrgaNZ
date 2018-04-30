package seng302.Actions.Client;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import seng302.Actions.Action;
import seng302.Client;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;

public class ModifyOrganRequestAction extends Action {

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

    //TODO implement these two methods

    public String getExecuteText() {
        return "";
    }

    public String getUnexecuteText() {
        return "";
    }

    /**
     * Loops through the list of changes and applies them to the client
     * @param isUndo If true, negate all booleans
     */
    private void runChanges(boolean isUndo) {
        for (Entry<Organ, Boolean> entry : changes.entrySet()) {
            try {
                Organ organ = entry.getKey();
                boolean newState = entry.getValue();
                if (isUndo) {
                    newState = !newState;
                }
                client.setOrganRequestStatus(organ, newState);

                if (!isUndo) {
                    if (!newState) {
                        client.getTransplantRequests()
                                .stream()
                                .filter(x -> x.getCurrentRequest() && x.getRequestedOrgan() == organ)
                                .forEach(x -> x.setCurrentRequest(false));
                    } else {
                        TransplantRequest transplantRequest = new TransplantRequest(organ, true);
                        client.addTransplantRequest(transplantRequest);
                    }
                } else {
                    if (!newState) {
                        client.getTransplantRequests()
                                .stream()
                                .filter(x -> !x.getCurrentRequest() && x.getRequestedOrgan() == organ)
                                .reduce((x, y) -> y).get()
                                .setCurrentRequest(true);
                    } else {
                        TransplantRequest lastestRequest = client.getTransplantRequests()
                                .stream()
                                .filter(x -> x.getCurrentRequest() && x.getRequestedOrgan() == organ)
                                .reduce((x, y) -> y).get();
                        client.getTransplantRequests().remove(lastestRequest);
                    }
                }

            } catch (OrganAlreadyRegisteredException e) {
                e.printStackTrace();
            }
        }
    }
}
