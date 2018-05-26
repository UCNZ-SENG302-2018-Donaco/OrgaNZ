package seng302.Actions.Client;

import seng302.Actions.Action;
import seng302.Client;
import seng302.ProcedureRecord;
import seng302.State.ClientManager;

/**
 * A reversible action that will delete the given procedure record from the given client's medication history.
 */
public class DeleteProcedureRecordAction extends Action {

    private Client client;
    private ProcedureRecord record;
    private ClientManager manager;

    /**
     * Creates a new action to delete an procedure record.
     * @param client The client whose medical history to delete it from.
     * @param record The procedure record to delete.
     */
    public DeleteProcedureRecordAction(Client client, ProcedureRecord record, ClientManager manager) {
        this.client = client;
        this.record = record;
        this.manager = manager;
    }

    @Override
    public void execute() {
        client.deleteProcedureRecord(record);
        manager.applyChangesTo(client);
    }

    @Override
    public void unExecute() {
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
