package seng302.Actions.Client;

import seng302.Actions.Action;
import seng302.Client;
import seng302.MedicationRecord;

public class DeleteMedicationRecordAction implements Action {

    private Client client;
    private MedicationRecord record;

    public DeleteMedicationRecordAction(Client client, MedicationRecord record) {
        this.client = client;
        this.record = record;
    }

    @Override
    public void execute() {
        client.deleteMedicationRecord(record);
    }

    @Override
    public void unExecute() {
        client.addMedicationRecord(record);
    }
}
