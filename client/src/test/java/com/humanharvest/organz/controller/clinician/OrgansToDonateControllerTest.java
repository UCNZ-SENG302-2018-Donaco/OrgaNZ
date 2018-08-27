package com.humanharvest.organz.controller.clinician;

import static org.junit.Assert.fail;
import static org.testfx.api.FxAssert.verifyThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.exceptions.OrganAlreadyRegisteredException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;

import org.junit.Ignore;
import org.junit.Test;
import org.testfx.matcher.control.TableViewMatchers;

public class OrgansToDonateControllerTest extends ControllerTest {

    private static final int ROWS_PER_PAGE = 30;

    private Administrator testAdmin = new Administrator("username", "password");

    private Client client1 = new Client(1);
    private Client client2 = new Client(2);
    private Client client3 = new Client(3);
    private Collection<Client> clients = new ArrayList<>();

    private int numberOfOrgansBeingDonated;

    @Override
    protected Page getPage() {
        return Page.ORGANS_TO_DONATE;
    }

    @Override
    protected void initState() {
        State.reset();
        State.login(testAdmin);
        mainController.setWindowContext(WindowContext.defaultContext());

        // Setup clients
        client1.setFirstName("Fred");

        // Add clients to list of clients
        clients.add(client1);
        clients.add(client2);
        clients.add(client3);

        // Register organs to donate
        try {
            client1.setOrganDonationStatus(Organ.LIVER, true);
            client2.setOrganDonationStatus(Organ.LIVER, true);
            client3.setOrganDonationStatus(Organ.LIVER, true);
            client1.setOrganDonationStatus(Organ.LUNG, true);
            numberOfOrgansBeingDonated = 4;
        } catch (OrganAlreadyRegisteredException e) {
            fail("OrganAlreadyRegisteredException thrown when setting up the clients");
        }

        // Mark them as dead
        for (Client client : clients) {
            client.markDead(LocalDate.now(), LocalTime.now(), Country.NZ, "Canterbury", "Christchurch");
        }

        State.getClientManager().setClients(clients);
    }

    @Test
    public void hasAllRowsTest() {
        verifyThat("#tableView", TableViewMatchers.hasNumRows(numberOfOrgansBeingDonated));
    }

    @Ignore("Pagination not implemented on this page.")
    @Test
    public void paginationTest() {
        for (int i = 10; i < 50; i++) {
            Client client = new Client(i);
            client.markDead(LocalDate.now(), LocalTime.now(), Country.NZ, "Canterbury", "Christchurch");
            clients.add(client);
        }
        State.getClientManager().setClients(clients);

        verifyThat("#tableView", TableViewMatchers.hasNumRows(ROWS_PER_PAGE));
    }
}
