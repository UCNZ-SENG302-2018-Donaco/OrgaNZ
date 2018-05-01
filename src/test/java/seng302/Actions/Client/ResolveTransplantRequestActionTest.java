package seng302.Actions.Client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static seng302.TransplantRequest.RequestStatus.CANCELLED;
import static seng302.TransplantRequest.RequestStatus.COMPLETED;
import static seng302.TransplantRequest.RequestStatus.WAITING;

import java.time.Duration;
import java.time.LocalDateTime;

import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Client;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Organ;

import org.junit.Before;
import org.junit.Test;

public class ResolveTransplantRequestActionTest {

    private ActionInvoker invoker;
    private TransplantRequest testRequest;

    @Before
    public void beforeEach() {
        invoker = new ActionInvoker();
        testRequest = new TransplantRequest(new Client(1), Organ.BONE_MARROW);
    }

    @Test
    public void cancelTest() {
        Action action = new ResolveTransplantRequestAction(testRequest, CANCELLED);
        invoker.execute(action);
        assertEquals(CANCELLED, testRequest.getStatus());
    }

    @Test
    public void completeTest() {
        Action action = new ResolveTransplantRequestAction(testRequest, COMPLETED);
        invoker.execute(action);
        assertEquals(COMPLETED, testRequest.getStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void statusNotValidResolutionTest() {
        new ResolveTransplantRequestAction(testRequest, WAITING);
    }

    @Test
    public void correctResolvedDateTest() {
        Action action = new ResolveTransplantRequestAction(testRequest, CANCELLED);
        invoker.execute(action);

        Duration timeDiff = Duration.between(LocalDateTime.now(), testRequest.getResolvedDate());
        assertTrue(Math.abs(timeDiff.getSeconds()) < 3);
    }
}
