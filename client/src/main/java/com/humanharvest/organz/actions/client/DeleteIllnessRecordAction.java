package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.state.ClientManager;

/**
 * A reversible action that will delete the given illness record from the given client's medication history.
 */
public class DeleteIllnessRecordAction extends ClientAction {

    private IllnessRecord record;

    /**
     * Creates a new action to delete an illness record.
     * @param client The client whose medical history to delete it from.
     * @param record The illness record to delete.
     * @param manager The ClientManager to apply the changes to
     */
    public DeleteIllnessRecordAction(Client client, IllnessRecord record, ClientManager manager) {
        super(client, manager);
        this.record = record;
    }

    @Override
    public void execute() {
        super.execute();
        client.deleteIllnessRecord(record);
        manager.applyChangesTo(client);
    }

    @Override
    public void unExecute() {
        super.unExecute();
        client.addIllnessRecord(record);
        manager.applyChangesTo(client);
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
