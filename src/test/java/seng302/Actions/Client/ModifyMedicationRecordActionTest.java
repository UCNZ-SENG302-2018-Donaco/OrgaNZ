package seng302.Actions.Client;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import seng302.Actions.ActionInvoker;
import seng302.Donor;
import seng302.MedicationRecord;

import org.junit.Before;
import org.junit.Test;

public class ModifyMedicationRecordActionTest {

    private ActionInvoker invoker;
    private Donor baseDonor;
    private MedicationRecord record;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        baseDonor = new Donor("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);
        baseDonor.addMedicationRecord(record);
    }

    @Test
    public void ModifySingleMedicationCurrentTest() {
        ModifyMedicationRecordAction action = new ModifyMedicationRecordAction(record);

        LocalDate newDate = LocalDate.of(2018, 4, 11);

        action.changeStarted(newDate);

        invoker.execute(action);

        assertEquals(newDate, baseDonor.getCurrentMedications().get(0).getStarted());
    }


    @Test
    public void ModifySingleMedicationCurrentToPastTest() {
        ModifyMedicationRecordAction action = new ModifyMedicationRecordAction(record);

        LocalDate newDate = LocalDate.of(2018, 4, 11);

        action.changeStopped(newDate);

        invoker.execute(action);

        assertEquals(1, baseDonor.getPastMedications().size());
        assertEquals(newDate, baseDonor.getPastMedications().get(0).getStopped());
    }


    @Test
    public void ModifySingleMedicationCurrentUndoTest() {
        ModifyMedicationRecordAction action = new ModifyMedicationRecordAction(record);

        LocalDate newDate = LocalDate.of(2018, 4, 11);

        action.changeStarted(newDate);

        invoker.execute(action);
        invoker.undo();

        assertEquals(LocalDate.of(2018, 4, 9), baseDonor.getCurrentMedications().get(0).getStarted());
    }


    @Test
    public void ModifySingleMedicationCurrentUndoRedoTest() {
        ModifyMedicationRecordAction action = new ModifyMedicationRecordAction(record);

        LocalDate newDate = LocalDate.of(2018, 4, 11);

        action.changeStarted(newDate);

        invoker.execute(action);
        invoker.undo();
        invoker.redo();

        assertEquals(newDate, baseDonor.getCurrentMedications().get(0).getStarted());
    }


}
