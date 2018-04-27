package seng302.Actions.Client;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import seng302.Actions.Action;
import seng302.Actions.Client.AddMedicationRecordAction;
import seng302.Client;
import seng302.Actions.ActionInvoker;
import seng302.MedicationRecord;

import org.junit.Before;
import org.junit.Test;

public class AddMedicationRecordActionTest {

    private ActionInvoker invoker;
    private Client baseClient;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        baseClient = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
    }

    @Test
    public void AddSingleMedicationCurrentTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);
        AddMedicationRecordAction action = new AddMedicationRecordAction(baseClient, record);

        invoker.execute(action);

        assertEquals(1, baseClient.getCurrentMedications().size());
        assertEquals(record, baseClient.getCurrentMedications().get(0));
    }

    @Test
    public void AddSingleMedicationPastTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), LocalDate.of(2018,
                4, 10));
        AddMedicationRecordAction action = new AddMedicationRecordAction(baseClient, record);

        invoker.execute(action);

        assertEquals(1, baseClient.getPastMedications().size());
        assertEquals(record, baseClient.getPastMedications().get(0));
    }



    @Test
    public void AddMultipleMedicationTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);

        MedicationRecord record2 = new MedicationRecord("Second Generic Name", LocalDate.of(2018, 4, 8), null);

        MedicationRecord record3 = new MedicationRecord("Third Generic Name", LocalDate.of(2018, 4, 7), null);
        AddMedicationRecordAction action = new AddMedicationRecordAction(baseClient, record);
        AddMedicationRecordAction action2 = new AddMedicationRecordAction(baseClient, record2);
        AddMedicationRecordAction action3 = new AddMedicationRecordAction(baseClient, record3);

        invoker.execute(action);
        invoker.execute(action2);
        invoker.execute(action3);

        assertEquals(3, baseClient.getCurrentMedications().size());
        assertEquals(record, baseClient.getCurrentMedications().get(0));
    }


    @Test
    public void AddMultipleMedicationUndoOneTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);

        MedicationRecord record2 = new MedicationRecord("Second Generic Name", LocalDate.of(2018, 4, 8), null);

        MedicationRecord record3 = new MedicationRecord("Third Generic Name", LocalDate.of(2018, 4, 7), null);
        AddMedicationRecordAction action = new AddMedicationRecordAction(baseClient, record);
        AddMedicationRecordAction action2 = new AddMedicationRecordAction(baseClient, record2);
        AddMedicationRecordAction action3 = new AddMedicationRecordAction(baseClient, record3);

        invoker.execute(action);
        invoker.execute(action2);
        invoker.execute(action3);

        invoker.undo();

        assertEquals(2, baseClient.getCurrentMedications().size());
        assertEquals(record, baseClient.getCurrentMedications().get(0));
        assertEquals(record2, baseClient.getCurrentMedications().get(1));
    }

    @Test
    public void AddMultipleMedicationUndoThreeRedoOneTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);

        MedicationRecord record2 = new MedicationRecord("Second Generic Name", LocalDate.of(2018, 4, 8), null);

        MedicationRecord record3 = new MedicationRecord("Third Generic Name", LocalDate.of(2018, 4, 7), null);
        AddMedicationRecordAction action = new AddMedicationRecordAction(baseClient, record);
        AddMedicationRecordAction action2 = new AddMedicationRecordAction(baseClient, record2);
        AddMedicationRecordAction action3 = new AddMedicationRecordAction(baseClient, record3);

        invoker.execute(action);
        invoker.execute(action2);
        invoker.execute(action3);

        invoker.undo();
        invoker.undo();
        invoker.undo();
        invoker.redo();

        assertEquals(1, baseClient.getCurrentMedications().size());
        assertEquals(record, baseClient.getCurrentMedications().get(0));
    }

    @Test
    public void AddMedicationStringTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);
        AddMedicationRecordAction action = new AddMedicationRecordAction(baseClient, record);

        String result = invoker.execute(action);

        assertEquals(
                String.format("Added record for medication 'Generic Name' to the history of client %d: First Last.",
                        baseClient.getUid()),
                result);
    }

    @Test
    public void AddMedicationUndoStringTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);
        AddMedicationRecordAction action = new AddMedicationRecordAction(baseClient, record);

        invoker.execute(action);

        String result = invoker.undo();

        assertEquals(
                String.format("Reversed the addition of record for medication 'Generic Name' to the history of client "
                                + "%d: First Last.", baseClient.getUid()),
                result);
    }
}
