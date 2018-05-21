package seng302.Commands.Modify;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import seng302.Actions.ActionInvoker;
import seng302.Client;
import seng302.State.ClientManager;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Enums.RequestStatus;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

public class ResolveOrganTest {
    private ClientManager spyClientManager;
    private ResolveOrgan spyResolveOrgan;
    private Client testClient;

    @Before
    public void init() {
        spyClientManager = spy(new ClientManager());
        spyResolveOrgan = spy(new ResolveOrgan(spyClientManager, new ActionInvoker()));
        testClient = new Client("Jan", "Michael", "Vincent", LocalDate.now(), 1);
        spyClientManager.addClient(testClient);

    }

    @Test
    public void validResolve1() {
        TransplantRequest tr = new TransplantRequest(testClient, Organ.LIVER);
        testClient.addTransplantRequest(tr);
        String[] inputs = {"-u", "1", "-o", "liver", "-r", "input error"};
        CommandLine.run(spyResolveOrgan, System.out, inputs);
        assertTrue(tr.getStatus() == RequestStatus.CANCELLED);
    }

    @Test
    public void validResolve2() {
        TransplantRequest tr = new TransplantRequest(testClient, Organ.HEART);
        testClient.addTransplantRequest(tr);
        String[] inputs = {"-u", "1", "-o", "heart", "-reason", "Transplant completed"};
        CommandLine.run(spyResolveOrgan, System.out, inputs);
        assertTrue(tr.getStatus() == RequestStatus.COMPLETED);
    }

    @Test
    public void validResolveCustom() {
        TransplantRequest tr = new TransplantRequest(testClient, Organ.PANCREAS);
        testClient.addTransplantRequest(tr);
        String[] inputs = {"-u", "1", "-o", "pancreas", "-reason", "Custom reason...", "-m", "A message for a custom "
                + "reason"};
        CommandLine.run(spyResolveOrgan, System.out, inputs);
        assertTrue(tr.getStatus() == RequestStatus.CANCELLED);
    }

    @Test
    public void resolveNonExisting() {
        TransplantRequest tr = new TransplantRequest(testClient, Organ.PANCREAS);
        testClient.addTransplantRequest(tr);
        String[] inputs = {"-u", "1", "-o", "liver", "-reason", "input error"};
        CommandLine.run(spyResolveOrgan, System.out, inputs);
        assertTrue(tr.getStatus() == RequestStatus.WAITING);
    }

    @Test
    public void resolveInvalidParam1() {
        TransplantRequest tr = new TransplantRequest(testClient, Organ.LIVER);
        testClient.addTransplantRequest(tr);
        String[] inputs = {"-u", "1", "-o", "liver", "-r", "ops this input is invalid"};
        CommandLine.run(spyResolveOrgan, System.out, inputs);
        assertTrue(tr.getStatus() == RequestStatus.WAITING);
    }

    @Test
    public void resolveInvalidParam2() {
        TransplantRequest tr = new TransplantRequest(testClient, Organ.HEART);
        testClient.addTransplantRequest(tr);
        String[] inputs = {"-u", "1", "-o", "liver", "-r", "input error"};
        CommandLine.run(spyResolveOrgan, System.out, inputs);
        assertTrue(tr.getStatus() == RequestStatus.WAITING);
    }

    @Test
    public void resolveInvalidParam3() {
        TransplantRequest tr = new TransplantRequest(testClient, Organ.LUNG);
        testClient.addTransplantRequest(tr);
        String[] inputs = {"-u", "1", "-o", "liver", "-r", "input error", "-m"};
        CommandLine.run(spyResolveOrgan, System.out, inputs);
        assertTrue(tr.getStatus() == RequestStatus.WAITING);
    }

}