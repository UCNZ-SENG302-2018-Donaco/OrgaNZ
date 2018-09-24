package com.humanharvest.organz.actions.client.illness;

import java.time.LocalDate;
import java.util.Objects;

import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.actions.client.ClientAction;
import com.humanharvest.organz.state.ClientManager;

/**
 * A reversible action to modify a given illness record. Only the diagnosis date, cured date and chronic status
 * attributes of the record can be changed.
 */
public class ModifyIllnessRecordAction extends ClientAction {

    private IllnessRecord record;
    private LocalDate oldDiagnosisDate;
    private LocalDate oldCuredDate;
    private LocalDate newDiagnosisDate;
    private LocalDate newCuredDate;
    private boolean oldChronic;
    private boolean newChronic;

    /**
     * Creates a new action to modify a illness record. Will initialise the new diagnosis/cured dates and new chronic
     * status to be the same as the current ones.
     *
     * @param record The illness record to modify.
     */
    public ModifyIllnessRecordAction(IllnessRecord record, ClientManager manager) {
        super(record.getClient(), manager);
        this.record = record;
        oldDiagnosisDate = record.getDiagnosisDate();
        oldCuredDate = record.getCuredDate();
        oldChronic = record.getIsChronic();
        newDiagnosisDate = oldDiagnosisDate;
        newCuredDate = oldCuredDate;
        newChronic = oldChronic;
    }

    /**
     * Make the action change the illness record's diagnosis date to the one given.
     *
     * @param newDiagnosisDate The new diagnosis date.
     */
    public void changeDiagnosisDate(LocalDate newDiagnosisDate) {
        this.newDiagnosisDate = newDiagnosisDate;
    }

    /**
     * Make the action change the illness record's cured date to the one given.
     *
     * @param newCuredDate The new diagnosis date.
     */
    public void changeCuredDate(LocalDate newCuredDate) {
        this.newCuredDate = newCuredDate;
    }

    /**
     * Make the action change the illness record's chronic status to the status given.
     *
     * @param newChronic The new chronic status.
     */
    public void changeChronicStatus(boolean newChronic) {
        this.newChronic = newChronic;
    }

    /**
     * Apply all changes to the illness record.
     *
     * @throws IllegalStateException If no changes were made.
     */
    @Override
    protected void execute() {
        if (Objects.equals(newDiagnosisDate, oldDiagnosisDate) &&
                Objects.equals(newCuredDate, oldCuredDate) &&
                Objects.equals(newChronic, oldChronic)) {
            throw new IllegalStateException("No changes were made to the IllnessRecord.");
        }
        super.execute();
        if (!Objects.equals(newDiagnosisDate, oldDiagnosisDate)) {
            record.setDiagnosisDate(newDiagnosisDate);
        }

        if (!Objects.equals(newCuredDate, oldCuredDate)) {
            record.setCuredDate(newCuredDate);
        }

        if (!Objects.equals(newChronic, oldChronic)) {
            record.setChronic(newChronic);
        }
        manager.applyChangesTo(record.getClient());
    }

    @Override
    protected void unExecute() {
        super.unExecute();
        if (!Objects.equals(newDiagnosisDate, oldDiagnosisDate)) {
            record.setDiagnosisDate(oldDiagnosisDate);
        }

        if (!Objects.equals(newCuredDate, oldCuredDate)) {
            record.setCuredDate(oldCuredDate);
        }

        if (!Objects.equals(newChronic, oldChronic)) {
            record.setChronic(oldChronic);
        }
        manager.applyChangesTo(record.getClient());
    }

    @Override
    public String getExecuteText() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Changed illness record for '%s':", record.getIllnessName()));

        if (!Objects.equals(newDiagnosisDate, oldDiagnosisDate)) {
            builder.append(String.format("%nDiagnosisDate date changed from %s to %s",
                    oldDiagnosisDate, newDiagnosisDate));
        }
        if (!Objects.equals(newCuredDate, oldCuredDate)) {
            builder.append(String.format("%nCuredDate date changed from %s to %s",
                    oldDiagnosisDate, newDiagnosisDate));
        }
        if (!Objects.equals(newChronic, oldChronic)) {
            builder.append(String.format("%nChronic status changed from %s to %s", oldChronic, newChronic));
        }

        return builder.toString();
    }

    @Override
    public String getUnexecuteText() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Reversed these changes to illness record for '%s':", record.getIllnessName()));

        if (!Objects.equals(newDiagnosisDate, oldDiagnosisDate)) {
            builder.append(
                    String.format("%nDiagnosisDate date changed from %s to %s", oldDiagnosisDate, newDiagnosisDate));
        }
        if (!Objects.equals(newCuredDate, oldCuredDate)) {
            builder.append(String.format("%nCuredDate date changed from %s to %s", oldDiagnosisDate, newDiagnosisDate));
        }
        if (!Objects.equals(newChronic, oldChronic)) {
            builder.append(String.format("%nChronic status changed from %s to %s", oldChronic, newChronic));
        }

        return builder.toString();
    }
}
