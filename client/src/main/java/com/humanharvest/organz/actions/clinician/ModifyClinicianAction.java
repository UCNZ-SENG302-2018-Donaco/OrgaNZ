package com.humanharvest.organz.actions.clinician;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.ModifyObjectByFieldAction;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.actions.ModifyObjectByFieldAction;
import com.humanharvest.organz.state.ClinicianManager;

/**
 * A reversible clinician modification Action
 */
public class ModifyClinicianAction extends ClinicianAction {

    private ArrayList<ModifyObjectByFieldAction> actions = new ArrayList<>();
    /**
     * Create a new Action
     * @param clinician The clinician to be modified
     */
    public ModifyClinicianAction(Clinician clinician,ClinicianManager manager) {
        super(clinician, manager);
    }

    /**
     * Add a modification to the clinician
     * @param field The setter field of the clinician. Must match a valid setter in the Clinician object
     * @param oldValue The object the field initially had. Should be taken from the Clinicians equivalent getter
     * @param newValue The object the field should be update to. Must match the setters Object type
     * @throws NoSuchMethodException Thrown if the Clinician does not have the specified setter
     * @throws NoSuchFieldException Thrown if the Clinicians specified setter does not take the same type as given in
     * one of the values
     */
    public void addChange(String field, Object oldValue, Object newValue)
            throws NoSuchMethodException, NoSuchFieldException {
        if (field.equals("setPassword")) {
            actions.add(new ModifyObjectByFieldAction(clinician, field, oldValue, newValue, true));
        } else {
            actions.add(new ModifyObjectByFieldAction(clinician, field, oldValue, newValue, false));
        }
    }

    @Override
    protected void execute() {
        if (actions.size() == 0) {
            throw new IllegalStateException("No changes were made to the clinician.");
        } else {
            super.execute();
            for (ModifyObjectByFieldAction action : actions) {
                action.execute();
            }
            manager.applyChangesTo(clinician);
        }
    }

    @Override
    protected void unExecute() {
        super.unExecute();
        for (ModifyObjectByFieldAction action : actions) {
            action.unExecute();
        }
        manager.applyChangesTo(clinician);
    }

    @Override
    public String getExecuteText() {
        String changesText = actions.stream()
                .map(ModifyObjectByFieldAction::getExecuteText)
                .collect(Collectors.joining("\n"));

        return String.format("Updated details for clinician %d: %s %s. \n"
                        + "These changes were made: \n\n%s",
                clinician.getStaffId(), clinician.getFirstName(), clinician.getLastName(), changesText);
    }

    @Override
    public String getUnexecuteText() {
        String changesText = actions.stream()
                .map(ModifyObjectByFieldAction::getExecuteText)
                .collect(Collectors.joining("\n"));

        return String.format("Reversed update for clinician %d: %s %s. \n"
                        + "These changes were reversed: \n\n%s",
                clinician.getStaffId(), clinician.getFirstName(), clinician.getLastName(), changesText);
    }
}
