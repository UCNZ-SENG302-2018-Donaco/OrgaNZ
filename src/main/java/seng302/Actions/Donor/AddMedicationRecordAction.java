package seng302.Actions.Donor;

import seng302.Actions.Action;
import seng302.Donor;
import seng302.HistoryItem;
import seng302.MedicationRecord;
import seng302.Utilities.JSONConverter;

public class AddMedicationRecordAction implements Action {

    private Donor donor;
    private MedicationRecord record;

    public AddMedicationRecordAction(Donor donor, MedicationRecord record) {
        this.donor = donor;
        this.record = record;
        HistoryItem save = new HistoryItem("ADD_MEDICATION", String.format("Medication %s added for %s %s",
                record.getMedicationName(), donor.getFirstName(), donor.getLastName()));
        JSONConverter.updateHistory(save, "action_history.json");
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
