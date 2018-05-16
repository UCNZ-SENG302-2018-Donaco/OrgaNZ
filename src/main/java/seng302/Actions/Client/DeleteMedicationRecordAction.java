package seng302.Actions.Client;

import seng302.Actions.Action;
import seng302.Client;
import seng302.HistoryItem;
import seng302.HistoryManager;
import seng302.MedicationRecord;

/**
 * A reversible action that will delete the given medication record from the given Client's medication history.
 */
public class DeleteMedicationRecordAction extends Action {

    private Client client;
    private MedicationRecord record;

    /**
     * Creates a new action to delete a medication record.
     * @param client The client whose history to delete it from.
     * @param record The medication record to delete.
     */
    public DeleteMedicationRecordAction(Client client, MedicationRecord record) {
        this.client = client;
        this.record = record;
    }

    @Override
    protected void execute() {
        client.deleteMedicationRecord(record);
        HistoryItem historyItem = new HistoryItem("DELETE_MEDICATION",
                String.format("Medication record for %s deleted from %s",
                        record.getMedicationName(), client.getFullName()));
        HistoryManager.INSTANCE.updateHistory(historyItem);
    }

    @Override
    protected void unExecute() {
        client.addMedicationRecord(record);
    }

    @Override
    public String getExecuteText() {
        return String.format("Removed record for medication '%s' from the history of client %d: %s.",
                record.getMedicationName(), client.getUid(), client.getFullName());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Re-added record for medication '%s' to the history of client %d: %s.",
                record.getMedicationName(), client.getUid(), client.getFullName());
    }
}
