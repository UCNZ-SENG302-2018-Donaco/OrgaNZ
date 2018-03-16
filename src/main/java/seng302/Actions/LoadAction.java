package seng302.Actions;

import seng302.Donor;
import seng302.DonorManager;

import java.util.ArrayList;

public class LoadAction implements Action {

    private DonorManager manager;

    private ArrayList<Donor> oldState;
    private ArrayList<Donor> newState;

    public LoadAction (ArrayList<Donor> oldState, ArrayList<Donor> newState, DonorManager manager) {
        this.manager = manager;
        this.oldState = oldState;
        this.newState = newState;
    }

    @Override
    public void execute() {
        manager.setDonors(newState);
    }

    @Override
    public void unExecute() {
        manager.setDonors(oldState);
    }
}
