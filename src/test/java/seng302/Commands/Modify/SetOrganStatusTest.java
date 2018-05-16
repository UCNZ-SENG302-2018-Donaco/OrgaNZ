package seng302.Commands.Modify;


import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import seng302.Actions.ActionInvoker;
import seng302.Client;
import seng302.HistoryManager;
import seng302.State.ClientManager;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import picocli.CommandLine;

public class SetOrganStatusTest {

    private ClientManager spyClientManager;
    private SetOrganStatus spySetOrganStatus;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void init() {
        HistoryManager.createTestManager();

        spyClientManager = spy(new ClientManager());
        spySetOrganStatus = spy(new SetOrganStatus(spyClientManager, new ActionInvoker()));

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

    }

    @Test
    public void setorganstatus_invalid_format_id() {
        String[] inputs = {"-u", "notint"};

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        verify(spySetOrganStatus, times(0)).run();
    }

    @Test
    public void setorganstatus_invalid_option() {
        String[] inputs = {"-u", "1", "--notanoption"};

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        verify(spySetOrganStatus, times(0)).run();
    }

    @Test
    public void setorganstatus_non_existent_id() {
        when(spyClientManager.getClientByID(anyInt())).thenReturn(null);
        String[] inputs = {"-u", "2"};

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        verify(spySetOrganStatus, times(1)).run();
    }

    @Test
    public void setorganstatus_valid_set_to_true() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        when(spyClientManager.getClientByID(anyInt())).thenReturn(client);
        String[] inputs = {"-u", "1", "--liver"};

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        assertEquals(true, client.getOrganDonationStatus().get(Organ.LIVER));
    }

    @Test
    public void setorganstatus_valid_set_to_false() throws OrganAlreadyRegisteredException {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);

        client.setOrganDonationStatus(Organ.LIVER, true);

        when(spyClientManager.getClientByID(anyInt())).thenReturn(client);
        String[] inputs = {"-u", "1", "--liver=false"};

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        assertEquals(false, client.getOrganDonationStatus().get(Organ.LIVER));
    }

    @Test
    public void setorganstatus_invalid_set_to_true_already_true() throws OrganAlreadyRegisteredException {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);

        client.setOrganDonationStatus(Organ.LIVER, true);

        when(spyClientManager.getClientByID(anyInt())).thenReturn(client);
        String[] inputs = {"-u", "1", "--liver"};

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        assertThat(outContent.toString(), containsString("Liver is already registered for donation"));
    }

    @Test
    public void setorganstatus_valid_multiple_updates() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);

        when(spyClientManager.getClientByID(anyInt())).thenReturn(client);
        String[] inputs = {"-u", "1", "--liver", "--kidney"};

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        assertEquals(true, client.getOrganDonationStatus().get(Organ.LIVER));
        assertEquals(true, client.getOrganDonationStatus().get(Organ.KIDNEY));
    }

    @Test
    public void setorganstatus_valid_multiple_updates_some_invalid() throws OrganAlreadyRegisteredException {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);

        client.setOrganDonationStatus(Organ.LIVER, true);
        client.setOrganDonationStatus(Organ.BONE, true);

        when(spyClientManager.getClientByID(anyInt())).thenReturn(client);
        String[] inputs = {"-u", "1", "--liver", "--kidney", "--bone=false"};

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        assertThat(outContent.toString(), containsString("Liver is already registered for donation"));

        assertEquals(true, client.getOrganDonationStatus().get(Organ.KIDNEY));
        assertEquals(false, client.getOrganDonationStatus().get(Organ.BONE));
    }
}
