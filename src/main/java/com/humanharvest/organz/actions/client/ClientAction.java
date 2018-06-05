package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.actions.Action;

public abstract class ClientAction extends Action {

    @Override
    protected void execute() {
        recordInClientHistory();
    }

    @Override
    protected void unExecute() {
        eraseFromClientHistory();
    }

    protected abstract Client getAffectedClient();

    private void recordInClientHistory() {
        getAffectedClient().addToChangesHistory(getExecuteHistoryItem());
    }

    private void eraseFromClientHistory() {
        HistoryItem toErase = null;
        for (HistoryItem item : getAffectedClient().getChangesHistory()) {
            if (item.getDetails().equals(getExecuteHistoryItem().getDetails())) {
                toErase = item;
                break;
            }
        }
        getAffectedClient().removeFromChangesHistory(toErase);
    }
}
