package com.humanharvest.organz.commands.view;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Optional;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.ClientManagerMemory;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.exceptions.OrganAlreadyRegisteredException;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

public class PrintClientOrganTest extends BaseTest {

    private ClientManager spyClientManager;
    private PrintClientOrgan spyPrintClientOrgan;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void init() {
        spyClientManager = spy(new ClientManagerMemory());

        spyPrintClientOrgan = spy(new PrintClientOrgan(spyClientManager));
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void printuserorgan_invalid_format_id() {
        doNothing().when(spyClientManager).addClient(any());
        String[] inputs = {"-u", "notint", "-t", "donations"};

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
        when(spyClientManager.getClientByID(anyInt())).thenReturn(Optional.empty());
        String[] inputs = {"-u", "2", "-t", "donations"};

        CommandLine.run(spyPrintClientOrgan, System.out, inputs);

        verify(spyPrintClientOrgan, times(1)).run();
        assertThat(outContent.toString(), containsString("No client exists with that user ID"));
    }

    @Test
    public void printuserorgan_valid_no_organs() {
        Client client = new Client("First", "mid", "Last", LocalDate.of(1970, 1, 1), 1);

        when(spyClientManager.getClientByID(anyInt())).thenReturn(Optional.of(client));

        String[] inputs = {"-u", "1", "-t", "donations"};

        CommandLine.run(spyPrintClientOrgan, System.out, inputs);

        assertThat(outContent.toString(),
                containsString("User: 1. Name: First mid Last. Donation status: None"));
    }

    @Test
    public void printuserorgan_valid_one_organ() throws OrganAlreadyRegisteredException {
        Client client = new Client("First", "mid", "Last", LocalDate.of(1970, 1, 1), 1);
        client.setOrganDonationStatus(Organ.KIDNEY, true);

        when(spyClientManager.getClientByID(anyInt())).thenReturn(Optional.of(client));

        String[] inputs = {"-u", "1", "-t", "donations"};

        CommandLine.run(spyPrintClientOrgan, System.out, inputs);

        assertThat(outContent.toString(), containsString("User: 1. Name: First mid Last. Donation status: Kidney"));
    }

    @Test
    public void printuserorgan_valid_multiple_organs() throws OrganAlreadyRegisteredException {
        Client client = new Client("First", "mid", "Last", LocalDate.of(1970, 1, 1), 1);
        client.setOrganDonationStatus(Organ.LIVER, true);
        client.setOrganDonationStatus(Organ.KIDNEY, true);

        when(spyClientManager.getClientByID(anyInt())).thenReturn(Optional.of(client));

        String[] inputs = {"-u", "1", "-t", "donations"};

        CommandLine.run(spyPrintClientOrgan, System.out, inputs);

        assertTrue(outContent.toString().contains("User: 1. Name: First mid Last. Donation status: Kidney, Liver")
                || outContent.toString().contains("User: 1. Name: First mid Last. Donation status: Liver, Kidney"));
    }

    @Test
    public void printuserorgan_valid_one_organ_request() {
        Client client = new Client("First", "mid", "Last", LocalDate.of(1970, 1, 1), 1);
        TransplantRequest tr = new TransplantRequest(client, Organ.KIDNEY);
        client.addTransplantRequest(tr);

        when(spyClientManager.getClientByID(anyInt())).thenReturn(Optional.of(client));

        String[] inputs = {"-u", "1", "-t", "requests"};

        CommandLine.run(spyPrintClientOrgan, System.out, inputs);

        assertThat(outContent.toString(), containsString("User: 1. Name: First mid Last. Request status: Kidney"));
    }
}
