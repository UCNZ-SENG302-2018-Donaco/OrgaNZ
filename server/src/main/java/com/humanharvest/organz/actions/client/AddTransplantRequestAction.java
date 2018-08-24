package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.ClientManager;

/**
 * A reversible action that will add the given transplant request for the given Client to the system.
 */
public class AddTransplantRequestAction extends ClientAction {

    private TransplantRequest request;

    /**
     * Creates a new action to add the given transplant request to the given client.
     *
     * @param client  The client to add the request for.
     * @param request The request to add.
     * @param manager The ClientManager to apply the changes to
     */
    public AddTransplantRequestAction(Client client, TransplantRequest request, ClientManager manager) {
        super(client, manager);
        this.request = request;
    }

    @Override
    protected void execute() {
        super.execute();
        client.addTransplantRequest(request);
        manager.applyChangesTo(client);
    }

    @Override
    protected void unExecute() {
        super.unExecute();
        client.removeTransplantRequest(request);
        manager.applyChangesTo(client);
    }

    @Override
    public String getExecuteText() {
        return String.format("Added new transplant request for '%s' to client %d: %s.",
                request.getRequestedOrgan(), client.getUid(), client.getFullName());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Reversed addition of new transplant request for '%s' to client %d: %s.",
                request.getRequestedOrgan(), client.getUid(), client.getFullName());
    }
}
