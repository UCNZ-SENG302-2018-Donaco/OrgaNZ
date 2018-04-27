package seng302.Actions.Donor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;

import seng302.Actions.Person.ModifyMedicationRecordAction;
import seng302.MedicationRecord;

import org.junit.Before;
import org.junit.Test;

public class ModifyMedicationRecordActionTest {

    private MedicationRecord testPastRecord;
    private MedicationRecord testCurrentRecord;

    @Before
    public void setupRecords() {
        testPastRecord = new MedicationRecord(
                "Med B",
                LocalDate.of(2010, 6, 1),
                LocalDate.of(2012, 5, 7));
        testCurrentRecord = new MedicationRecord(
                "Med C",
                LocalDate.of(2014, 3, 4),
                null);
    }

    @Test(expected = IllegalStateException.class)
    public void makeNoChangesTest() {
        ModifyMedicationRecordAction action = new ModifyMedicationRecordAction(testPastRecord);
        action.execute();
    }

    @Test
    public void changePastRecordToCurrentTest() {
        ModifyMedicationRecordAction action = new ModifyMedicationRecordAction(testPastRecord);
        action.changeStopped(null);
        action.execute();
        assertNull(testPastRecord.getStopped());
    }

    @Test
    public void changeCurrentRecordToPastTest() {
        ModifyMedicationRecordAction action = new ModifyMedicationRecordAction(testCurrentRecord);
        LocalDate newStopped = LocalDate.of(2015, 7, 7);
        action.changeStopped(newStopped);
        action.execute();
        assertEquals(newStopped, testCurrentRecord.getStopped());
    }

    @Test
    public void changeStartedAndStoppedThenUndoTest() {
        LocalDate newStarted = LocalDate.of(2001, 4, 5);
        LocalDate newStopped = LocalDate.of(2015, 7, 7);

        LocalDate origStarted = testPastRecord.getStarted();
        LocalDate origStopped = testPastRecord.getStopped();

        ModifyMedicationRecordAction action = new ModifyMedicationRecordAction(testPastRecord);
        action.changeStarted(newStarted);
        action.changeStopped(newStopped);

        action.execute();
        assertEquals(newStarted, testPastRecord.getStarted());
        assertEquals(newStopped, testPastRecord.getStopped());

        action.unExecute();
        assertEquals(origStarted, testPastRecord.getStarted());
        assertEquals(origStopped, testPastRecord.getStopped());
    }
}
