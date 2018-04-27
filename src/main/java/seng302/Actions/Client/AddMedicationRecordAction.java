package seng302.Actions.Client;

import seng302.Actions.Action;
import seng302.Client;
import seng302.HistoryItem;
import seng302.MedicationRecord;
import seng302.Utilities.JSONConverter;

/**
 * A reversible action that will add the given medication record to the given Client's medication history.
 */
public class AddMedicationRecordAction extends Action {

    private Client client;
    private MedicationRecord record;

    /**
     * Creates a new action to add a medication record.
     * @param client The client whose history to add it to.
     * @param record The medication record to add.
     */
    public AddMedicationRecordAction(Client client, MedicationRecord record) {
        this.client = client;
        this.record = record;
    }

    @Override
    protected void execute() {
        client.addMedicationRecord(record);
        HistoryItem save = new HistoryItem("ADD_MEDICATION",
                String.format("Medication record for %s added to %s %s",
                        record.getMedicationName(), client.getFirstName(), client.getLastName()));
        JSONConverter.updateHistory(save, "action_history.json");
    }

    @Override
    protected void unExecute() {
        client.deleteMedicationRecord(record);
    }

    @Override
    public String getExecuteText() {
        return String.format("Added record for medication '%s' to the history of client %d: %s.",
                record.getMedicationName(), client.getUid(), client.getFullName());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Reversed the addition of record for medication '%s' to the history of client %d: %s.",
                record.getMedicationName(), client.getUid(), client.getFullName());
    }
}
