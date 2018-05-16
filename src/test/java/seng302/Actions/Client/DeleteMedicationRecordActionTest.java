package seng302.Actions.Client;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import seng302.Actions.ActionInvoker;
import seng302.Client;
import seng302.HistoryManager;
import seng302.MedicationRecord;

import org.junit.Before;
import org.junit.Test;

public class DeleteMedicationRecordActionTest {

    private Client baseClient;
    private MedicationRecord record;
    private ActionInvoker invoker;

    @Before
    public void init() {
        HistoryManager.createTestManager();

        invoker = new ActionInvoker();
        baseClient = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);
        baseClient.addMedicationRecord(record);
    }

    @Test
    public void DeleteSingleMedicationCurrentTest() {
        DeleteMedicationRecordAction action = new DeleteMedicationRecordAction(baseClient, record);

        assertEquals(1, baseClient.getCurrentMedications().size());

        invoker.execute(action);

        assertEquals(0, baseClient.getCurrentMedications().size());
    }

    @Test
    public void DeleteSingleMedicationPastTest() {
        MedicationRecord newRecord = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), LocalDate.of(2018,
                4, 10));
        baseClient.addMedicationRecord(newRecord);

        DeleteMedicationRecordAction action = new DeleteMedicationRecordAction(baseClient, newRecord);

        assertEquals(1, baseClient.getPastMedications().size());

        invoker.execute(action);

        assertEquals(0, baseClient.getPastMedications().size());
    }


    @Test
    public void DeleteSingleMedicationCurrentUndoTest() {
        DeleteMedicationRecordAction action = new DeleteMedicationRecordAction(baseClient, record);

        invoker.execute(action);
        invoker.undo();

        assertEquals(1, baseClient.getCurrentMedications().size());
    }


    @Test
    public void DeleteSingleMedicationCurrentUndoRedoTest() {
        DeleteMedicationRecordAction action = new DeleteMedicationRecordAction(baseClient, record);

        invoker.execute(action);
        invoker.undo();
        invoker.redo();

        assertEquals(0, baseClient.getCurrentMedications().size());
    }

}
