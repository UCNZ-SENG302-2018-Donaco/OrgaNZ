package seng302.Actions.Client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Client;
import seng302.State.ClientManager;
import seng302.State.ClientManagerMemory;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Organ;

import org.junit.Before;
import org.junit.Test;

public class AddTransplantRequestActionTest {

    private ActionInvoker invoker;
    private ClientManager manager;
    private Client testClient;

    @Before
    public void beforeEach() {
        invoker = new ActionInvoker();
        manager = new ClientManagerMemory();
        testClient = new Client("Bob", "Henry", "Ellison", LocalDate.of(1962, 4, 9), 1);
    }

    @Test
    public void checkRequestWasAddedTest() {
        TransplantRequest request = new TransplantRequest(testClient, Organ.HEART);
        Action action = new AddTransplantRequestAction(testClient, request, manager);
        invoker.execute(action);
        assertTrue(testClient.getTransplantRequests().contains(request));
    }

    @Test
    public void checkRequestWasAddedThenUndoneTest() {
        TransplantRequest request = new TransplantRequest(testClient, Organ.HEART);
        Action action = new AddTransplantRequestAction(testClient, request, manager);
        invoker.execute(action);
        assertTrue(testClient.getTransplantRequests().contains(request));

        invoker.undo();
        assertFalse(testClient.getTransplantRequests().contains(request));
    }
}
