package com.humanharvest.organz.actions.clinician;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.state.ClinicianManager;

public abstract class ClinicianAction extends Action {

    final Clinician clinician;
    final ClinicianManager manager;

    ClinicianAction(Clinician clinician, ClinicianManager manager) {
        this.clinician = clinician;
        this.manager = manager;
    }

    @Override
    protected void execute() {
        recordInClientHistory();
    }

    @Override
    protected void unExecute() {
        eraseFromClientHistory();
    }

    @Override
    public Object getModifiedObject() {
        return clinician;
    }

    private void recordInClientHistory() {
        clinician.addToChangesHistory(getExecuteHistoryItem());
    }

    private void eraseFromClientHistory() {
        clinician.removeFromChangesHistory(getExecuteHistoryItem());
    }
}
