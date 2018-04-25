package seng302.Actions.Donor;

import seng302.Actions.Action;
import seng302.Donor;
import seng302.HistoryItem;
import seng302.MedicationRecord;
import seng302.Utilities.JSONConverter;

/**
 * A reversible action that will add the given medication record to the given Donor's medication history.
 */
public class AddMedicationRecordAction implements Action {

    private Donor donor;
    private MedicationRecord record;

    /**
     * Creates a new action to add a medication record.
     * @param donor The donor whose history to add it to.
     * @param record The medication record to add.
     */
    public AddMedicationRecordAction(Donor donor, MedicationRecord record) {
        this.donor = donor;
        this.record = record;
    }

    @Override
    public void execute() {
        donor.addMedicationRecord(record);
        HistoryItem save = new HistoryItem("ADD_MEDICATION",
                String.format("Medication record for %s added to %s %s",
                        record.getMedicationName(), donor.getFirstName(), donor.getLastName()));
        JSONConverter.updateHistory(save, "action_history.json");
    }

    @Override
    public void unExecute() {
        donor.deleteMedicationRecord(record);
    }
}
