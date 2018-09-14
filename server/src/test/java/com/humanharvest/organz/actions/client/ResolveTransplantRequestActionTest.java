package com.humanharvest.organz.actions.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.ClientManagerMemory;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;

import org.junit.Before;
import org.junit.Test;

public class ResolveTransplantRequestActionTest extends BaseTest {

    private ActionInvoker invoker;
    private ClientManager manager;
    private TransplantRequest testRequest;

    @Before
    public void beforeEach() {
        invoker = new ActionInvoker();
        manager = new ClientManagerMemory();
        testRequest = new TransplantRequest(new Client(1), Organ.BONE_MARROW);
    }

    @Test
    public void cancelTest() {
        Action action = new ResolveTransplantRequestAction(
                testRequest,
                TransplantRequestStatus.CANCELLED,
                "Cancelled.",
                LocalDateTime.now(),
                manager);
        invoker.execute(action);
        assertEquals(TransplantRequestStatus.CANCELLED, testRequest.getStatus());
    }

    @Test
    public void completeTest() {
        Action action = new ResolveTransplantRequestAction(
                testRequest,
                TransplantRequestStatus.COMPLETED,
                "Completed.",
                LocalDateTime.now(),
                manager);
        invoker.execute(action);
        assertEquals(TransplantRequestStatus.COMPLETED, testRequest.getStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void statusNotValidResolutionTest() {
        new ResolveTransplantRequestAction(
                testRequest,
                TransplantRequestStatus.WAITING,
                "Waiting.",
                LocalDateTime.now(),
                manager);
    }

    @Test
    public void correctResolvedDateTest() {
        Action action = new ResolveTransplantRequestAction(
                testRequest,
                TransplantRequestStatus.CANCELLED,
                "Cancelled.",
                LocalDateTime.now(),
                manager);
        invoker.execute(action);

        Duration timeDiff = Duration.between(LocalDateTime.now(), testRequest.getResolvedDateTime());
        assertTrue(Math.abs(timeDiff.getSeconds()) < 3);
    }
}
