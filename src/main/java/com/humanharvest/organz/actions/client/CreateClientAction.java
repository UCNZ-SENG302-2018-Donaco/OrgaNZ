package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.ClientManager;

/**
 * A reversible client creation action
 */
public class CreateClientAction extends ClientAction {

    /**
     * Create a new Action
     * @param client The Client to be created
     * @param manager The ClientManager to apply changes to
     */
    public CreateClientAction(Client client, ClientManager manager) {
        super(client, manager);
    }


    /**
     * Simply add the client to the ClientManager
     */
    @Override
    public void execute() {
        super.execute();
        manager.addClient(client);
    }

    /**
     * Simply remove the client from the ClientManager
     */
    @Override
    public void unExecute() {
        super.unExecute();
        manager.removeClient(client);
    }

    @Override
    public String getExecuteText() {
        return String.format("Created client %s with user id: %s", client.getFullName(),
                client.getUid());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Removed client %s with user id: %s", client.getFullName(),
                client.getUid());
    }
}
