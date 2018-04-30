package seng302.Actions.Client;

import seng302.Actions.Action;
import seng302.Client;
import seng302.State.ClientManager;

/**
 * A reversible client deletion action
 */
public class DeleteClientAction extends Action {

    private Client client;
    private ClientManager manager;

    /**
     * Create a new Action
     * @param client The client to be removed
     * @param manager The ClientManager to apply changes to
     */
    public DeleteClientAction(Client client, ClientManager manager) {
        this.client = client;
        this.manager = manager;
    }

    @Override
    public void execute() {
        manager.removeClient(client);
    }

    @Override
    public void unExecute() {
        manager.addClient(client);
    }

    public String getExecuteText() {
        return String.format("Deleted donor %s", client.getFullName());
    }

    public String getUnexecuteText() {
        return String.format("Re-added donor %s", client.getFullName());
    }
}