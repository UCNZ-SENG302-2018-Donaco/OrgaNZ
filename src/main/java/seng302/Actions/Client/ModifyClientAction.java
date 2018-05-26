package seng302.Actions.Client;

import java.util.ArrayList;
import java.util.stream.Collectors;

import seng302.Actions.ModifyObjectByFieldAction;
import seng302.Client;
import seng302.State.ClientManager;

/**
 * A reversible client modification Action
 */
public class ModifyClientAction extends ClientAction {

    private ArrayList<ModifyObjectByFieldAction> actions = new ArrayList<>();
    private Client client;
    private ClientManager manager;

    /**
     * Create a new Action
     * @param client The client to be modified
     * @param manager // TODO
     */
    public ModifyClientAction(Client client, ClientManager manager) {
        this.client = client;
        this.manager = manager;
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
            throws NoSuchMethodException, NoSuchFieldException {
        actions.add(new ModifyObjectByFieldAction(client, field, oldValue, newValue));
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

    @Override
    protected Client getAffectedClient() {
        return client;
    }
}
