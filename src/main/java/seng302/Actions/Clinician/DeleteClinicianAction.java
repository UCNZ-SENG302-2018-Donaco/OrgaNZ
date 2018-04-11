package seng302.Actions.Clinician;

import seng302.Actions.Action;
import seng302.Clinician;
import seng302.State.ClinicianManager;

/**
 * A reversible clinician deletion action
 */
public class DeleteClinicianAction implements Action {

    private Clinician clinician;
    private ClinicianManager manager;

    /**
     * Create a new Action
     * @param clinician The Clinician to be removed
     * @param manager The ClinicianManager to apply changes to
     */
    public DeleteClinicianAction(Clinician clinician, ClinicianManager manager) {
        this.clinician = clinician;
        this.manager = manager;
    }

    @Override
    public void execute() {
        manager.removeClinician(clinician);
    }

    @Override
    public void unExecute() {
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