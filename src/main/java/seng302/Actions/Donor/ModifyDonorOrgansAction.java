package seng302.Actions.Donor;

import seng302.Actions.Action;
import seng302.Donor;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;

import java.util.HashMap;
import java.util.Map;

/**
 * A reversible donor organ modification Action
 */
public class ModifyDonorOrgansAction implements Action {
    private Map<Organ, Boolean> changes = new HashMap<>();
    private Donor donor;

    /**
     * Create a new Action
     *
     * @param donor The donor to be modified
     */
    public ModifyDonorOrgansAction(Donor donor) {
        this.donor = donor;
    }

    /**
     * Add a organ change to the donor. Should check the value is not already set before adding the change
     * @param organ The organ to be updated
     * @param newValue The new value
     */
    public void addChange(Organ organ, Boolean newValue) {
        changes.put(organ, newValue);
    }


    @Override
    public void execute() {
        runChanges(false);
    }

    @Override
    public void unExecute() {
        runChanges(true);
    }

    /**
     * Loops through the list of changes and applies them to the donor
     *
     * @param isUndo If true, negate all booleans
     */
    private void runChanges(boolean isUndo) {
        for (Map.Entry<Organ, Boolean> entry : changes.entrySet()) {
            try {
                Organ organ = entry.getKey();
                boolean newState = entry.getValue();
                if (isUndo) {
                    newState = !newState;
                }
                donor.setOrganStatus(organ, newState);
            } catch (OrganAlreadyRegisteredException e) {
                e.printStackTrace();
            }
        }
    }
}