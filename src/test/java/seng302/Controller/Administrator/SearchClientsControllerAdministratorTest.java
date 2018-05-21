package seng302.Controller.Administrator;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.hasNumRows;
import static org.testfx.matcher.control.TableViewMatchers.hasTableCell;

import java.time.LocalDate;

import seng302.Administrator;
import seng302.Client;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Ignore;
import org.junit.Test;

public class SearchClientsControllerAdministratorTest extends ControllerTest {


    // Test data

    private Administrator testAdmin = new Administrator("u", "p");

    private Client client1 = new Client("Client", "Number", "One", LocalDate.now(), 1);
    private Client client2 = new Client("Client", "Number", "Two", LocalDate.now(), 2);
    private Client client3 = new Client("Client", "Number", "Three", LocalDate.now(), 3);

    private Client[] clients = {client1, client2, client3};

    private String tick = "\u2713";


    // Overridden classes from parent class

    @Override
    protected Page getPage() {
        return Page.SEARCH;
    }

    @Override
    protected void initState() {
        State.init();
        State.login(testAdmin);

        for (Client client : clients) {
            State.getClientManager().addClient(client);
        }

        client1.setRegion(Region.CANTERBURY);
        client2.setRegion(Region.AUCKLAND);
        // client3's region is left as null
/*
        for (int i = 100; i < 218; i++) {
            Client client = new Client("Client", "Number", Integer.toString(i), LocalDate.now(), i);
            TransplantRequest request = new TransplantRequest(client, Organ.MIDDLE_EAR);
            client.addTransplantRequest(request);
            client.setRegion(Region.NELSON);
            State.getClientManager().addClient(client);
        }*/

        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .build());
    }

    // Tests

    /**
     * This test passes in headless mode but not in headful mode.
     * See https://github.com/TestFX/Monocle/issues/12
     */
    @Ignore
    @Test
    public void testDeleteClient() {
        Client client = client1;
        String clientName = client.getFullName();

        //check the administrator is in the list
        verifyThat("#tableView", hasTableCell(clientName));

        rightClickOn(clientName);
        clickOn("Delete");

        try {
            verifyThat("#tableView", hasTableCell(clientName));
            fail("Still in client list");
        } catch (AssertionError e) {
            // check it has been deleted from the client manager
            assertNull(State.getClientManager().getClientByID(client.getUid()));
            // check it still has 30 rows
            verifyThat("#tableView", hasNumRows(2)); //just clients 2 and 3 left
        }
    }
}