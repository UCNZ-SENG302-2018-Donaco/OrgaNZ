package com.humanharvest.organz.actions.client;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.ClientManagerMemory;
import org.junit.Before;
import org.junit.Test;

public class AddIllnessRecordActionTest extends BaseTest {


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
    public void AddSingleIllnessCurrentTest() {
        IllnessRecord record = new IllnessRecord("Generic Name", LocalDate.of(2018, 4, 9), null, false);
        Action action = new AddIllnessRecordAction(testClient, record, manager);

        invoker.execute(action);

        assertEquals(1, testClient.getCurrentIllnesses().size());
        assertEquals(record, testClient.getCurrentIllnesses().get(0));

    }

    @Test
    public void AddSingleIllnessPastTest() {
        IllnessRecord record = new IllnessRecord("Generic Name", LocalDate.of(2018, 4, 9),
                LocalDate.of(2018, 4, 10), false);
        Action action = new AddIllnessRecordAction(testClient, record, manager);

        invoker.execute(action);

        assertEquals(1, testClient.getPastIllnesses().size());
        assertEquals(record, testClient.getPastIllnesses().get(0));
    }


    @Test
    public void AddMultipleIllnessTest() {
        IllnessRecord record = new IllnessRecord("Generic Name", LocalDate.of(2018, 4, 9), null, false);

        IllnessRecord record2 = new IllnessRecord("Second Generic Name", LocalDate.of(2018, 4, 8), null, false);

        IllnessRecord record3 = new IllnessRecord("Third Generic Name", LocalDate.of(2018, 4, 7), null, false);
        Action action = new AddIllnessRecordAction(testClient, record, manager);
        Action action2 = new AddIllnessRecordAction(testClient, record2, manager);
        Action action3 = new AddIllnessRecordAction(testClient, record3, manager);

        invoker.execute(action);
        invoker.execute(action2);
        invoker.execute(action3);

        assertEquals(3, testClient.getCurrentIllnesses().size());
        assertEquals(record, testClient.getCurrentIllnesses().get(0));
    }


    @Test
    public void AddMultipleIllnessUndoOneTest() {
        IllnessRecord record = new IllnessRecord("Generic Name", LocalDate.of(2018, 4, 9), null, false);

        IllnessRecord record2 = new IllnessRecord("Second Generic Name", LocalDate.of(2018, 4, 8), null, false);

        IllnessRecord record3 = new IllnessRecord("Third Generic Name", LocalDate.of(2018, 4, 7), null, false);
        Action action = new AddIllnessRecordAction(testClient, record, manager);
        Action action2 = new AddIllnessRecordAction(testClient, record2, manager);
        Action action3 = new AddIllnessRecordAction(testClient, record3, manager);

        invoker.execute(action);
        invoker.execute(action2);
        invoker.execute(action3);

        invoker.undo();

        assertEquals(2, testClient.getCurrentIllnesses().size());
        assertEquals(record, testClient.getCurrentIllnesses().get(0));
        assertEquals(record2, testClient.getCurrentIllnesses().get(1));
    }

    @Test
    public void AddMultipleIllnessUndoThreeRedoOneTest() {
        IllnessRecord record = new IllnessRecord("Generic Name", LocalDate.of(2018, 4, 9), null, false);

        IllnessRecord record2 = new IllnessRecord("Second Generic Name", LocalDate.of(2018, 4, 8), null, false);

        IllnessRecord record3 = new IllnessRecord("Third Generic Name", LocalDate.of(2018, 4, 7), null, false);
        Action action = new AddIllnessRecordAction(testClient, record, manager);
        Action action2 = new AddIllnessRecordAction(testClient, record2, manager);
        Action action3 = new AddIllnessRecordAction(testClient, record3, manager);

        invoker.execute(action);
        invoker.execute(action2);
        invoker.execute(action3);

        invoker.undo();
        invoker.undo();
        invoker.undo();
        invoker.redo();

        assertEquals(1, testClient.getCurrentIllnesses().size());
        assertEquals(record, testClient.getCurrentIllnesses().get(0));
    }
}
