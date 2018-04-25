package seng302.Actions.Donor;

import seng302.Actions.Action;
import seng302.Donor;
import seng302.HistoryItem;
import seng302.MedicationRecord;
import seng302.Utilities.JSONConverter;

/**
 * A reversible action that will delete the given medication record from the given Donor's medication history.
 */
public class DeleteMedicationRecordAction implements Action {

    private Donor donor;
    private MedicationRecord record;

    /**
     * Creates a new action to delete a medication record.
     * @param donor The donor whose history to delete it from.
     * @param record The medication record to delete.
     */
    public DeleteMedicationRecordAction(Donor donor, MedicationRecord record) {
        this.donor = donor;
        this.record = record;
    }

    @Override
    public void execute() {
        donor.deleteMedicationRecord(record);
        HistoryItem save = new HistoryItem("DELETE_MEDICATION",
                String.format("Medication record for %s deleted from %s %s",
                        record.getMedicationName(), donor.getFirstName(), donor.getLastName()));
        JSONConverter.updateHistory(save, "action_history.json");
    }

    @Override
    public void unExecute() {
        donor.addMedicationRecord(record);
    }
}
