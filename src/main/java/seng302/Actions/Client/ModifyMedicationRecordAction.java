package seng302.Actions.Client;

import java.time.LocalDate;
import java.util.Objects;

import seng302.Actions.Action;
import seng302.HistoryItem;
import seng302.MedicationRecord;
import seng302.Utilities.JSONConverter;

/**
 * A reversible action to modify a given medication record (specifically, its 'started' and 'stopped' dates).
 */
public class ModifyMedicationRecordAction implements Action {

    private MedicationRecord record;
    private LocalDate oldStarted;
    private LocalDate oldStopped;
    private LocalDate newStarted;
    private LocalDate newStopped;

    /**
     * Creates a new action to modify a medication record. Will initialise the new started/stopped dates to be the
     * same as the current ones.
     * @param record The medication record to modify.
     */
    public ModifyMedicationRecordAction(MedicationRecord record) {
        this.record = record;
        this.oldStarted = record.getStarted();
        this.oldStopped = record.getStopped();
        this.newStarted = oldStarted;
        this.newStopped = oldStopped;
    }

    /**
     * Make the action change the medication record's started date to the one given.
     * @param newStarted The new started date.
     */
    public void changeStarted(LocalDate newStarted) {
        this.newStarted = newStarted;
    }

    /**
     * Make the action change the medication record's stopped date to the one given.
     * @param newStopped The new started date.
     */
    public void changeStopped(LocalDate newStopped) {
        this.newStopped = newStopped;
    }

    /**
     * Apply all changes to the medication record.
     * @throws IllegalStateException If no changes were made.
     */
    @Override
    public void execute() {
        if (Objects.equals(newStarted, oldStarted) && Objects.equals(newStopped, oldStopped)) {
            throw new IllegalStateException("No changes were made to the MedicationRecord.");
        }
        if (!Objects.equals(newStarted, oldStarted)) {
            record.setStarted(newStarted);
        }
        if (!Objects.equals(newStopped, oldStopped)) {
            record.setStopped(newStopped);
        }
        HistoryItem save = new HistoryItem("MODIFY_MEDICATION",
                String.format("Medication record for %s changed. New started date: %s. New stopped date: %s",
                        record.getMedicationName(), record.getStarted(), record.getStopped()));
        JSONConverter.updateHistory(save, "action_history.json");
    }

    @Override
    public void unExecute() {
        if (!Objects.equals(newStarted, oldStarted)) {
            record.setStarted(oldStarted);
        }
        if (!Objects.equals(newStopped, oldStopped)) {
            record.setStopped(oldStopped);
        }
    }
}
