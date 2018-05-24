package seng302.Actions.Administrator;


import static org.junit.Assert.assertEquals;

import seng302.Actions.ActionInvoker;
import seng302.Administrator;
import seng302.State.AdministratorManager;
import seng302.State.AdministratorManagerMemory;

import org.junit.Before;
import org.junit.Test;

public class ModifyAdministratorActionTest {

    private AdministratorManager manager;
    private ActionInvoker invoker;
    private Administrator baseAdministrator;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        manager = new AdministratorManagerMemory();
        baseAdministrator = new Administrator("First", "pass");
        manager.addAdministrator(baseAdministrator);
    }

    @Test
    public void checkAdministratorModifyPasswordTest() throws Exception {
        ModifyAdministratorAction action = new ModifyAdministratorAction(baseAdministrator);
        action.addChange("setPassword", baseAdministrator.getPassword(), "New");
        invoker.execute(action);
        assertEquals("New", manager.getAdministrators().get(1).getPassword());
    }

    @Test
    public void checkAdministratorModifyUndoPasswordTest() throws Exception {
        ModifyAdministratorAction action = new ModifyAdministratorAction(baseAdministrator);
        action.addChange("setPassword", baseAdministrator.getPassword(), "New");
        invoker.execute(action);
        invoker.undo();
        assertEquals("pass", manager.getAdministrators().get(1).getPassword());
    }

    @Test(expected = NoSuchMethodException.class)
    public void invalidSetterFieldTest() throws Exception {
        ModifyAdministratorAction action = new ModifyAdministratorAction(baseAdministrator);
        action.addChange("notAField", "Old", "New");
        invoker.execute(action);
    }

    @Test(expected = NoSuchFieldException.class)
    public void invalidSetterAttributeTest() throws Exception {
        ModifyAdministratorAction action = new ModifyAdministratorAction(baseAdministrator);
        action.addChange("setPassword", 1, "New");
        invoker.execute(action);
    }
}
