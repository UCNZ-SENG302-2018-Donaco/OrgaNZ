package com.humanharvest.organz.actions.administrator;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.actions.Action;

public abstract class AdministratorAction extends Action {

    @Override
    protected void execute() {
        recordInClientHistory();
    }

    @Override
    protected void unExecute() {
        eraseFromClientHistory();
    }

    protected abstract Administrator getAffectedAdministrator();

    private void recordInClientHistory() {
        getAffectedAdministrator().addToChangesHistory(getExecuteHistoryItem());
    }

    private void eraseFromClientHistory() {
        HistoryItem toErase = null;
        for (HistoryItem item : getAffectedAdministrator().getChangesHistory()) {
            if (item.getDetails().equals(getExecuteHistoryItem().getDetails())) {
                toErase = item;
                break;
            }
        }
        getAffectedAdministrator().removeFromChangesHistory(toErase);
    }
}
