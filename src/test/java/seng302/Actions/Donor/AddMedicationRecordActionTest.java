package seng302.Actions.Donor;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import seng302.Actions.ActionInvoker;
import seng302.Donor;
import seng302.MedicationRecord;

import org.junit.Before;
import org.junit.Test;

public class AddMedicationRecordActionTest {

    private ActionInvoker invoker;
    private Donor baseDonor;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        baseDonor = new Donor("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
    }

    @Test
    public void AddSingleMedicationCurrentTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);
        AddMedicationRecordAction action = new AddMedicationRecordAction(baseDonor, record);

        invoker.execute(action);

        assertEquals(1, baseDonor.getCurrentMedications().size());
        assertEquals(record, baseDonor.getCurrentMedications().get(0));
    }

    @Test
    public void AddSingleMedicationPastTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), LocalDate.of(2018,
                4, 10));
        AddMedicationRecordAction action = new AddMedicationRecordAction(baseDonor, record);

        invoker.execute(action);

        assertEquals(1, baseDonor.getPastMedications().size());
        assertEquals(record, baseDonor.getPastMedications().get(0));
    }



    @Test
    public void AddMultipleMedicationTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);

        MedicationRecord record2 = new MedicationRecord("Second Generic Name", LocalDate.of(2018, 4, 8), null);

        MedicationRecord record3 = new MedicationRecord("Third Generic Name", LocalDate.of(2018, 4, 7), null);
        AddMedicationRecordAction action = new AddMedicationRecordAction(baseDonor, record);
        AddMedicationRecordAction action2 = new AddMedicationRecordAction(baseDonor, record2);
        AddMedicationRecordAction action3 = new AddMedicationRecordAction(baseDonor, record3);

        invoker.execute(action);
        invoker.execute(action2);
        invoker.execute(action3);

        assertEquals(3, baseDonor.getCurrentMedications().size());
        assertEquals(record, baseDonor.getCurrentMedications().get(0));
    }


    @Test
    public void AddMultipleMedicationUndoOneTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);

        MedicationRecord record2 = new MedicationRecord("Second Generic Name", LocalDate.of(2018, 4, 8), null);

        MedicationRecord record3 = new MedicationRecord("Third Generic Name", LocalDate.of(2018, 4, 7), null);
        AddMedicationRecordAction action = new AddMedicationRecordAction(baseDonor, record);
        AddMedicationRecordAction action2 = new AddMedicationRecordAction(baseDonor, record2);
        AddMedicationRecordAction action3 = new AddMedicationRecordAction(baseDonor, record3);

        invoker.execute(action);
        invoker.execute(action2);
        invoker.execute(action3);

        invoker.undo();

        assertEquals(2, baseDonor.getCurrentMedications().size());
        assertEquals(record, baseDonor.getCurrentMedications().get(0));
        assertEquals(record2, baseDonor.getCurrentMedications().get(1));
    }

    @Test
    public void AddMultipleMedicationUndoThreeRedoOneTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);

        MedicationRecord record2 = new MedicationRecord("Second Generic Name", LocalDate.of(2018, 4, 8), null);

        MedicationRecord record3 = new MedicationRecord("Third Generic Name", LocalDate.of(2018, 4, 7), null);
        AddMedicationRecordAction action = new AddMedicationRecordAction(baseDonor, record);
        AddMedicationRecordAction action2 = new AddMedicationRecordAction(baseDonor, record2);
        AddMedicationRecordAction action3 = new AddMedicationRecordAction(baseDonor, record3);

        invoker.execute(action);
        invoker.execute(action2);
        invoker.execute(action3);

        invoker.undo();
        invoker.undo();
        invoker.undo();
        invoker.redo();

        assertEquals(1, baseDonor.getCurrentMedications().size());
        assertEquals(record, baseDonor.getCurrentMedications().get(0));
    }

    @Test
    public void AddMedicationStringTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);
        AddMedicationRecordAction action = new AddMedicationRecordAction(baseDonor, record);

        String result = invoker.execute(action);

        assertEquals("Added medication Generic Name record for donor First Last", result);
    }

    @Test
    public void AddMedicationUndoStringTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);
        AddMedicationRecordAction action = new AddMedicationRecordAction(baseDonor, record);

        invoker.execute(action);

        String result = invoker.undo();

        assertEquals("Removed medication Generic Name record for donor First Last", result);
    }
}
