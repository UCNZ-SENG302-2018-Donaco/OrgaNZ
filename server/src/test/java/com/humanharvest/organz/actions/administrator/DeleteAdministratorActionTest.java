package com.humanharvest.organz.actions.administrator;

import static org.junit.Assert.assertEquals;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.state.AdministratorManager;
import com.humanharvest.organz.state.AdministratorManagerMemory;

import org.junit.Before;
import org.junit.Test;

public class DeleteAdministratorActionTest extends BaseTest {

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
    public void CheckClinicianDeletedTest() {
        DeleteAdministratorAction action = new DeleteAdministratorAction(baseAdministrator, manager);
        invoker.execute(action);
        assertEquals(1, manager.getAdministrators().size());
    }

    @Test
    public void CheckClinicianDeletedUndoTest() {
        DeleteAdministratorAction action = new DeleteAdministratorAction(baseAdministrator, manager);
        invoker.execute(action);
        invoker.undo();
        assertEquals(2, manager.getAdministrators().size());
    }

    @Test
    public void CheckClinicianMultipleDeletesOneUndoTest() {
        Administrator second = new Administrator("First2", "pass");
        manager.addAdministrator(second);

        DeleteAdministratorAction action = new DeleteAdministratorAction(baseAdministrator, manager);
        DeleteAdministratorAction secondAction = new DeleteAdministratorAction(second, manager);

        invoker.execute(action);
        invoker.execute(secondAction);

        invoker.undo();
        assertEquals(second, manager.getAdministrators().get(1));
        assertEquals(2, manager.getAdministrators().size());
    }

    @Test
    public void CheckClinicianMultipleDeletesOneUndoRedoTest() {
        Administrator second = new Administrator("First2", "pass");
        manager.addAdministrator(second);

        DeleteAdministratorAction action = new DeleteAdministratorAction(baseAdministrator, manager);
        DeleteAdministratorAction secondAction = new DeleteAdministratorAction(second, manager);

        invoker.execute(action);
        invoker.execute(secondAction);

        invoker.undo();
        assertEquals(second, manager.getAdministrators().get(1));
        assertEquals(2, manager.getAdministrators().size());

        invoker.redo();

        assertEquals(1, manager.getAdministrators().size());
    }
}
