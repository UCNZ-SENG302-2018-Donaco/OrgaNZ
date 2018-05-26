package seng302.Actions.Administrator;

import seng302.Actions.Action;
import seng302.Administrator;
import seng302.HistoryItem;

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
        HistoryItem item = new HistoryItem("ACTION", this.getExecuteText());
        getAffectedAdministrator().addToChangesHistory(item);
    }

    private void eraseFromClientHistory() {
        HistoryItem toErase = null;
        for (HistoryItem item : getAffectedAdministrator().getChangesHistory()) {
            if (item.getDetails().equals(this.getExecuteText())) {
                toErase = item;
                break;
            }
        }
        getAffectedAdministrator().removeFromChangesHistory(toErase);
    }
}
