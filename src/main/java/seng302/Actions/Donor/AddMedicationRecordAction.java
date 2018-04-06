package seng302.Actions.Donor;

import seng302.Actions.Action;
import seng302.Donor;
import seng302.MedicationRecord;

public class AddMedicationRecordAction implements Action {

    private Donor donor;
    private MedicationRecord record;

    public AddMedicationRecordAction(Donor donor, MedicationRecord record) {
        this.donor = donor;
        this.record = record;
    }

    @Override
    public void execute() {
        donor.addMedicationRecord(record);
    }

    @Override
    public void unExecute() {
        donor.deleteMedicationRecord(record);
    }
}
