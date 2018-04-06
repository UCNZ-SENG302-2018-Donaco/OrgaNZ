package seng302.Actions.Donor;

import java.time.LocalDate;

import seng302.Actions.Action;
import seng302.MedicationRecord;

public class ModifyMedicationRecordAction implements Action {

    private MedicationRecord record;
    private LocalDate oldStarted;
    private LocalDate oldStopped;
    private LocalDate newStarted;
    private LocalDate newStopped;

    public ModifyMedicationRecordAction(MedicationRecord record) {
        this.record = record;
        this.oldStarted = record.getStarted();
        this.oldStopped = record.getStopped();
        this.newStarted = oldStarted;
        this.newStopped = oldStopped;
    }

    public void changeStarted(LocalDate newStarted) {
        this.newStarted = newStarted;
    }

    public void changeStopped(LocalDate newStopped) {
        this.newStopped = newStopped;
    }

    @Override
    public void execute() {
        if (newStarted == oldStarted && newStopped == oldStopped) {
            throw new IllegalStateException("No changes were made to the MedicationRecord.");
        }
        if (newStarted != oldStarted) {
            record.setStarted(newStarted);
        }
        if (newStopped != oldStopped) {
            record.setStopped(newStopped);
        }
    }

    @Override
    public void unExecute() {
        if (newStarted != oldStarted) {
            record.setStarted(oldStarted);
        }
        if (newStopped != oldStopped) {
            record.setStopped(oldStopped);
        }
    }
}
