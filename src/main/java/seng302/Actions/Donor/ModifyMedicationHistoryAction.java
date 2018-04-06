package seng302.Actions.Donor;

import java.util.ArrayList;
import java.util.List;

import seng302.Actions.Action;
import seng302.Donor;
import seng302.MedicationRecord;

public class ModifyMedicationHistoryAction implements Action {

    private Donor donor;
    private List<MedicationRecord> addToCurrentMedications;
    private List<MedicationRecord> addToPastMedications;
    private List<MedicationRecord> removeFromCurrentMedications;
    private List<MedicationRecord> removeFromPastMedications;

    public ModifyMedicationHistoryAction(Donor donor) {
        this.donor = donor;
        addToCurrentMedications = new ArrayList<>();
        addToPastMedications = new ArrayList<>();
        removeFromCurrentMedications = new ArrayList<>();
        removeFromPastMedications = new ArrayList<>();
    }

    public void addCurrentMedication(MedicationRecord record) {
        addToCurrentMedications.add(record);
    }

    public void addPastMedication(MedicationRecord record) {
        addToPastMedications.add(record);
    }

    public void removeCurrentMedication(MedicationRecord record) {
        removeFromCurrentMedications.add(record);
    }

    public void removePastMedication(MedicationRecord record) {
        removeFromPastMedications.add(record);
    }

    @Override
    public void execute() {
        for (MedicationRecord record : removeFromCurrentMedications) {
            donor.getCurrentMedications().remove(record);
        }
        for (MedicationRecord record : removeFromPastMedications) {
            donor.getPastMedications().remove(record);
        }
        for (MedicationRecord record : addToCurrentMedications) {
            donor.getCurrentMedications().add(record);
        }
        for (MedicationRecord record : addToPastMedications) {
            donor.getPastMedications().add(record);
        }
    }

    @Override
    public void unExecute() {
        for (MedicationRecord record : addToCurrentMedications) {
            donor.getCurrentMedications().remove(record);
        }
        for (MedicationRecord record : addToPastMedications) {
            donor.getPastMedications().remove(record);
        }
        for (MedicationRecord record : removeFromCurrentMedications) {
            donor.getCurrentMedications().add(record);
        }
        for (MedicationRecord record : removeFromPastMedications) {
            donor.getPastMedications().add(record);
        }
    }
}
