package seng302.Actions.Client;

import seng302.Actions.Action;
import seng302.Client;
import seng302.State.ClientManager;

/**
 * A reversible client creation action
 */
public class CreateClientAction extends Action {


    private Client client;
    private ClientManager manager;


    /**
     * Create a new Action
     * @param client The Client to be created
     * @param manager The ClientManager to apply changes to
     */
    public CreateClientAction(Client client, ClientManager manager) {
        this.client = client;
        this.manager = manager;
    }


    /**
     * Simply add the client to the ClientManager
     */
    @Override
    public void execute() {
        manager.addClient(client);
    }

    /**
     * Simply remove the client from the ClientManager
     */
    @Override
    public void unExecute() {
        manager.removeClient(client);
    }

    //TODO implement these two methods

    public String getExecuteText() {
        return "";
    }

    public String getUnexecuteText() {
        return "";
    }
}
