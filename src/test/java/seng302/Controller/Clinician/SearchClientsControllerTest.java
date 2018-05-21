package seng302.Controller.Clinician;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.TableViewMatchers.hasNumRows;
import static org.testfx.matcher.control.TableViewMatchers.hasTableCell;
import static org.testfx.matcher.control.TextMatchers.hasText;

import java.time.LocalDate;

import javafx.scene.Node;
import javafx.scene.control.TableView;

import seng302.Client;
import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Gender;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class SearchClientsControllerTest extends ControllerTest {


    // Test data

    private Clinician testClinician = new Clinician("A", "B", "C", "D",
            Region.UNSPECIFIED, 0, "E");

    private Client client1 = new Client("Client", "Number", "One", LocalDate.now(), 1);
    private Client client2 = new Client("Client", "Number", "Two", LocalDate.now(), 2);
    private Client client3 = new Client("Client", "Number", "Three", LocalDate.now(), 3);
    private Client client4 = new Client("Zeta", "Zeta", "Alpha", LocalDate.now(), 4);
    private Client client5 = new Client("Alpha", "Zeta", "Beta", LocalDate.now(), 5);
    private Client client6 = new Client("Zeta", "Alpha", "Beta", LocalDate.now(), 6);
    private Client client7 = new Client("Alpha", "Beta", "Charlie", LocalDate.now(), 7);
    private Client client8 = new Client("Alpha", "Alpha", "Charlie", LocalDate.now(), 8);

    private Client[] clients = {client1, client2, client3, client4, client5, client6, client7, client8};

    private String tick = "\u2713";


    // Overridden classes from parent class

    @Override
    protected Page getPage() {
        return Page.SEARCH;
    }

    @Override
    protected void initState() {
        State.init();
        State.login(testClinician);

        for (Client client : clients) {
            State.getClientManager().addClient(client);
        }

        client1.setRegion(Region.CANTERBURY);
        client2.setRegion(Region.AUCKLAND);
        // client3's region is left as null

        for (int i = 100; i < 218; i++) {
            Client client = new Client("Client", "Number", "num" + Integer.toString(i), LocalDate.now(), i);
            TransplantRequest request = new TransplantRequest(client, Organ.MIDDLE_EAR);
            client.addTransplantRequest(request);
            client.setRegion(Region.NELSON);
            State.getClientManager().addClient(client);
        }

        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .build());
    }

    // Tests

    @Test
    public void componentsAreVisibleTest() {
        verifyThat("#tableView", isVisible());
        verifyThat("#displayingXToYOfZText", isVisible());
        verifyThat("#sidebarPane", isVisible());
        verifyThat("#pagination", isVisible());
    }

    @Ignore
    public void paginationDescriptionTest() {
        verifyThat("#tableView", hasNumRows(30));
        int totalRows = clients.length + 118;
        verifyThat("#displayingXToYOfZText", hasText("Displaying 1-30 of " + Integer.toString(totalRows)));
    }

    @Ignore
    public void clientIsReceiverTest() {
        TransplantRequest transplantRequest = new TransplantRequest(client1, Organ.MIDDLE_EAR);
        client1.addTransplantRequest(transplantRequest);

        TableView<Client> tableView = lookup("#tableView").query();
        boolean isReceiver = (Boolean) (tableView.getColumns().get(5).getCellObservableValue(client1).getValue());
        assertTrue(isReceiver);
    }

    @Ignore
    public void clientIsDonorTest() throws OrganAlreadyRegisteredException {
        client1.setOrganDonationStatus(Organ.PANCREAS, true);

        TableView<Client> tableView = lookup("#tableView").query();
        boolean isDonor = (Boolean) (tableView.getColumns().get(4).getCellObservableValue(client1).getValue());
        assertTrue(isDonor);
    }

    @Ignore
    public void clientNotDonorOrReceiverTest() {
        TableView<Client> tableView = lookup("#tableView").query();
        boolean isDonor = (Boolean) (tableView.getColumns().get(4).getCellObservableValue(client1).getValue());
        boolean isReceiver = (Boolean) (tableView.getColumns().get(5).getCellObservableValue(client1).getValue());

        assertFalse(isDonor);
        assertFalse(isReceiver);
    }

    @Ignore
    public void testLastNameIsFirstPriority() {
        TableView<Client> tableView = lookup("#tableView").query();

        Client result = tableView.getItems().get(0);

        assertEquals(result.getFullName(), client4.getFullName());
    }

    @Ignore
    public void testFirstNameIsSecondPriority() {
        TableView<Client> tableView = lookup("#tableView").query();

        Client result = tableView.getItems().get(1);

        assertEquals(result.getFullName(), client5.getFullName());
    }

    @Ignore
    public void testMiddleNameIsThirdPriority() {
        TableView<Client> tableView = lookup("#tableView").query();

        Client result = tableView.getItems().get(3);

        assertEquals(result.getFullName(), client7.getFullName());
    }

    @Ignore
    public void testNameColReverseOrder() {
        clickOn("#nameCol");
        TableView<Client> tableView = lookup("#tableView").query();
        Client result0 = tableView.getItems().get(0);
        Client result1 = tableView.getItems().get(1);
        Client result2 = tableView.getItems().get(2);

        assertEquals(result0.getFullName(), "Client Number num217");
        assertEquals(result1.getFullName(), "Client Number num216");
        assertEquals(result2.getFullName(), "Client Number num215");
    }

    @Ignore
    public void testNameColReverseOrderLastPage() {
        clickOn("#nameCol");

        TableView<Client> tableView = lookup("#tableView").query();

        clickOn((Node) lookup("5").query()); // Click on the last page
        Client result = tableView.getItems().get(5);
        assertEquals(result.getFullName(), "Zeta Zeta Alpha");
    }

    // Tests to ensure the custom comparator hasn't broken the other column default comps.

    @Ignore
    public void testIDOrderStillWorks() {
        clickOn("#idCol");
        TableView<Client> tableView = lookup("#tableView").query();
        Client result = tableView.getItems().get(0);
        assertEquals(result.getUid(), 1);
    }

    @Ignore
    public void testGenderOrderStillWorks() {
        client1.setGender(Gender.MALE);
        clickOn("#genderCol");
        TableView<Client> tableView = lookup("#tableView").query();
        Client result = tableView.getItems().get(0);
        assertEquals(result.getGender(), Gender.MALE);
    }

    @Ignore
    public void testRegionOrderStillWorks() {
        doubleClickOn("#regionCol");
        TableView<Client> tableView = lookup("#tableView").query();
        Client result = tableView.getItems().get(0);

        assertEquals(result.getRegion(), Region.CANTERBURY);
    }

}
