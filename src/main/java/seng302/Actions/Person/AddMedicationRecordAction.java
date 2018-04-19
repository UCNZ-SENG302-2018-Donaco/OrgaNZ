package seng302.Actions.Person;

import seng302.Actions.Action;
import seng302.Person;
import seng302.MedicationRecord;

public class AddMedicationRecordAction implements Action {

    private Person person;
    private MedicationRecord record;

    public AddMedicationRecordAction(Person person, MedicationRecord record) {
        this.person = person;
        this.record = record;
    }

    @Override
    public void execute() {
        person.addMedicationRecord(record);
    }

    @Override
    public void unExecute() {
        person.deleteMedicationRecord(record);
    }
}
