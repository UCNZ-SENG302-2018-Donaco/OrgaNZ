package seng302.Actions.Client;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import seng302.Actions.ActionInvoker;
import seng302.Client;
import seng302.MedicationRecord;
import seng302.State.ClientManager;
import seng302.State.ClientManagerMemory;

import org.junit.Before;
import org.junit.Test;

public class ModifyMedicationRecordActionTest {

    private ActionInvoker invoker;
    private ClientManager manager;
    private Client baseClient;
    private MedicationRecord record;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        manager = new ClientManagerMemory();
        baseClient = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);
        baseClient.addMedicationRecord(record);
    }

    @Test
    public void ModifySingleMedicationCurrentTest() {
        ModifyMedicationRecordAction action = new ModifyMedicationRecordAction(record, manager);

        LocalDate newDate = LocalDate.of(2018, 4, 11);

        action.changeStarted(newDate);

        invoker.execute(action);

        assertEquals(newDate, baseClient.getCurrentMedications().get(0).getStarted());
    }


    @Test
    public void ModifySingleMedicationCurrentToPastTest() {
        ModifyMedicationRecordAction action = new ModifyMedicationRecordAction(record, manager);

        LocalDate newDate = LocalDate.of(2018, 4, 11);

        action.changeStopped(newDate);

        invoker.execute(action);

        assertEquals(1, baseClient.getPastMedications().size());
        assertEquals(newDate, baseClient.getPastMedications().get(0).getStopped());
    }


    @Test
    public void ModifySingleMedicationCurrentUndoTest() {
        ModifyMedicationRecordAction action = new ModifyMedicationRecordAction(record, manager);

        LocalDate newDate = LocalDate.of(2018, 4, 11);

        action.changeStarted(newDate);

        invoker.execute(action);
        invoker.undo();

        assertEquals(LocalDate.of(2018, 4, 9), baseClient.getCurrentMedications().get(0).getStarted());
    }


    @Test
    public void ModifySingleMedicationCurrentUndoRedoTest() {
        ModifyMedicationRecordAction action = new ModifyMedicationRecordAction(record, manager);

        LocalDate newDate = LocalDate.of(2018, 4, 11);

        action.changeStarted(newDate);

        invoker.execute(action);
        invoker.undo();
        invoker.redo();

        assertEquals(newDate, baseClient.getCurrentMedications().get(0).getStarted());
    }


}
