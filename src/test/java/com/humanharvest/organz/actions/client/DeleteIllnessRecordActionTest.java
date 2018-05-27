package com.humanharvest.organz.actions.client;

import static org.junit.Assert.*;

import java.time.LocalDate;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;

import org.junit.Before;
import org.junit.Test;

public class DeleteIllnessRecordActionTest extends BaseTest {

    private Client baseClient;
    private IllnessRecord record;
    private ActionInvoker invoker;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        baseClient = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        record = new IllnessRecord("Generic Name", LocalDate.of(2018, 4, 9), null, false);
        baseClient.addIllnessRecord(record);
    }

    @Test
    public void DeleteSingleIllnessCurrentTest() {
        DeleteIllnessRecordAction action = new DeleteIllnessRecordAction(baseClient, record);

        assertEquals(1, baseClient.getCurrentIllnesses().size());

        invoker.execute(action);

        assertEquals(0, baseClient.getCurrentIllnesses().size());
    }

    @Test
    public void DeleteSingleIllnessPastTest() {
        IllnessRecord newRecord = new IllnessRecord("Generic Name", LocalDate.of(2018, 4, 9),
                LocalDate.of(2018, 4, 10), false);
        baseClient.addIllnessRecord(newRecord);

        DeleteIllnessRecordAction action = new DeleteIllnessRecordAction(baseClient, newRecord);

        assertEquals(1, baseClient.getPastIllnesses().size());

        invoker.execute(action);

        assertEquals(0, baseClient.getPastIllnesses().size());
    }


    @Test
    public void DeleteSingleIllnessCurrentUndoTest() {
        DeleteIllnessRecordAction action = new DeleteIllnessRecordAction(baseClient, record);

        invoker.execute(action);
        invoker.undo();

        assertEquals(1, baseClient.getCurrentIllnesses().size());
    }


    @Test
    public void DeleteSingleIllnessCurrentUndoRedoTest() {
        DeleteIllnessRecordAction action = new DeleteIllnessRecordAction(baseClient, record);

        invoker.execute(action);
        invoker.undo();
        invoker.redo();

        assertEquals(0, baseClient.getCurrentIllnesses().size());
    }

}
