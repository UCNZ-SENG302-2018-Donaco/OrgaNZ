package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.ClientManager;

/**
 * A reversible client deletion action
 */
public class DeleteClientAction extends ClientAction {

    /**
     * Create a new Action
     *
     * @param client  The client to be removed
     * @param manager The ClientManager to apply changes to
     */
    public DeleteClientAction(Client client, ClientManager manager) {
        super(client, manager);
    }

    @Override
    protected void execute() {
        super.execute();
        manager.removeClient(client);
    }

    @Override
    protected void unExecute() {
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
}
