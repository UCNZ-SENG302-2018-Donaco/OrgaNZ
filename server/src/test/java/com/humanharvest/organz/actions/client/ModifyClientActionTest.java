package com.humanharvest.organz.actions.client;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.ClientManagerMemory;
import com.humanharvest.organz.utilities.enums.Gender;

import org.junit.Before;
import org.junit.Test;

public class ModifyClientActionTest extends BaseTest {

    private ClientManager manager;
    private ActionInvoker invoker;
    private Client baseClient;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        manager = new ClientManagerMemory();
        baseClient = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        manager.addClient(baseClient);
    }

    @Test
    public void CheckClientModifyNameTest() throws Exception {
        ModifyClientAction action = new ModifyClientAction(baseClient, manager);
        action.addChange("firstName", baseClient.getFirstName(), "New");
        invoker.execute(action);
        assertEquals("New", manager.getClients().get(0).getFirstName());
    }

    @Test
    public void CheckClientModifyUndoNameTest() throws Exception {
        ModifyClientAction action = new ModifyClientAction(baseClient, manager);
        action.addChange("firstName", baseClient.getFirstName(), "New");
        invoker.execute(action);
        invoker.undo();
        assertEquals("First", manager.getClients().get(0).getFirstName());
    }

    @Test
    public void CheckClientMultipleUpdateValuesTest() throws Exception {
        ModifyClientAction action = new ModifyClientAction(baseClient, manager);
        action.addChange("firstName", baseClient.getFirstName(), "New");
        action.addChange("gender", baseClient.getGender(), Gender.FEMALE);
        invoker.execute(action);
        assertEquals("New", manager.getClients().get(0).getFirstName());
        assertEquals(Gender.FEMALE, manager.getClients().get(0).getGender());
    }

    @Test
    public void CheckClientMultipleUpdateValuesUndoTest() throws Exception {
        ModifyClientAction action = new ModifyClientAction(baseClient, manager);
        action.addChange("firstName", baseClient.getFirstName(), "New");
        action.addChange("gender", baseClient.getGender(), Gender.FEMALE);
        invoker.execute(action);
        invoker.undo();
        assertEquals("First", manager.getClients().get(0).getFirstName());
        assertEquals(Gender.UNSPECIFIED, manager.getClients().get(0).getGender());
    }

    @Test(expected = NoSuchFieldException.class)
    public void InvalidSetterFieldTest() throws Exception {
        ModifyClientAction action = new ModifyClientAction(baseClient, manager);
        action.addChange("notAField", "Old", "New");
        invoker.execute(action);
    }

    @Test(expected = NoSuchFieldException.class)
    public void InvalidSetterAttributeTest() throws Exception {
        ModifyClientAction action = new ModifyClientAction(baseClient, manager);
        action.addChange("firstName", 1, "New");
        invoker.execute(action);
    }
}
