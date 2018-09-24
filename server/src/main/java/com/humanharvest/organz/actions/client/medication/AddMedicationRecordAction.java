package com.humanharvest.organz.actions.client.medication;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.actions.client.ClientAction;
import com.humanharvest.organz.state.ClientManager;

/**
 * A reversible action that will add the given medication record to the given Client's medication history.
 */
public class AddMedicationRecordAction extends ClientAction {

    private MedicationRecord record;

    /**
     * Creates a new action to add a medication record.
     *
     * @param client The client whose history to add it to.
     * @param record The medication record to add.
     * @param manager The ClientManager to apply the changes to
     */
    public AddMedicationRecordAction(Client client, MedicationRecord record, ClientManager manager) {
        super(client, manager);
        this.record = record;
    }

    @Override
    protected void execute() {
        super.execute();
        client.addMedicationRecord(record);
        manager.applyChangesTo(client);
    }

    @Override
    protected void unExecute() {
        super.unExecute();
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
