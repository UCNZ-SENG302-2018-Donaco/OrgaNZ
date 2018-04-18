package seng302.Actions;

import java.util.ArrayList;

import seng302.Client;
import seng302.State.ClientManager;

public class SaveAction implements Action {

    private ClientManager manager;

    private ArrayList<Client> oldState;
    private ArrayList<Client> newState;

    public SaveAction(ArrayList<Client> oldState, ArrayList<Client> newState, ClientManager manager) {
        this.manager = manager;
        this.oldState = oldState;
        this.newState = newState;
    }

    @Override
    public void execute() {
        manager.setPeople(newState);
    }

    @Override
    public void unExecute() {
        manager.setPeople(oldState);
    }
}
