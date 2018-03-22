package seng302.Actions;

import seng302.Donor;
import seng302.DonorManager;

import java.util.ArrayList;

/**
 * A reversible DonorManager donor list modification Action
 */
public class LoadAction implements Action {

    private DonorManager manager;

    private ArrayList<Donor> oldState;
    private ArrayList<Donor> newState;

    /**
     * Create a new Action
     * @param oldState The initial state of the Donor array
     * @param newState The new state of the Donor array
     * @param manager The DonorManager to apply changes to
     */
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
