package seng302.Actions.Client;

import java.time.LocalDate;
import java.util.Objects;

import seng302.Actions.Action;
import seng302.IllnessRecord;

public class ModifyIllnessRecordAction extends Action {

    private IllnessRecord record;
    private LocalDate oldDiagnosisDate;
    private LocalDate oldCuredDate;
    private LocalDate newDiagnosisDate;
    private LocalDate newCuredDate;
    private Boolean oldChronic;
    private Boolean newChronic;

    public ModifyIllnessRecordAction(IllnessRecord record) {
        this.record = record;
        oldDiagnosisDate = record.getDiagnosisDate();
        oldCuredDate = record.getCuredDate();
        oldChronic = record.isChronic();
        newDiagnosisDate = oldDiagnosisDate;
        newCuredDate = oldCuredDate;
        newChronic = oldChronic;
    }

    public void changeDiagnosisDate(LocalDate newDiagnosisDate) {
        this.newDiagnosisDate = newDiagnosisDate;
    }

    public void changeCuredDate(LocalDate newCuredDate) {
        this.newCuredDate = newCuredDate;
    }

    public void changeChronicStatus(Boolean newChronic) {
        this.newChronic = newChronic;
    }

    @Override
    public void execute() {
        if (Objects.equals(newDiagnosisDate, oldDiagnosisDate) &&
                Objects.equals(newCuredDate, oldCuredDate) &&
                Objects.equals(newChronic, oldChronic)) {
            throw new IllegalStateException("No changes were made to the MedicationRecord.");
        }
        if (!Objects.equals(newDiagnosisDate, oldDiagnosisDate)) {
            record.setDiagnosisDate(newDiagnosisDate);
        }

        if (!Objects.equals(newCuredDate, oldCuredDate)) {
            record.setCuredDate(newCuredDate);
        }

        if (!Objects.equals(newChronic, oldChronic)) {
            record.setChronic(newChronic);
        }
    }

    @Override
    public void unExecute() {
        if (!Objects.equals(newDiagnosisDate, oldDiagnosisDate)) {
            record.setDiagnosisDate(oldDiagnosisDate);
        }

        if (!Objects.equals(newCuredDate, oldCuredDate)) {
            record.setCuredDate(oldCuredDate);
        }

        if (!Objects.equals(newChronic, oldChronic)) {
            record.setChronic(oldChronic);
        }
    }

    @Override
    public String getExecuteText() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Changed illness record for '%s':", record.getIllnessName()));

        if (!Objects.equals(newDiagnosisDate, oldDiagnosisDate)) {
            builder.append(String.format("\nDiagnosisDate date changed from %s to %s", oldDiagnosisDate, newDiagnosisDate));
        }
        if (!Objects.equals(newCuredDate, oldCuredDate)) {
            builder.append(String.format("\nCuredDate date changed from %s to %s", oldDiagnosisDate, newDiagnosisDate));
        }
        if (!Objects.equals(newChronic, oldChronic)) {
            builder.append(String.format("\nChronic status changed from %s to %s", oldChronic, newChronic));
        }

        return builder.toString();
    }

    @Override
    public String getUnexecuteText() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Reversed these changes to illness record for '%s':", record.getIllnessName()));

        if (!Objects.equals(newDiagnosisDate, oldDiagnosisDate)) {
            builder.append(String.format("\nDiagnosisDate date changed from %s to %s", oldDiagnosisDate, newDiagnosisDate));
        }
        if (!Objects.equals(newCuredDate, oldCuredDate)) {
            builder.append(String.format("\nCuredDate date changed from %s to %s", oldDiagnosisDate, newDiagnosisDate));
        }
        if (!Objects.equals(newChronic, oldChronic)) {
            builder.append(String.format("\nChronic status changed from %s to %s", oldChronic, newChronic));
        }

        return builder.toString();
    }
}
