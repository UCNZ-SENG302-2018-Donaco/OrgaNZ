package seng302.Actions.Donor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import seng302.Actions.Action;
import seng302.Donor;
import seng302.MedicationRecord;

import org.junit.Before;
import org.junit.Test;

public class AddMedicationRecordActionTest {

    private Donor testDonor;

    @Before
    public void resetDonor() {
        testDonor = new Donor();
    }

    @Test
    public void executeTest() {
        MedicationRecord newRecord = new MedicationRecord(
                "Med A",
                LocalDate.of(1990, 1, 1),
                null);

        Action action = new AddMedicationRecordAction(testDonor, newRecord);
        action.execute();
        assertTrue(testDonor.getCurrentMedications().contains(newRecord));
    }

    @Test
    public void unExecuteTest() {
        MedicationRecord newRecord = new MedicationRecord(
                "Med B",
                LocalDate.of(1995, 2, 2),
                null);

        Action action = new AddMedicationRecordAction(testDonor, newRecord);
        action.execute();
        assertTrue(testDonor.getCurrentMedications().contains(newRecord));

        action.unExecute();
        assertFalse(testDonor.getCurrentMedications().contains(newRecord));
    }
}
