package seng302.Actions.Client;


import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import seng302.Actions.ActionInvoker;
import seng302.Client;
import seng302.State.ClientManager;
import seng302.Utilities.Enums.Gender;

import org.junit.Before;
import org.junit.Test;

public class ModifyClientActionTest {

    private ClientManager manager;
    private ActionInvoker invoker;
    private Client baseClient;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        manager = new ClientManager();
        baseClient = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        manager.addClient(baseClient);
    }

    @Test
    public void CheckClientModifyNameTest() throws Exception {
        ModifyClientAction action = new ModifyClientAction(baseClient);
        action.addChange("setFirstName", baseClient.getFirstName(), "New");
        invoker.execute(action);
        assertEquals("New", manager.getClients().get(0).getFirstName());
    }

    @Test
    public void CheckClientModifyUndoNameTest() throws Exception {
        ModifyClientAction action = new ModifyClientAction(baseClient);
        action.addChange("setFirstName", baseClient.getFirstName(), "New");
        invoker.execute(action);
        invoker.undo();
        assertEquals("First", manager.getClients().get(0).getFirstName());
    }

    @Test
    public void CheckClientMultipleUpdateValuesTest() throws Exception {
        ModifyClientAction action = new ModifyClientAction(baseClient);
        action.addChange("setFirstName", baseClient.getFirstName(), "New");
        action.addChange("setGender", baseClient.getGender(), Gender.FEMALE);
        invoker.execute(action);
        assertEquals("New", manager.getClients().get(0).getFirstName());
        assertEquals(Gender.FEMALE, manager.getClients().get(0).getGender());
    }

    @Test
    public void CheckClientMultipleUpdateValuesUndoTest() throws Exception {
        ModifyClientAction action = new ModifyClientAction(baseClient);
        action.addChange("setFirstName", baseClient.getFirstName(), "New");
        action.addChange("setGender", baseClient.getGender(), Gender.FEMALE);
        invoker.execute(action);
        invoker.undo();
        assertEquals("First", manager.getClients().get(0).getFirstName());
        assertEquals(Gender.UNSPECIFIED, manager.getClients().get(0).getGender());
    }

    @Test(expected = NoSuchMethodException.class)
    public void InvalidSetterFieldTest() throws Exception {
        ModifyClientAction action = new ModifyClientAction(baseClient);
        action.addChange("notAField", "Old", "New");
        invoker.execute(action);
    }

    @Test(expected = NoSuchFieldException.class)
    public void InvalidSetterAttributeTest() throws Exception {
        ModifyClientAction action = new ModifyClientAction(baseClient);
        action.addChange("setFirstName", 1, "New");
        invoker.execute(action);
    }
}
