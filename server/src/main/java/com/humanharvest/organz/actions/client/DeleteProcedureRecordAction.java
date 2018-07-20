package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.state.ClientManager;

/**
 * A reversible action that will delete the given procedure record from the given client's medication history.
 */
public class DeleteProcedureRecordAction extends ClientAction {

    private ProcedureRecord record;

    /**
     * Creates a new action to delete an procedure record.
     * @param client The client whose medical history to delete it from.
     * @param record The procedure record to delete.
     * @param manager The ClientManager to apply the changes to
     */
    public DeleteProcedureRecordAction(Client client, ProcedureRecord record, ClientManager manager) {
        super(client, manager);
        this.record = record;
    }

    @Override
    public void execute() {
        super.execute();
        client.deleteProcedureRecord(record);
        manager.applyChangesTo(client);
    }

    @Override
    public void unExecute() {
        super.unExecute();
        client.addProcedureRecord(record);
        manager.applyChangesTo(client);
    }

    @Override
    public String getExecuteText() {
        return String.format("Removed record for procedure '%s' client %d: %s.",
                record.getSummary(), client.getUid(), client.getFullName());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Re-added record for procedure '%s' to client %d: %s.",
                record.getSummary(), client.getUid(), client.getFullName());
    }
}
