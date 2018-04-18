package seng302.Actions.Client;

import seng302.Actions.Action;
import seng302.Client;
import seng302.MedicationRecord;

public class AddMedicationRecordAction implements Action {

    private Client client;
    private MedicationRecord record;

    public AddMedicationRecordAction(Client client, MedicationRecord record) {
        this.client = client;
        this.record = record;
    }

    @Override
    public void execute() {
        client.addMedicationRecord(record);
    }

    @Override
    public void unExecute() {
        client.deleteMedicationRecord(record);
    }
}
