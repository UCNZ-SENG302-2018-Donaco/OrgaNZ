package seng302.Actions.Donor;

import java.util.ArrayList;

import seng302.Actions.Action;
import seng302.Actions.ModifyObjectByFieldAction;
import seng302.Donor;

/**
 * A reversible donor modification Action
 */
public class ModifyDonorAction implements Action {

    private ArrayList<ModifyObjectByFieldAction> actions = new ArrayList<>();
    private Donor donor;

    /**
     * Create a new Action
     * @param donor The donor to be modified
     */
    public ModifyDonorAction(Donor donor) {
        this.donor = donor;
    }

    /**
     * Add a modification to the donor
     * @param field The setter field of the donor. Must match a valid setter in the Donor object
     * @param oldValue The object the field initially had. Should be taken from the Donors equivalent getter
     * @param newValue The object the field should be update to. Must match the setters Object type
     * @throws NoSuchMethodException Thrown if the Donor does not have the specified setter
     * @throws NoSuchFieldException Thrown if the Donors specified setter does not take the same type as given in one of
     * the values
     */
    public void addChange(String field, Object oldValue, Object newValue)
            throws NoSuchMethodException, NoSuchFieldException {
        actions.add(new ModifyObjectByFieldAction(donor, field, oldValue, newValue));
    }

    @Override
    public void execute() {
        for (ModifyObjectByFieldAction action : actions) {
            action.execute();
        }
    }

    @Override
    public void unExecute() {
        for (ModifyObjectByFieldAction action : actions) {
            action.unExecute();
        }
    }
}
