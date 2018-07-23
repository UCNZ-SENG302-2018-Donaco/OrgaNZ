package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.state.ClientManager;

/**
 * A reversible action that will add the given procedure record to the given client's medical history.
 */
public class AddProcedureRecordAction extends ClientAction {

    private ProcedureRecord record;

    /**
     * Creates a new action to add an procedure record.
     * @param client The client whose medical history to add to.
     * @param record The procedure record to add.
     * @param manager The ClientManager to apply the changes to
     */
    public AddProcedureRecordAction(Client client, ProcedureRecord record, ClientManager manager) {
        super(client, manager);
        this.record = record;
    }

    @Override
    public void execute() {
        super.execute();
        client.addProcedureRecord(record);
        manager.applyChangesTo(client);
    }

    @Override
    public void unExecute() {
        super.unExecute();
        client.deleteProcedureRecord(record);
        manager.applyChangesTo(client);
    }

    @Override
    public String getExecuteText() {
        return String.format("Added record for procedure '%s' to client %d: %s.",
                record.getSummary(), client.getUid(), client.getFullName());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Reversed the addition of record for procedure '%s' from client %d: %s.",
                record.getSummary(), client.getUid(), client.getFullName());
    }
}
