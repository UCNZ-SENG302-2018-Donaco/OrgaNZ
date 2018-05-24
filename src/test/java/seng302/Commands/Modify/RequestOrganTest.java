package seng302.Commands.Modify;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import seng302.Actions.ActionInvoker;
import seng302.Client;
import seng302.State.ClientManager;
import seng302.State.State;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

public class RequestOrganTest {
    private ClientManager spyClientManager;
    private RequestOrgan spyRequestOrgan;
    private Client testClient;

    @Before
    public void init() {
        spyClientManager = spy(new ClientManager());
        spyRequestOrgan = spy(new RequestOrgan(spyClientManager, new ActionInvoker()));
        testClient = new Client("Jan", "Michael", "Vincent", LocalDate.now(), 1);
        spyClientManager.addClient(testClient);

    }

    @Test
    public void validRequest1() {
        String[] inputs = {"-u", "1", "-o", "liver"};
        CommandLine.run(spyRequestOrgan, System.out, inputs);
        assertTrue(testClient.getTransplantRequests().size() == 1);
    }

    @Test
    public void validRequest2() {
        String[] inputs = {"-u", "1", "-organ", "bone marrow"};
        CommandLine.run(spyRequestOrgan, System.out, inputs);
        assertTrue(testClient.getTransplantRequests().size() == 1);
    }

    @Test
    public void organAlreadyRequested() {
        String[] inputs = {"-u", "1", "-organ", "bone marrow"};
        CommandLine.run(spyRequestOrgan, System.out, inputs);
        CommandLine.run(spyRequestOrgan, System.out, inputs); // Run the command twice
        assertTrue(testClient.getTransplantRequests().size() == 1);
    }

    @Test
    public void invalidUid() {
        String[] inputs = {"-u", "10", "-organ", "bone marrow"};
        CommandLine.run(spyRequestOrgan, System.out, inputs);
        assertTrue(testClient.getTransplantRequests().size() == 0);
    }

    @Test
    public void invalidOrgan() {
        String[] inputs = {"-u", "10", "-organ", "plumbus"};
        CommandLine.run(spyRequestOrgan, System.out, inputs);
        assertTrue(testClient.getTransplantRequests().size() == 0);
    }

    @Test
    public void missingParams1() {
        String[] inputs = {"-u", "10", "liver"};
        CommandLine.run(spyRequestOrgan, System.out, inputs);
        assertTrue(testClient.getTransplantRequests().size() == 0);
    }

    @Test
    public void missingParams2() {
        String[] inputs = {"-u", "10", "-organ"};
        CommandLine.run(spyRequestOrgan, System.out, inputs);
        assertTrue(testClient.getTransplantRequests().size() == 0);
    }

}