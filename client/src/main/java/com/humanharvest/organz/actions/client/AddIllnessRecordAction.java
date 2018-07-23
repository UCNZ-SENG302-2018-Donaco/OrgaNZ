package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.state.ClientManager;

/**
 * A reversible action that will add the given illness record to the given client's medical history.
 */
public class AddIllnessRecordAction extends ClientAction {

    private IllnessRecord record;

    /**
     * Creates a new action to add an illness record.
     * @param client The client whose medical history to add to.
     * @param record The illness record to add.
     * @param manager The ClientManager to apply the changes to
     */
    public AddIllnessRecordAction(Client client, IllnessRecord record, ClientManager manager) {
        super(client, manager);
        this.record = record;
    }

    @Override
    public void execute() {
        super.execute();
        client.addIllnessRecord(record);
        manager.applyChangesTo(client);
    }

    @Override
    public void unExecute() {
        super.unExecute();
        client.deleteIllnessRecord(record);
        manager.applyChangesTo(client);
    }

    @Override
    public String getExecuteText() {
        return String.format("Added record for illness '%s' to the history of client %d: %s.",
                record.getIllnessName(), client.getUid(), client.getFullName());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Reversed the addition of record for illness '%s' from the history of client %d: %s.",
                record.getIllnessName(), client.getUid(), client.getFullName());
    }
}
