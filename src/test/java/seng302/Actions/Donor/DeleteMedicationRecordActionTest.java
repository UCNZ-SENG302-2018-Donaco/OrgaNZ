package seng302.Actions.Donor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import seng302.Actions.Action;
import seng302.Donor;
import seng302.MedicationRecord;

import org.junit.Before;
import org.junit.Test;

public class DeleteMedicationRecordActionTest {

    private Donor testDonor;
    private MedicationRecord recordToDelete;

    @Before
    public void resetDonor() {
        testDonor = new Donor();
        recordToDelete = new MedicationRecord(
                "Med C",
                LocalDate.of(2000, 1, 1),
                LocalDate.of(2010, 5, 6));
        testDonor.addMedicationRecord(recordToDelete);
    }

    @Test
    public void executeTest() {
        assertTrue(testDonor.getPastMedications().contains(recordToDelete));

        Action action = new DeleteMedicationRecordAction(testDonor, recordToDelete);
        action.execute();
        assertFalse(testDonor.getPastMedications().contains(recordToDelete));
    }

    @Test
    public void unExecuteTest() {
        assertTrue(testDonor.getPastMedications().contains(recordToDelete));

        Action action = new DeleteMedicationRecordAction(testDonor, recordToDelete);
        action.execute();
        assertFalse(testDonor.getPastMedications().contains(recordToDelete));

        action.unExecute();
        assertTrue(testDonor.getPastMedications().contains(recordToDelete));
    }
}
