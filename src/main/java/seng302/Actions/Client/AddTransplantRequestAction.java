package seng302.Actions.Client;

import seng302.Actions.Action;
import seng302.Client;
import seng302.TransplantRequest;

/**
 * A reversible action that will add the given transplant request for the given Client to the system.
 */
public class AddTransplantRequestAction extends Action {

    private Client client;
    private TransplantRequest request;

    public AddTransplantRequestAction(Client client, TransplantRequest request) {
        this.client = client;
        this.request = request;
    }

    @Override
    protected void execute() {
        client.addTransplantRequest(request);
    }

    @Override
    protected void unExecute() {
        client.removeTransplantRequest(request);
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
