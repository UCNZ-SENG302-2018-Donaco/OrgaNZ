package seng302.Actions.Donor;

import seng302.Actions.Action;
import seng302.Donor;
import seng302.State.DonorManager;

/**
 * A reversible donor deletion action
 */
public class DeleteDonorAction implements Action {

    private Donor donor;
    private DonorManager manager;

    /**
     * Create a new Action
     * @param donor The donor to be removed
     * @param manager The DonorManager to apply changes to
     */
    public DeleteDonorAction(Donor donor, DonorManager manager) {
        this.donor = donor;
        this.manager = manager;
    }

    @Override
    public void execute() {
        manager.removeDonor(donor);
    }

    @Override
    public void unExecute() {
        manager.addDonor(donor);
    }

    @Override
    public String getExecuteText() {
        return String.format("Deleted donor %s", donor.getFullName());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Re-added donor %s", donor.getFullName());
    }
}