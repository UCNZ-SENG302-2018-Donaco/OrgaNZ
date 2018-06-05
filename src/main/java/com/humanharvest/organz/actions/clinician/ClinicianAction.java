package com.humanharvest.organz.actions.clinician;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.actions.Action;

public abstract class ClinicianAction extends Action {

    @Override
    protected void execute() {
        recordInClientHistory();
    }

    @Override
    protected void unExecute() {
        eraseFromClientHistory();
    }

    protected abstract Clinician getAffectedClinician();

    private void recordInClientHistory() {
        getAffectedClinician().addToChangesHistory(getExecuteHistoryItem());
    }

    private void eraseFromClientHistory() {
        HistoryItem toErase = null;
        for (HistoryItem item : getAffectedClinician().getChangesHistory()) {
            if (item.getDetails().equals(getExecuteHistoryItem().getDetails())) {
                toErase = item;
                break;
            }
        }
        getAffectedClinician().removeFromChangesHistory(toErase);
    }
}
