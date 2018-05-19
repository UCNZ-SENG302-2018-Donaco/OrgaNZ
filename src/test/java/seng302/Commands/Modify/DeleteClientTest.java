package seng302.Commands.Modify;


import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

import seng302.Actions.ActionInvoker;
import seng302.Client;
import seng302.State.ClientManager;
import seng302.State.ClientManagerMemory;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

public class DeleteClientTest {

    private ClientManager spyClientManager;
    private DeleteClient spyDeleteClient;

    @Before
    public void init() {
        spyClientManager = spy(new ClientManagerMemory());
        spyDeleteClient = spy(new DeleteClient(spyClientManager, new ActionInvoker()));

    }

    @Test
    public void deleteuser_invalid_format_id() {
        doNothing().when(spyClientManager).addClient(any());
        String[] inputs = {"-u", "notint"};

        CommandLine.run(spyDeleteClient, System.out, inputs);

        verify(spyDeleteClient, times(0)).run();
    }

    @Test
    public void deleteuser_invalid_option() {
        String[] inputs = {"-u", "1", "--notanoption"};

        CommandLine.run(spyDeleteClient, System.out, inputs);

        verify(spyDeleteClient, times(0)).run();
    }

    @Test
    public void deleteuser_non_existent_id() {
        when(spyClientManager.getClientByID(anyInt())).thenReturn(null);
        String[] inputs = {"-u", "2"};

        CommandLine.run(spyDeleteClient, System.out, inputs);

        verify(spyDeleteClient, times(1)).run();
        verify(spyClientManager, times(0)).removeClient(any());
    }

    @Test
    public void deleteuser_valid_reject() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        when(spyClientManager.getClientByID(anyInt())).thenReturn(client);
        String[] inputs = {"-u", "1"};

        ByteArrayInputStream in = new ByteArrayInputStream("n".getBytes());
        System.setIn(in);

        CommandLine.run(spyDeleteClient, System.out, inputs);

        verify(spyClientManager, times(0)).removeClient(client);
    }

    @Test
    public void deleteuser_valid_accept() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        when(spyClientManager.getClientByID(anyInt())).thenReturn(client);
        String[] inputs = {"-u", "1"};

        ByteArrayInputStream in = new ByteArrayInputStream("y".getBytes());
        System.setIn(in);

        CommandLine.run(spyDeleteClient, System.out, inputs);

        verify(spyClientManager, times(1)).removeClient(client);
    }
}
