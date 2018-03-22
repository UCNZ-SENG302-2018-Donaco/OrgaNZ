package seng302.Actions;

import seng302.Clinician;

import java.util.ArrayList;

/**
 * A reversible clinician modification Action
 */
public class ModifyClinicianAction implements Action {

    private ArrayList<ModifyObjectByFieldAction> actions = new ArrayList<>();
    private Clinician clinician;

    /**
     * Create a new Action
     * @param clinician The clinician to be modified
     */
    public ModifyClinicianAction(Clinician clinician) {
        this.clinician = clinician;
    }

    /**
     * Add a modification to the clinician
     *
     * @param field    The setter field of the clinician. Must match a valid setter in the Clinician object
     * @param oldValue The object the field initially had. Should be taken from the Clinicians equivalent getter
     * @param newValue The object the field should be update to. Must match the setters Object type
     */
    public void addChange(String field, Object oldValue, Object newValue) throws NoSuchMethodException, NoSuchFieldException {
        actions.add(new ModifyObjectByFieldAction(clinician, field, oldValue, newValue));
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
