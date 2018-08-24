package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.state.ClientManager;

import java.time.LocalDate;
import java.util.Objects;

/**
 * A reversible action to modify a given medication record (specifically, its 'started' and 'stopped' dates).
 */
public class ModifyMedicationRecordAction extends ClientAction {

    private MedicationRecord record;
    private LocalDate oldStarted;
    private LocalDate oldStopped;
    private LocalDate newStarted;
    private LocalDate newStopped;

    /**
     * Creates a new action to modify a medication record. Will initialise the new started/stopped dates to be the
     * same as the current ones.
     *
     * @param record The medication record to modify.
     */
    public ModifyMedicationRecordAction(MedicationRecord record, ClientManager manager) {
        super(record.getClient(), manager);
        this.record = record;
        this.oldStarted = record.getStarted();
        this.oldStopped = record.getStopped();
        this.newStarted = oldStarted;
        this.newStopped = oldStopped;
    }

    /**
     * Make the action change the medication record's started date to the one given.
     *
     * @param newStarted The new started date.
     */
    public void changeStarted(LocalDate newStarted) {
        this.newStarted = newStarted;
    }

    /**
     * Make the action change the medication record's stopped date to the one given.
     *
     * @param newStopped The new started date.
     */
    public void changeStopped(LocalDate newStopped) {
        this.newStopped = newStopped;
    }

    /**
     * Apply all changes to the medication record.
     *
     * @throws IllegalStateException If no changes were made.
     */
    @Override
    protected void execute() {
        if (Objects.equals(newStarted, oldStarted) && Objects.equals(newStopped, oldStopped)) {
            throw new IllegalStateException("No changes were made to the MedicationRecord.");
        }
        super.execute();
        if (!Objects.equals(newStarted, oldStarted)) {
            record.setStarted(newStarted);
        }
        if (!Objects.equals(newStopped, oldStopped)) {
            record.setStopped(newStopped);
        }
        manager.applyChangesTo(record.getClient());
    }

    @Override
    protected void unExecute() {
        super.unExecute();
        if (!Objects.equals(newStarted, oldStarted)) {
            record.setStarted(oldStarted);
        }
        if (!Objects.equals(newStopped, oldStopped)) {
            record.setStopped(oldStopped);
        }
        manager.applyChangesTo(record.getClient());
    }

    @Override
    public String getExecuteText() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Changed medication record for '%s':", record.getMedicationName()));

        if (!Objects.equals(newStarted, oldStarted)) {
            builder.append(String.format("\nStarted date changed from %s to %s", oldStarted, newStarted));
        }
        if (!Objects.equals(newStopped, oldStopped)) {
            builder.append(String.format("\nStopped date changed from %s to %s", oldStarted, newStarted));
        }

        return builder.toString();
    }

    @Override
    public String getUnexecuteText() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Reversed these changes to medication record for '%s':",
                record.getMedicationName()));

        if (!Objects.equals(newStarted, oldStarted)) {
            builder.append(String.format("\nStarted date changed from %s to %s", oldStarted, newStarted));
        }
        if (!Objects.equals(newStopped, oldStopped)) {
            builder.append(String.format("\nStopped date changed from %s to %s", oldStopped, newStopped));
        }

        return builder.toString();
    }
}
