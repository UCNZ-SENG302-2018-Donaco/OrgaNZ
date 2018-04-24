package seng302.Actions.Client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import seng302.Actions.Action;
import seng302.Actions.Client.AddMedicationRecordAction;
import seng302.Client;
import seng302.MedicationRecord;

import org.junit.Before;
import org.junit.Test;

public class AddMedicationRecordActionTest {

    private Client testClient;

    @Before
    public void resetClient() {
        testClient = new Client();
    }

    @Test
    public void executeTest() {
        MedicationRecord newRecord = new MedicationRecord(
                "Med A",
                LocalDate.of(1990, 1, 1),
                null);

        Action action = new AddMedicationRecordAction(testClient, newRecord);
        action.execute();
        assertTrue(testClient.getCurrentMedications().contains(newRecord));
    }

    @Test
    public void unExecuteTest() {
        MedicationRecord newRecord = new MedicationRecord(
                "Med B",
                LocalDate.of(1995, 2, 2),
                null);

        Action action = new AddMedicationRecordAction(testClient, newRecord);
        action.execute();
        assertTrue(testClient.getCurrentMedications().contains(newRecord));

        action.unExecute();
        assertFalse(testClient.getCurrentMedications().contains(newRecord));
    }
}
