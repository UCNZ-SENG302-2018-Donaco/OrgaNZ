package seng302.Actions.Client;

import seng302.Client;
import seng302.MedicationRecord;
import seng302.State.ClientManager;

/**
 * A reversible action that will add the given medication record to the given Client's medication history.
 */
public class AddMedicationRecordAction extends ClientAction {

    private Client client;
    private MedicationRecord record;
    private ClientManager manager;

    /**
     * Creates a new action to add a medication record.
     * @param client The client whose history to add it to.
     * @param record The medication record to add.
     */
    public AddMedicationRecordAction(Client client, MedicationRecord record, ClientManager manager) {
        this.client = client;
        this.record = record;
        this.manager = manager;
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

    @Override
    protected Client getAffectedClient() {
        return client;
    }
}
