package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;

/**
 * A reversible action that will delete the given illness record from the given client's medication history.
 */
public class DeleteIllnessRecordAction extends Action {

    private Client client;
    private IllnessRecord record;

    /**
     * Creates a new action to delete an illness record.
     * @param client The client whose medical history to delete it from.
     * @param record The illness record to delete.
     */
    public DeleteIllnessRecordAction(Client client, IllnessRecord record) {
        this.client = client;
        this.record = record;
    }

    @Override
    public void execute() {
        client.deleteIllnessRecord(record);

    }

    @Override
    public void unExecute() {
        client.addIllnessRecord(record);
    }

    @Override
    public String getExecuteText() {
        return String.format("Removed record for illness '%s' from the history of client %d: %s.",
                record.getIllnessName(), client.getUid(), client.getFullName());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Re-added record for illness '%s' to the history of client %d: %s.",
                record.getIllnessName(), client.getUid(), client.getFullName());
    }
}
