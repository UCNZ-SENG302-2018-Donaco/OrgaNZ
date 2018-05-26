package seng302.Actions.Client;

import seng302.Client;
import seng302.State.ClientManager;

/**
 * A reversible client deletion action
 */
public class DeleteClientAction extends ClientAction {

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
        super.execute();
        manager.removeClient(client);
    }

    @Override
    public void unExecute() {
        super.unExecute();
        manager.addClient(client);
    }

    @Override
    public String getExecuteText() {
        return String.format("Deleted client %s", client.getFullName());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Re-added client %s", client.getFullName());
    }

    @Override
    protected Client getAffectedClient() {
        return client;
    }
}
