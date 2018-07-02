package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.state.ClientManager;

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

    @Override
    public String getExecuteText() {
        return String.format("Deleted client %s", client.getFullName());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Re-added client %s", client.getFullName());
    }

}
