package com.humanharvest.organz.commands.view;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.ClientManagerMemory;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.exceptions.OrganAlreadyRegisteredException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import picocli.CommandLine;

public class PrintAllOrganTest extends BaseTest {

    private ClientManager spyClientManager;
    private PrintAllOrgan spyPrintAllOrgan;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void init() {
        spyClientManager = spy(new ClientManagerMemory());

        spyPrintAllOrgan = Mockito.spy(new PrintAllOrgan(spyClientManager));
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void printallorgan_no_clients() {
        ArrayList<Client> clients = new ArrayList<>();

        when(spyClientManager.getClients()).thenReturn(clients);
        String[] inputs = {"-t", "donations"};

        CommandLine.run(spyPrintAllOrgan, System.out, inputs);

        assertThat(outContent.toString(), containsString("No clients exist"));
    }

    @Test
    @Ignore
    public void printallorgan_no_type_def() {
        ArrayList<Client> clients = new ArrayList<>();

        when(spyClientManager.getClients()).thenReturn(clients);
        String[] inputs = {};

        CommandLine.run(spyPrintAllOrgan, System.out, inputs);

        assertThat(outContent.toString(), containsString("Missing required option '-t=<type>'"));
    }

    @Test
    public void printallorgan_single_client() throws OrganAlreadyRegisteredException {
        Client client = new Client("First", "mid", "Last", LocalDate.of(1970, 1, 1), 1);
        client.setOrganDonationStatus(Organ.LIVER, true);
        client.setOrganDonationStatus(Organ.KIDNEY, true);

        ArrayList<Client> clients = new ArrayList<>();
        clients.add(client);

        when(spyClientManager.getClients()).thenReturn(clients);
        String[] inputs = {"-t", "donations"};

        CommandLine.run(spyPrintAllOrgan, System.out, inputs);

        assertTrue(outContent.toString().contains("User: 1. Name: First mid Last. Donation status: Kidney, Liver")
                || outContent.toString().contains("User: 1. Name: First mid Last. Donation status: Liver, Kidney"));
    }

    @Test
    public void printallorgan_single_client_receiving() {
        Client client = new Client("First", "mid", "Last", LocalDate.of(1970, 1, 1), 1);
        TransplantRequest tr1 = new TransplantRequest(client, Organ.LIVER);
        TransplantRequest tr2 = new TransplantRequest(client, Organ.KIDNEY);
        client.addTransplantRequest(tr1);
        client.addTransplantRequest(tr2);

        ArrayList<Client> clients = new ArrayList<>();
        clients.add(client);

        when(spyClientManager.getClients()).thenReturn(clients);
        String[] inputs = {"-t", "requests"};

        CommandLine.run(spyPrintAllOrgan, System.out, inputs);

        assertTrue(outContent.toString().contains("User: 1. Name: First mid Last. Request status: Kidney, Liver")
                || outContent.toString().contains("User: 1. Name: First mid Last. Request status: Liver, Kidney"));
    }

    @Test
    public void printallorgan_multiple_clients() throws OrganAlreadyRegisteredException {
        Client client = new Client("First", "mid", "Last", LocalDate.of(1970, 1, 1), 1);
        Client client2 = new Client("FirstTwo", null, "LastTwo", LocalDate.of(1971, 2, 2), 2);
        Client client3 = new Client("FirstThree", null, "LastThree", LocalDate.of(1971, 2, 2), 3);
        client.setOrganDonationStatus(Organ.LIVER, true);
        client.setOrganDonationStatus(Organ.KIDNEY, true);
        client2.setOrganDonationStatus(Organ.CONNECTIVE_TISSUE, true);

        ArrayList<Client> clients = new ArrayList<>();
        clients.add(client);
        clients.add(client2);
        clients.add(client3);

        when(spyClientManager.getClients()).thenReturn(clients);
        String[] inputs = {"-t", "donations"};

        CommandLine.run(spyPrintAllOrgan, System.out, inputs);

        assertTrue(outContent.toString().contains("User: 1. Name: First mid Last. Donation status: Kidney, Liver")
                || outContent.toString().contains("User: 1. Name: First mid Last. Donation status: Liver, Kidney"));
        assertThat(outContent.toString(),
                containsString("User: 2. Name: FirstTwo LastTwo. Donation status: Connective tissue"));
        assertThat(outContent.toString(),
                containsString("User: 3. Name: FirstThree LastThree. Donation status: None"));
    }
}
