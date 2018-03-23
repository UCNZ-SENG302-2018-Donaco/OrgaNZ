package seng302.Actions;

import seng302.Donor;
import seng302.DonorManager;

/**
 * A reversible donor creation action
 */
public class CreateDonorAction implements Action {


    private Donor donor;
    private DonorManager manager;


    /**
     * Create a new Action
     * @param donor The Donor to be created
     * @param manager The DonorManager to apply changes to
     */
    public CreateDonorAction(Donor donor, DonorManager manager) {
        this.donor = donor;
        this.manager = manager;
    }


    /**
     * Simply add the donor to the DonorManager
     */
    @Override
    public void execute() {
        manager.addDonor(donor);
    }

    /**
     * Simply remove the donor from the DonorManager
     */
    @Override
    public void unExecute() {
        manager.removeDonor(donor);
    }
}
