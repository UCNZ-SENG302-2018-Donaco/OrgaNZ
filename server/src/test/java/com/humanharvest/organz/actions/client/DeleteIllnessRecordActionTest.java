package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.ClientManagerMemory;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class DeleteIllnessRecordActionTest extends BaseTest {

    private Client baseClient;
    private IllnessRecord record;
    private ActionInvoker invoker;
    private ClientManager manager;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        manager = new ClientManagerMemory();
        baseClient = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        record = new IllnessRecord("Generic Name", LocalDate.of(2018, 4, 9), null, false);
        baseClient.addIllnessRecord(record);
    }

    @Test
    public void DeleteSingleIllnessCurrentTest() {
        DeleteIllnessRecordAction action = new DeleteIllnessRecordAction(baseClient, record, manager);

        assertEquals(1, baseClient.getCurrentIllnesses().size());

        invoker.execute(action);

        assertEquals(0, baseClient.getCurrentIllnesses().size());
    }

    @Test
    public void DeleteSingleIllnessPastTest() {
        IllnessRecord newRecord = new IllnessRecord("Generic Name", LocalDate.of(2018, 4, 9),
                LocalDate.of(2018, 4, 10), false);
        baseClient.addIllnessRecord(newRecord);

        DeleteIllnessRecordAction action = new DeleteIllnessRecordAction(baseClient, newRecord, manager);

        assertEquals(1, baseClient.getPastIllnesses().size());

        invoker.execute(action);

        assertEquals(0, baseClient.getPastIllnesses().size());
    }


    @Test
    public void DeleteSingleIllnessCurrentUndoTest() {
        DeleteIllnessRecordAction action = new DeleteIllnessRecordAction(baseClient, record, manager);

        invoker.execute(action);
        invoker.undo();

        assertEquals(1, baseClient.getCurrentIllnesses().size());
    }


    @Test
    public void DeleteSingleIllnessCurrentUndoRedoTest() {
        DeleteIllnessRecordAction action = new DeleteIllnessRecordAction(baseClient, record, manager);

        invoker.execute(action);
        invoker.undo();
        invoker.redo();

        assertEquals(0, baseClient.getCurrentIllnesses().size());
    }

}
