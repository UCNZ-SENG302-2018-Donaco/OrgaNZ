package seng302.Actions.Client;

import seng302.Actions.Action;
import seng302.Client;
import seng302.State.ClientManager;
import seng302.TransplantRequest;

/**
 * A reversible action that will add the given transplant request for the given Client to the system.
 */
public class AddTransplantRequestAction extends Action {

    private Client client;
    private TransplantRequest request;
    private ClientManager clientManager;

    /**
     * Creates a new action to add the given transplant request to the given client.
     * @param client The client to add the request for.
     * @param request The request to add.
     */
    public AddTransplantRequestAction(Client client, TransplantRequest request, ClientManager clientManager) {
        this.client = client;
        this.request = request;
        this.clientManager = clientManager;
    }

    @Override
    protected void execute() {
        client.addTransplantRequest(request);
        clientManager.applyChangesTo(client);
    }

    @Override
    protected void unExecute() {
        client.removeTransplantRequest(request);
        clientManager.applyChangesTo(client);
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
