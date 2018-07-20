package com.humanharvest.organz.actions.administrator;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.actions.Action;

public abstract class AdministratorAction extends Action {

    final Administrator administrator;

    AdministratorAction(Administrator administrator) {
        this.administrator = administrator;
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
        return administrator;
    }

    private void recordInClientHistory() {
        administrator.addToChangesHistory(getExecuteHistoryItem());
    }

    private void eraseFromClientHistory() {
        administrator.removeFromChangesHistory(getExecuteHistoryItem());
    }
}
