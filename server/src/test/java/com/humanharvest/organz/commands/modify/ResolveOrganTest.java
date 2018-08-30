package com.humanharvest.organz.commands.modify;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

import java.time.LocalDate;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.ClientManagerMemory;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

public class ResolveOrganTest extends BaseTest {

    private ClientManager spyClientManager;
    private ResolveOrgan spyResolveOrgan;
    private Client testClient;

    @Before
    public void init() {
        spyClientManager = spy(new ClientManagerMemory());
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
        assertTrue(tr.getStatus() == TransplantRequestStatus.CANCELLED);
    }

    @Test
    public void validResolve2() {
        TransplantRequest tr = new TransplantRequest(testClient, Organ.HEART);
        testClient.addTransplantRequest(tr);
        String[] inputs = {"-u", "1", "-o", "heart", "-reason", "Transplant completed"};
        CommandLine.run(spyResolveOrgan, System.out, inputs);
        assertTrue(tr.getStatus() == TransplantRequestStatus.COMPLETED);
    }

    @Test
    public void validResolveCustom() {
        TransplantRequest tr = new TransplantRequest(testClient, Organ.PANCREAS);
        testClient.addTransplantRequest(tr);
        String[] inputs = {"-u", "1", "-o", "pancreas", "-reason", "Custom reason...", "-m", "A message for a custom "
                + "reason"};
        CommandLine.run(spyResolveOrgan, System.out, inputs);
        assertTrue(tr.getStatus() == TransplantRequestStatus.CANCELLED);
    }

    @Test
    public void resolveNonExisting() {
        TransplantRequest tr = new TransplantRequest(testClient, Organ.PANCREAS);
        testClient.addTransplantRequest(tr);
        String[] inputs = {"-u", "1", "-o", "liver", "-reason", "input error"};
        CommandLine.run(spyResolveOrgan, System.out, inputs);
        assertTrue(tr.getStatus() == TransplantRequestStatus.WAITING);
    }

    @Test
    public void resolveInvalidParam1() {
        TransplantRequest tr = new TransplantRequest(testClient, Organ.LIVER);
        testClient.addTransplantRequest(tr);
        String[] inputs = {"-u", "1", "-o", "liver", "-r", "ops this input is invalid"};
        CommandLine.run(spyResolveOrgan, System.out, inputs);
        assertTrue(tr.getStatus() == TransplantRequestStatus.WAITING);
    }

    @Test
    public void resolveInvalidParam2() {
        TransplantRequest tr = new TransplantRequest(testClient, Organ.HEART);
        testClient.addTransplantRequest(tr);
        String[] inputs = {"-u", "1", "-o", "liver", "-r", "input error"};
        CommandLine.run(spyResolveOrgan, System.out, inputs);
        assertTrue(tr.getStatus() == TransplantRequestStatus.WAITING);
    }

    @Test
    public void resolveInvalidParam3() {
        TransplantRequest tr = new TransplantRequest(testClient, Organ.LUNG);
        testClient.addTransplantRequest(tr);
        String[] inputs = {"-u", "1", "-o", "liver", "-r", "input error", "-m"};
        CommandLine.run(spyResolveOrgan, System.out, inputs);
        assertTrue(tr.getStatus() == TransplantRequestStatus.WAITING);
    }

}