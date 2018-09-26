package com.humanharvest.organz.controller.clinician;

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

import org.junit.Test;
import org.testfx.matcher.control.TableViewMatchers;

public class OrgansToDonateControllerTest extends ControllerTest {

    private final Administrator testAdmin = new Administrator("username", "password");

    private final Client client1 = new Client(1);
    private final Client client2 = new Client(2);
    private final Client client3 = new Client(3);
    private final Collection<Client> clients = new ArrayList<>();

    private int numberOfOrgansBeingDonated;

    @Override
    protected Page getPage() {
        return Page.ORGANS_TO_DONATE;
    }

    @Override
    protected void initState() throws OrganAlreadyRegisteredException {
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
        client1.setOrganDonationStatus(Organ.LIVER, true);
        client2.setOrganDonationStatus(Organ.LIVER, true);
        client3.setOrganDonationStatus(Organ.LIVER, true);
        client1.setOrganDonationStatus(Organ.LUNG, true);
        numberOfOrgansBeingDonated = 4;

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
}
