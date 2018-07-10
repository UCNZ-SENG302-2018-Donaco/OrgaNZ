package com.humanharvest.organz.commands.modify;

import static org.mockito.Mockito.*;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.ClientManagerMemory;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

public class CreateClientTest extends BaseTest {

    private ClientManager spyClientManager;

    private CreateClient spyCreateClient;

    @Before
    public void init() {
        spyClientManager = spy(new ClientManagerMemory());

        spyCreateClient = spy(new CreateClient(spyClientManager, new ActionInvoker()));

    }

    @Test
    public void createclient_valid() {
        doNothing().when(spyClientManager).addClient(any());
        String[] inputs = {"-f", "Jack", "-l", "Steel", "-d", "21/04/1997"};

        CommandLine.run(spyCreateClient, System.out, inputs);

        verify(spyClientManager, times(1)).addClient(any());
    }

    @Test
    public void createclient_invalidDOB() {
        doNothing().when(spyClientManager).addClient(any());
        String[] inputs = {"-f", "Jack", "-l", "Steel", "-d", "21/04/197"};

        CommandLine.run(spyCreateClient, System.out, inputs);

        verify(spyClientManager, times(0)).addClient(any());
    }


    @Test
    public void createclient_invalidFieldCountLow() {
        String[] inputs = {"-f", "Jack", "-l", "Steel"};

        CommandLine.run(spyCreateClient, System.out, inputs);

        verify(spyCreateClient, times(0)).run();
    }

    @Test
    public void createclient_invalidFieldCountHigh() {
        String[] inputs = {"-f", "Jack", "-l", "Steel", "-d", "21/04/1997", "extra"};

        CommandLine.run(spyCreateClient, System.out, inputs);

        verify(spyClientManager, times(0)).addClient(any());
    }

    @Test
    public void createclient_duplicateAccept() {
        when(spyClientManager.collisionExists(any(), any(), any())).thenReturn(true);
        doNothing().when(spyClientManager).addClient(any());
        String[] inputs = {"-f", "Jack", "-l", "Steel", "-d", "21/04/1997", "--force"};

        CommandLine.run(spyCreateClient, System.out, inputs);

        verify(spyClientManager, times(1)).addClient(any());
    }

    @Test
    public void createclient_duplicateReject() {
        when(spyClientManager.collisionExists(any(), any(), any())).thenReturn(true);
        doNothing().when(spyClientManager).addClient(any());
        String[] inputs = {"-f", "Jack", "-l", "Steel", "-d", "21/04/1997"};

        CommandLine.run(spyCreateClient, System.out, inputs);

        verify(spyClientManager, times(0)).addClient(any());
    }
}
