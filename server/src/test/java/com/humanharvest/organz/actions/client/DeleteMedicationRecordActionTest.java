package com.humanharvest.organz.actions.client;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.client.medication.DeleteMedicationRecordAction;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.ClientManagerMemory;

import org.junit.Before;
import org.junit.Test;

public class DeleteMedicationRecordActionTest extends BaseTest {

    private Client baseClient;
    private MedicationRecord record;
    private ActionInvoker invoker;
    private ClientManager manager;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        manager = new ClientManagerMemory();
        baseClient = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        record = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), null);
        record.setId(1);
        baseClient.addMedicationRecord(record);
    }

    @Test
    public void DeleteSingleMedicationCurrentTest() {
        DeleteMedicationRecordAction action = new DeleteMedicationRecordAction(baseClient, record, manager);

        assertEquals(1, baseClient.getCurrentMedications().size());

        invoker.execute(action);

        assertEquals(0, baseClient.getCurrentMedications().size());
    }

    @Test
    public void DeleteSingleMedicationPastTest() {
        MedicationRecord newRecord = new MedicationRecord("Generic Name", LocalDate.of(2018, 4, 9), LocalDate.of(2018,
                4, 10));
        newRecord.setId(2);

        baseClient.addMedicationRecord(newRecord);

        DeleteMedicationRecordAction action = new DeleteMedicationRecordAction(baseClient, newRecord, manager);

        assertEquals(1, baseClient.getPastMedications().size());

        invoker.execute(action);

        assertEquals(0, baseClient.getPastMedications().size());
    }

    @Test
    public void DeleteSingleMedicationCurrentUndoTest() {
        DeleteMedicationRecordAction action = new DeleteMedicationRecordAction(baseClient, record, manager);

        invoker.execute(action);
        invoker.undo();

        assertEquals(1, baseClient.getCurrentMedications().size());
    }

    @Test
    public void DeleteSingleMedicationCurrentUndoRedoTest() {
        DeleteMedicationRecordAction action = new DeleteMedicationRecordAction(baseClient, record, manager);

        invoker.execute(action);
        invoker.undo();
        invoker.redo();

        assertEquals(0, baseClient.getCurrentMedications().size());
    }

}
