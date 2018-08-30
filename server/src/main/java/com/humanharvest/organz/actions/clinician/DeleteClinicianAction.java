package com.humanharvest.organz.actions.clinician;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.state.ClinicianManager;

/**
 * A reversible clinician deletion action
 */
public class DeleteClinicianAction extends ClinicianAction {

    /**
     * Create a new Action
     *
     * @param clinician The Clinician to be removed
     * @param manager The ClinicianManager to apply changes to
     */
    public DeleteClinicianAction(Clinician clinician, ClinicianManager manager) {
        super(clinician, manager);
    }

    @Override
    protected void execute() {
        super.execute();
        manager.removeClinician(clinician);
    }

    @Override
    protected void unExecute() {
        super.unExecute();
        manager.addClinician(clinician);
    }

    @Override
    public String getExecuteText() {
        return String.format("Deleted clinician %s", clinician.getFullName());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Re-added clinician %s", clinician.getFullName());
    }
}
