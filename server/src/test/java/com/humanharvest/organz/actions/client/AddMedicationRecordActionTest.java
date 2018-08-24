package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.ClientManagerMemory;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class AddMedicationRecordActionTest extends BaseTest {

    private ActionInvoker invoker;
    private ClientManager manager;
    private Client testClient;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        manager = new ClientManagerMemory();
        testClient = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
    }

    @Test
    public void AddSingleMedicationCurrentTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);
        AddMedicationRecordAction action = new AddMedicationRecordAction(testClient, record, manager);

        invoker.execute(action);

        assertEquals(1, testClient.getCurrentMedications().size());
        assertEquals(record, testClient.getCurrentMedications().get(0));

    }

    @Test
    public void AddSingleMedicationPastTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), LocalDate.of(2018,
                4, 10));
        AddMedicationRecordAction action = new AddMedicationRecordAction(testClient, record, manager);

        invoker.execute(action);

        assertEquals(1, testClient.getPastMedications().size());
        assertEquals(record, testClient.getPastMedications().get(0));
    }


    @Test
    public void AddMultipleMedicationTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);

        MedicationRecord record2 = new MedicationRecord("Second Generic Name", LocalDate.of(2018, 4, 8), null);

        MedicationRecord record3 = new MedicationRecord("Third Generic Name", LocalDate.of(2018, 4, 7), null);
        AddMedicationRecordAction action = new AddMedicationRecordAction(testClient, record, manager);
        AddMedicationRecordAction action2 = new AddMedicationRecordAction(testClient, record2, manager);
        AddMedicationRecordAction action3 = new AddMedicationRecordAction(testClient, record3, manager);

        invoker.execute(action);
        invoker.execute(action2);
        invoker.execute(action3);

        assertEquals(3, testClient.getCurrentMedications().size());
        assertEquals(record, testClient.getCurrentMedications().get(0));
    }


    @Test
    public void AddMultipleMedicationUndoOneTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);

        MedicationRecord record2 = new MedicationRecord("Second Generic Name", LocalDate.of(2018, 4, 8), null);

        MedicationRecord record3 = new MedicationRecord("Third Generic Name", LocalDate.of(2018, 4, 7), null);
        AddMedicationRecordAction action = new AddMedicationRecordAction(testClient, record, manager);
        AddMedicationRecordAction action2 = new AddMedicationRecordAction(testClient, record2, manager);
        AddMedicationRecordAction action3 = new AddMedicationRecordAction(testClient, record3, manager);

        invoker.execute(action);
        invoker.execute(action2);
        invoker.execute(action3);

        invoker.undo();

        assertEquals(2, testClient.getCurrentMedications().size());
        assertEquals(record, testClient.getCurrentMedications().get(0));
        assertEquals(record2, testClient.getCurrentMedications().get(1));
    }

    @Test
    public void AddMultipleMedicationUndoThreeRedoOneTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);

        MedicationRecord record2 = new MedicationRecord("Second Generic Name", LocalDate.of(2018, 4, 8), null);

        MedicationRecord record3 = new MedicationRecord("Third Generic Name", LocalDate.of(2018, 4, 7), null);
        AddMedicationRecordAction action = new AddMedicationRecordAction(testClient, record, manager);
        AddMedicationRecordAction action2 = new AddMedicationRecordAction(testClient, record2, manager);
        AddMedicationRecordAction action3 = new AddMedicationRecordAction(testClient, record3, manager);

        invoker.execute(action);
        invoker.execute(action2);
        invoker.execute(action3);

        invoker.undo();
        invoker.undo();
        invoker.undo();
        invoker.redo();

        assertEquals(1, testClient.getCurrentMedications().size());
        assertEquals(record, testClient.getCurrentMedications().get(0));
    }

    @Test
    public void AddMedicationStringTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);
        AddMedicationRecordAction action = new AddMedicationRecordAction(testClient, record, manager);

        String result = invoker.execute(action);

        assertEquals(
                String.format("Added record for medication 'Generic Name' to the history of client %d: First Last.",
                        testClient.getUid()),
                result);
    }

    @Test
    public void AddMedicationUndoStringTest() {
        MedicationRecord record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);
        AddMedicationRecordAction action = new AddMedicationRecordAction(testClient, record, manager);

        invoker.execute(action);

        String result = invoker.undo();

        assertEquals(
                String.format("Reversed the addition of record for medication 'Generic Name' to the history of client "
                        + "%d: First Last.", testClient.getUid()),
                result);
    }
}
