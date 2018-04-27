package seng302.Actions.Client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import seng302.Client;
import seng302.MedicationRecord;

import org.junit.Before;
import org.junit.Test;

public class DeleteMedicationRecordActionTest {

    private Client testClient;
    private MedicationRecord recordToDelete;

    @Before
    public void resetClient() {
        testClient = new Client(1);
        recordToDelete = new MedicationRecord(
                "Med C",
                LocalDate.of(2000, 1, 1),
                LocalDate.of(2010, 5, 6));
        testClient.addMedicationRecord(recordToDelete);
    }

    @Test
    public void executeTest() {
        assertTrue(testClient.getPastMedications().contains(recordToDelete));

        DeleteMedicationRecordAction action = new DeleteMedicationRecordAction(testClient, recordToDelete);
        action.execute();
        assertFalse(testClient.getPastMedications().contains(recordToDelete));
    }

    @Test
    public void unExecuteTest() {
        assertTrue(testClient.getPastMedications().contains(recordToDelete));

        DeleteMedicationRecordAction action = new DeleteMedicationRecordAction(testClient, recordToDelete);
        action.execute();
        assertFalse(testClient.getPastMedications().contains(recordToDelete));

        action.unExecute();
        assertTrue(testClient.getPastMedications().contains(recordToDelete));
    }
}
