package seng302.Actions.Donor;

import java.time.LocalDate;
import java.util.Objects;

import seng302.Actions.Action;
import seng302.MedicationRecord;

public class ModifyMedicationRecordAction extends Action {

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
    protected void execute() {
        record.setStarted(newStarted);
        record.setStopped(newStopped);
    }

    @Override
    protected void unExecute() {
        record.setStarted(oldStarted);
        record.setStopped(oldStopped);
    }

    @Override
    public String getExecuteText() {
        return String.format("Updated medication %s record", record.getMedicationName());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Update of medication %s record has been undone", record.getMedicationName());
    }
}
