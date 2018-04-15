package seng302.Actions.Donor;

import seng302.Actions.Action;
import seng302.Donor;
import seng302.MedicationRecord;

public class DeleteMedicationRecordAction implements Action {

    private Donor donor;
    private MedicationRecord record;

    public DeleteMedicationRecordAction(Donor donor, MedicationRecord record) {
        this.donor = donor;
        this.record = record;
    }

    @Override
    public void execute() {
        donor.deleteMedicationRecord(record);
    }

    @Override
    public void unExecute() {
        donor.addMedicationRecord(record);
    }
}
