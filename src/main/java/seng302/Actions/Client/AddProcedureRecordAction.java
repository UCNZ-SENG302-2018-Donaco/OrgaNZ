package seng302.Actions.Client;

import seng302.Actions.Action;
import seng302.Client;
import seng302.ProcedureRecord;

/**
 * A reversible action that will add the given procedure record to the given client's medical history.
 */
public class AddProcedureRecordAction extends Action {

    private Client client;
    private ProcedureRecord record;

    /**
     * Creates a new action to add an procedure record.
     * @param client The client whose medical history to add to.
     * @param record The procedure record to add.
     */
    public AddProcedureRecordAction(Client client, ProcedureRecord record) {
        this.client = client;
        this.record = record;
    }

    @Override
    public void execute() {
        client.addProcedureRecord(record);

    }

    @Override
    public void unExecute() {
        client.deleteProcedureRecord(record);
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
