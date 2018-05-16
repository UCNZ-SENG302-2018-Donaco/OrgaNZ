package seng302.Commands.Modify;


import static org.mockito.Mockito.*;

import seng302.Actions.ActionInvoker;
import seng302.HistoryManager;
import seng302.State.ClientManager;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

public class CreateClientTest {

    private ClientManager spyClientManager;

    private CreateClient spyCreateClient;

    @Before
    public void init() {
        HistoryManager.createTestManager();

        spyClientManager = spy(new ClientManager());
        spyCreateClient = spy(new CreateClient(spyClientManager, new ActionInvoker()));
    }

    @Test
    public void createuser_valid() {
        doNothing().when(spyClientManager).addClient(any());
        String[] inputs = {"-f", "Jack", "-l", "Steel", "-d", "21/04/1997"};

        CommandLine.run(spyCreateClient, System.out, inputs);

        verify(spyClientManager, times(1)).addClient(any());
    }

    @Test
    public void createuser_invalidDOB() {
        doNothing().when(spyClientManager).addClient(any());
        String[] inputs = {"-f", "Jack", "-l", "Steel", "-d", "21/04/197"};

        CommandLine.run(spyCreateClient, System.out, inputs);

        verify(spyClientManager, times(0)).addClient(any());
    }


    @Test
    public void createuser_invalidFieldCountLow() {
        String[] inputs = {"-f", "Jack", "-l", "Steel"};

        CommandLine.run(spyCreateClient, System.out, inputs);

        verify(spyCreateClient, times(0)).run();
    }

    @Test
    public void createuser_invalidFieldCountHigh() {
        String[] inputs = {"-f", "Jack", "-l", "Steel", "-d", "21/04/1997", "extra"};

        CommandLine.run(spyCreateClient, System.out, inputs);

        verify(spyClientManager, times(0)).addClient(any());
    }

    @Test
    public void createuser_duplicateAccept() {
        when(spyClientManager.collisionExists(any(), any(), any())).thenReturn(true);
        doNothing().when(spyClientManager).addClient(any());
        String[] inputs = {"-f", "Jack", "-l", "Steel", "-d", "21/04/1997", "--force"};

        CommandLine.run(spyCreateClient, System.out, inputs);

        verify(spyClientManager, times(1)).addClient(any());
    }

    @Test
    public void createuser_duplicateReject() {
        when(spyClientManager.collisionExists(any(), any(), any())).thenReturn(true);
        doNothing().when(spyClientManager).addClient(any());
        String[] inputs = {"-f", "Jack", "-l", "Steel", "-d", "21/04/1997"};

        CommandLine.run(spyCreateClient, System.out, inputs);

        verify(spyClientManager, times(0)).addClient(any());
    }
}
