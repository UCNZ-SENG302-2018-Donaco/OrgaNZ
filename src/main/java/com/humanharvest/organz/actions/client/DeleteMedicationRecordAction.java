package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.state.ClientManager;

/**
 * A reversible action that will delete the given medication record from the given Client's medication history.
 */
public class DeleteMedicationRecordAction extends ClientAction {

    private Client client;
    private MedicationRecord record;
    private ClientManager manager;

    /**
     * Creates a new action to delete a medication record.
     * @param client The client whose history to delete it from.
     * @param record The medication record to delete.
     */
    public DeleteMedicationRecordAction(Client client, MedicationRecord record, ClientManager manager) {
        this.client = client;
        this.record = record;
        this.manager = manager;
    }

    @Override
    protected void execute() {
        super.execute();
        client.deleteMedicationRecord(record);
        manager.applyChangesTo(client);
    }

    @Override
    protected void unExecute() {
        super.unExecute();
        client.addMedicationRecord(record);
        manager.applyChangesTo(client);
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

    @Override
    protected Client getAffectedClient() {
        return client;
    }
}
