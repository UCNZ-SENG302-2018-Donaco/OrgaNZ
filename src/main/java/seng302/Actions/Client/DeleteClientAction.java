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

    //TODO implement these two methods

    public String getExecuteText() {
        return "";
    }

    public String getUnexecuteText() {
        return "";
    }
}