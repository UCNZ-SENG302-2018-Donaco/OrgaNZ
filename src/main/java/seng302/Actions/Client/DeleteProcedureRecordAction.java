package seng302.Actions.Client;

import seng302.Client;
import seng302.ProcedureRecord;

/**
 * A reversible action that will delete the given procedure record from the given client's medication history.
 */
public class DeleteProcedureRecordAction extends ClientAction {

    private Client client;
    private ProcedureRecord record;

    /**
     * Creates a new action to delete an procedure record.
     * @param client The client whose medical history to delete it from.
     * @param record The procedure record to delete.
     */
    public DeleteProcedureRecordAction(Client client, ProcedureRecord record) {
        this.client = client;
        this.record = record;
    }

    @Override
    public void execute() {
        super.execute();
        client.deleteProcedureRecord(record);
    }

    @Override
    public void unExecute() {
        super.unExecute();
        client.addProcedureRecord(record);
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

    @Override
    protected Client getAffectedClient() {
        return client;
    }
}
