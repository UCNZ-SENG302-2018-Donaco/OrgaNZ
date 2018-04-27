package seng302.Actions.Person;

import java.time.LocalDate;
import java.util.Objects;

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
        if (Objects.equals(newStarted, oldStarted) && Objects.equals(newStopped, oldStopped)) {
            throw new IllegalStateException("No changes were made to the MedicationRecord.");
        }
        if (!Objects.equals(newStarted, oldStarted)) {
            record.setStarted(newStarted);
        }
        if (!Objects.equals(newStopped, oldStopped)) {
            record.setStopped(newStopped);
        }
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
