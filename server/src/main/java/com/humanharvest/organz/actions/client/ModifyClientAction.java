package com.humanharvest.organz.actions.client;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.ModifyObjectByFieldAction;
import com.humanharvest.organz.state.ClientManager;

/**
 * A reversible client modification Action
 */
public class ModifyClientAction extends ClientAction {

    private ArrayList<ModifyObjectByFieldAction> actions = new ArrayList<>();

    /**
     * Create a new Action
     * @param client The client to be modified
     * @param manager The ClientManager to apply the changes to
     */
    public ModifyClientAction(Client client, ClientManager manager) {
        super(client, manager);
    }

    /**
     * Add a modification to the client
     * @param field The setter field of the client. Must match a valid setter in the Client object
     * @param oldValue The object the field initially had. Should be taken from the Clients equivalent getter
     * @param newValue The object the field should be update to. Must match the setters Object type
     * @throws NoSuchMethodException Thrown if the Client does not have the specified setter
     * @throws NoSuchFieldException Thrown if the Clients specified setter does not take the same type as given in one
     * of the values
     */
    public void addChange(String field, Object oldValue, Object newValue)
            throws NoSuchFieldException {
        actions.add(new ModifyObjectByFieldAction(client, field, oldValue, newValue));
    }

    /**
     * Add a modification to the client
     * @param field The setter field of the client. Must match a valid setter in the Client object
     * @param newValue The object the field should be update to. Must match the setters Object type
     * @throws NoSuchFieldException Thrown if the Clients specified setter does not take the same type as given in one
     * of the values
     */
    public void addChange(String field, Object newValue)
            throws NoSuchFieldException {
        actions.add(new ModifyObjectByFieldAction(client, field, newValue));
    }

    @Override
    protected void execute() {
        super.execute();
        if (actions.size() == 0) {
            throw new IllegalStateException("No changes were made to the client.");
        } else {
            for (ModifyObjectByFieldAction action : actions) {
                action.execute();
            }
            manager.applyChangesTo(client);
        }
    }

    @Override
    protected void unExecute() {
        super.unExecute();
        for (ModifyObjectByFieldAction action : actions) {
            action.unExecute();
        }
        manager.applyChangesTo(client);
    }

    @Override
    public String getExecuteText() {
        String changesText = actions.stream()
                .map(ModifyObjectByFieldAction::getExecuteText)
                .collect(Collectors.joining("\n"));

        return String.format("Updated details for client %d: %s. \n"
                        + "These changes were made: \n\n%s",
                client.getUid(), client.getFullName(), changesText);
    }

    @Override
    public String getUnexecuteText() {
        String changesText = actions.stream()
                .map(ModifyObjectByFieldAction::getExecuteText)
                .collect(Collectors.joining("\n"));

        return String.format("Reversed update for client %d: %s. \n"
                        + "These changes were reversed: \n\n%s",
                client.getUid(), client.getFullName(), changesText);
    }
}
