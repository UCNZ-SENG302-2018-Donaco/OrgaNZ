package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.ClientManagerMemory;
import com.humanharvest.organz.utilities.enums.Organ;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AddTransplantRequestActionTest extends BaseTest {

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
