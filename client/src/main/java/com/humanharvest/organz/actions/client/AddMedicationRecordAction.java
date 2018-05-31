package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.utilities.JSONConverter;

/**
 * A reversible action that will add the given medication record to the given Client's medication history.
 */
public class AddMedicationRecordAction extends Action {

    private Client client;
    private MedicationRecord record;
    private ClientManager manager;

    /**
     * Creates a new action to add a medication record.
     * @param client The client whose history to add it to.
     * @param record The medication record to add.
     */
    public AddMedicationRecordAction(Client client, MedicationRecord record, ClientManager manager) {
        this.client = client;
        this.record = record;
        this.manager = manager;
    }

    @Override
    protected void execute() {
        client.addMedicationRecord(record);
        manager.applyChangesTo(client);
        HistoryItem save = new HistoryItem("ADD_MEDICATION",
                String.format("Medication record for %s added to %s %s",
                        record.getMedicationName(), client.getFirstName(), client.getLastName()));
        JSONConverter.updateHistory(save, "action_history.json");
    }

    @Override
    protected void unExecute() {
        client.deleteMedicationRecord(record);
        manager.applyChangesTo(client);
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
