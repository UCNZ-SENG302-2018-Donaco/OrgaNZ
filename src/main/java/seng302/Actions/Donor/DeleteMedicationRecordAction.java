package seng302.Actions.Donor;

import seng302.Actions.Action;
import seng302.Donor;
import seng302.HistoryItem;
import seng302.MedicationRecord;
import seng302.Utilities.JSONConverter;

public class DeleteMedicationRecordAction implements Action {

    private Donor donor;
    private MedicationRecord record;

    public DeleteMedicationRecordAction(Donor donor, MedicationRecord record) {
        this.donor = donor;
        this.record = record;
        HistoryItem save = new HistoryItem("DELETE_MEDICATION", String.format("Medication %s deleted for %s %s",
                record.getMedicationName(), donor.getFirstName(), donor.getLastName()));
        JSONConverter.updateHistory(save, "action_history.json");
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
