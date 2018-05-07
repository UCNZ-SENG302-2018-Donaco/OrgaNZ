package seng302.Commands.View;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import seng302.Client;
import seng302.State.ClientManager;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

public class PrintClientOrganTest {

    private ClientManager spyClientManager;
    private PrintClientOrgan spyPrintClientOrgan;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void init() {
        spyClientManager = spy(new ClientManager());

        spyPrintClientOrgan = spy(new PrintClientOrgan(spyClientManager));
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void printuserorgan_invalid_format_id() {
        doNothing().when(spyClientManager).addClient(any());
        String[] inputs = {"-u", "notint"};

        CommandLine.run(spyPrintClientOrgan, System.out, inputs);

        verify(spyPrintClientOrgan, times(0)).run();
    }

    @Test
    public void printuserorgan_invalid_option() {
        String[] inputs = {"-u", "1", "--notanoption"};

        CommandLine.run(spyPrintClientOrgan, System.out, inputs);

        verify(spyPrintClientOrgan, times(0)).run();
    }

    @Test
    public void printuserorgan_non_existent_id() {
        when(spyClientManager.getClientByID(anyInt())).thenReturn(null);
        String[] inputs = {"-u", "2"};

        CommandLine.run(spyPrintClientOrgan, System.out, inputs);

        verify(spyPrintClientOrgan, times(1)).run();
        assertThat(outContent.toString(), containsString("No client exists with that user ID"));
    }

    @Test
    public void printuserorgan_valid_no_organs() {
        Client client = new Client("First", "mid", "Last", LocalDate.of(1970, 1, 1), 1);

        when(spyClientManager.getClientByID(anyInt())).thenReturn(client);

        String[] inputs = {"-u", "1"};

        CommandLine.run(spyPrintClientOrgan, System.out, inputs);

        assertThat(outContent.toString(),
                containsString("User: 1. Name: First mid Last, Donation status: No organs found"));
    }

    @Test
    public void printuserorgan_valid_one_organ() throws OrganAlreadyRegisteredException {
        Client client = new Client("First", "mid", "Last", LocalDate.of(1970, 1, 1), 1);
        client.setOrganDonationStatus(Organ.KIDNEY, true);

        when(spyClientManager.getClientByID(anyInt())).thenReturn(client);

        String[] inputs = {"-u", "1"};

        CommandLine.run(spyPrintClientOrgan, System.out, inputs);

        assertThat(outContent.toString(), containsString("User: 1. Name: First mid Last, Donation status: Kidney"));
    }

    @Test
    public void printuserorgan_valid_multiple_organs() throws OrganAlreadyRegisteredException {
        Client client = new Client("First", "mid", "Last", LocalDate.of(1970, 1, 1), 1);
        client.setOrganDonationStatus(Organ.LIVER, true);
        client.setOrganDonationStatus(Organ.KIDNEY, true);

        when(spyClientManager.getClientByID(anyInt())).thenReturn(client);

        String[] inputs = {"-u", "1"};

        CommandLine.run(spyPrintClientOrgan, System.out, inputs);

        assertTrue(outContent.toString().contains("User: 1. Name: First mid Last, Donation status: Kidney, Liver")
                || outContent.toString().contains("User: 1. Name: First mid Last, Donation status: Liver, Kidney"));
    }
}
