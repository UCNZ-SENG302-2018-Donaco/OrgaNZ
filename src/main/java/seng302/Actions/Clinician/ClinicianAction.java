package seng302.Actions.Clinician;

import seng302.Actions.Action;
import seng302.Clinician;
import seng302.HistoryItem;

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
        HistoryItem item = new HistoryItem("ACTION", this.getExecuteText());
        getAffectedClinician().addToChangesHistory(item);
    }

    private void eraseFromClientHistory() {
        HistoryItem toErase = null;
        for (HistoryItem item : getAffectedClinician().getChangesHistory()) {
            if (item.getDetails().equals(this.getExecuteText())) {
                toErase = item;
                break;
            }
        }
        getAffectedClinician().removeFromChangesHistory(toErase);
    }
}
