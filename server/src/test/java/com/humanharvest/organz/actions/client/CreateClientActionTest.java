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

public class CreateClientActionTest extends BaseTest {

    private ClientManager manager;
    private ActionInvoker invoker;
    private Client baseClient;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        manager = new ClientManagerMemory();
        baseClient = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
    }

    @Test
    public void CheckClientAddedTest() {
        CreateClientAction action = new CreateClientAction(baseClient, manager);
        invoker.execute(action);
        assertEquals(1, manager.getClients().size());
    }

    @Test
    public void CheckClientAddedUndoTest() {
        CreateClientAction action = new CreateClientAction(baseClient, manager);
        invoker.execute(action);
        invoker.undo();
        assertEquals(0, manager.getClients().size());
    }

    @Test
    public void CheckClientMultipleAddsOneUndoTest() {
        CreateClientAction action = new CreateClientAction(baseClient, manager);
        invoker.execute(action);
        Client second = new Client("SecondClient", null, "Last", LocalDate.of(1970, 1, 1), 2);
        CreateClientAction secondAction = new CreateClientAction(second, manager);
        invoker.execute(secondAction);
        invoker.undo();
        assertEquals(baseClient, manager.getClients().get(0));
        assertEquals(1, manager.getClients().size());
    }

    @Test
    public void CheckClientMultipleAddsOneUndoRedoTest() {
        CreateClientAction action = new CreateClientAction(baseClient, manager);
        invoker.execute(action);

        Client second = new Client("SecondClient", null, "Last", LocalDate.of(1970, 1, 1), 2);
        CreateClientAction secondAction = new CreateClientAction(second, manager);
        invoker.execute(secondAction);

        invoker.undo();

        assertEquals(baseClient, manager.getClients().get(0));
        assertEquals(1, manager.getClients().size());

        invoker.redo();

        assertEquals(baseClient, manager.getClients().get(0));
        assertEquals(second, manager.getClients().get(1));
        assertEquals(2, manager.getClients().size());
    }

}
