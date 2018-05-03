package seng302.Actions.Client;

import static org.junit.Assert.*;

import java.time.LocalDate;

import seng302.Actions.ActionInvoker;
import seng302.Client;
import seng302.IllnessRecord;

import org.junit.Before;
import org.junit.Test;

public class ModifyIllnessRecordActionTest {

    private ActionInvoker invoker;
    private Client baseClient;
    private IllnessRecord record;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        baseClient = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        record = new IllnessRecord("Generic Name", LocalDate.of(2018, 4, 9), null, false);
        baseClient.addIllnessRecord(record);
    }

    @Test
    public void ModifySingleIllnessCurrentTest() {
        ModifyIllnessRecordAction action = new ModifyIllnessRecordAction(record);

        LocalDate newDate = LocalDate.of(2018, 4, 11);

        action.changeDiagnosisDate(newDate);
        action.changeChronicStatus(true);

        invoker.execute(action);

        assertEquals(newDate, baseClient.getCurrentIllnesses().get(0).getDiagnosisDate());
        assertTrue(baseClient.getCurrentIllnesses().get(0).isChronic());
    }


    @Test
    public void ModifySingleIllnessCurrentToPastTest() {
        ModifyIllnessRecordAction action = new ModifyIllnessRecordAction(record);

        LocalDate newDate = LocalDate.of(2018, 4, 11);

        action.changeCuredDate(newDate);

        invoker.execute(action);

        assertEquals(1, baseClient.getPastIllnesses().size());
        assertEquals(newDate, baseClient.getPastIllnesses().get(0).getCuredDate());
    }


    @Test
    public void ModifySingleIllnessCurrentUndoTest() {
        ModifyIllnessRecordAction action = new ModifyIllnessRecordAction(record);

        LocalDate newDate = LocalDate.of(2018, 4, 11);

        action.changeDiagnosisDate(newDate);
        action.changeChronicStatus(true);

        invoker.execute(action);
        invoker.undo();

        assertEquals(LocalDate.of(2018, 4, 9), baseClient.getCurrentIllnesses().get(0).getDiagnosisDate());
        assertFalse(baseClient.getCurrentIllnesses().get(0).isChronic());
    }


    @Test
    public void ModifySingleIllnessCurrentUndoRedoTest() {
        ModifyIllnessRecordAction action = new ModifyIllnessRecordAction(record);

        LocalDate newDate = LocalDate.of(2018, 4, 11);

        action.changeDiagnosisDate(newDate);
        action.changeChronicStatus(true);

        invoker.execute(action);
        invoker.undo();
        invoker.redo();

        assertEquals(newDate, baseClient.getCurrentIllnesses().get(0).getDiagnosisDate());
        assertTrue(baseClient.getCurrentIllnesses().get(0).isChronic());
    }
}
