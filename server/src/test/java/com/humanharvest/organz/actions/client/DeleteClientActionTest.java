package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.ClientManagerMemory;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class DeleteClientActionTest extends BaseTest {

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
    public void CheckClientDeletedTest() {
        DeleteClientAction action = new DeleteClientAction(baseClient, manager);
        invoker.execute(action);
        assertEquals(0, manager.getClients().size());
    }

    @Test
    public void CheckClientDeletedUndoTest() {
        DeleteClientAction action = new DeleteClientAction(baseClient, manager);
        invoker.execute(action);
        invoker.undo();
        assertEquals(1, manager.getClients().size());
    }

    @Test
    public void CheckClientMultipleDeletesOneUndoTest() {
        Client second = new Client("SecondClient", null, "Last", LocalDate.of(1970, 1, 1), 2);
        manager.addClient(second);

        DeleteClientAction action = new DeleteClientAction(baseClient, manager);
        DeleteClientAction secondAction = new DeleteClientAction(second, manager);

        invoker.execute(action);
        invoker.execute(secondAction);

        invoker.undo();
        assertEquals(second, manager.getClients().get(0));
        assertEquals(1, manager.getClients().size());
    }

    @Test
    public void CheckClientMultipleDeletesOneUndoRedoTest() {
        Client second = new Client("SecondClient", null, "Last", LocalDate.of(1970, 1, 1), 2);
        manager.addClient(second);

        DeleteClientAction action = new DeleteClientAction(baseClient, manager);
        DeleteClientAction secondAction = new DeleteClientAction(second, manager);

        invoker.execute(action);
        invoker.execute(secondAction);

        invoker.undo();
        assertEquals(second, manager.getClients().get(0));
        assertEquals(1, manager.getClients().size());

        invoker.redo();

        assertEquals(0, manager.getClients().size());
    }
}
