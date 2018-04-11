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

    @Override
    public String getExecuteText() {
        return String.format("Added medication %s record for donor %s", record.getMedicationName(), donor
                .getFullName());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Removed medication %s record for donor %s", record.getMedicationName(), donor
                .getFullName());
    }
}
