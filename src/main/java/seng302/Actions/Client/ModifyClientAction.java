package seng302.Actions.Client;

import java.util.ArrayList;

import seng302.Actions.Action;
import seng302.Actions.ModifyObjectByFieldAction;
import seng302.Client;

/**
 * A reversible client modification Action
 */
public class ModifyClientAction implements Action {

    private ArrayList<ModifyObjectByFieldAction> actions = new ArrayList<>();
    private Client client;

    /**
     * Create a new Action
     * @param client The client to be modified
     */
    public ModifyClientAction(Client client) {
        this.client = client;
    }

    /**
     * Add a modification to the client
     * @param field The setter field of the client. Must match a valid setter in the Client object
     * @param oldValue The object the field initially had. Should be taken from the Clients equivalent getter
     * @param newValue The object the field should be update to. Must match the setters Object type
     * @throws NoSuchMethodException Thrown if the Client does not have the specified setter
     * @throws NoSuchFieldException Thrown if the Clients specified setter does not take the same type as given in one of
     * the values
     */
    public void addChange(String field, Object oldValue, Object newValue)
            throws NoSuchMethodException, NoSuchFieldException {
        actions.add(new ModifyObjectByFieldAction(client, field, oldValue, newValue));
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
