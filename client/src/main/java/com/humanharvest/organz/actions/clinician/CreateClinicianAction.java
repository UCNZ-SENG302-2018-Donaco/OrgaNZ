package com.humanharvest.organz.actions.clinician;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.state.ClinicianManager;

/**
 * A reversible clinician creation action
 */
public class CreateClinicianAction extends ClinicianAction {

    /**
     * Create a new Action
     * @param clinician The Clinician to be created
     * @param manager The ClinicianManager to apply changes to
     */
    public CreateClinicianAction(Clinician clinician, ClinicianManager manager) {
        super(clinician, manager);
    }

    /**
     * Simply add the clinician to the ClinicianManager
     */
    @Override
    protected void execute() {
        super.execute();
        manager.addClinician(clinician);
    }

    /**
     * Simply remove the clinician from the ClinicianManager
     */
    @Override
    protected void unExecute() {
        super.unExecute();
        manager.removeClinician(clinician);
    }

    @Override
    public String getExecuteText() {
        return String.format("Created clinician %s with staff ID %d", clinician.getFullName(), clinician.getStaffId());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Removed clinician %s", clinician.getFullName());
    }
}
