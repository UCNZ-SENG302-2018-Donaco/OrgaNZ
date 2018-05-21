package seng302.Controller.Clinician;


import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.TableViewMatchers.containsRowAtIndex;
import static org.testfx.matcher.control.TableViewMatchers.hasNumRows;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

import java.time.LocalDate;

import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;

import seng302.Client;
import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Gender;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Enums.Gender;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Ignore;
import org.junit.Test;
import org.testfx.api.FxRobotException;
import org.testfx.matcher.control.TextMatchers;

/**
 * Class to test the search clients controller. Used only for the clinician/admin to search and find a particular client.
 */
public class SearchClientsControllerTest extends ControllerTest{

    private Clinician testClinician = new Clinician("Admin", "Da", "Nimda", "2 Two Street", Region.CANTERBURY,
            55, "admin");
    private Client testClient1 = new Client("john", null, "1", LocalDate.of(2017, 1, 1), 1); //One year old
    private Client testClient2 = new Client("jack", null, "2", LocalDate.of(2008, 1, 1), 2); // Ten years old
    private Client testClient3 = new Client("steve", null, "3", LocalDate.of(2008, 1, 1), 3);
    private Client testClient4 = new Client("tom", null, "4", LocalDate.of(1918, 1, 1), 4); // 100 years old
    private Client[] testClients = {testClient1, testClient2, testClient3, testClient4};

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

    private TransplantRequest getRequestLiver1  = new TransplantRequest(testClient1, Organ.LIVER);
    private TransplantRequest getRequestKidney1  = new TransplantRequest(testClient1, Organ.KIDNEY);
    private TransplantRequest getRequestKidney2  = new TransplantRequest(testClient1, Organ.KIDNEY);
    private TransplantRequest getRequestKidney4 = new TransplantRequest(testClient1, Organ.KIDNEY);

    @Override
    protected Page getPage() {
        return Page.SEARCH;
    }

    @Override
    protected void initState() {
        State.init();
        State.login(testClinician);
        setupClientDetails();
        for (Client client: testClients) {
            State.getClientManager().addClient(client);
        }
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
        mainController.setWindowContext(WindowContext.defaultContext());

    }

    /**
     * Method to set up the test details for the test client
     */
    private void setupClientDetails() {
        testClient1.setRegion(Region.AUCKLAND);
        testClient2.setRegion(Region.AUCKLAND);
        testClient3.setRegion(Region.NORTHLAND);
        testClient4.setRegion(Region.WEST_COAST);

        //Set Genders
        testClient1.setGender(Gender.MALE);
        testClient2.setGender(Gender.FEMALE); // Dylans tests
        testClient3.setGender(Gender.FEMALE);
        testClient4.setGender(Gender.FEMALE);

        //add organ requests
        testClient1.addTransplantRequest(getRequestKidney1);
        testClient1.addTransplantRequest(getRequestLiver1);
        testClient2.addTransplantRequest(getRequestKidney2);
        testClient4.addTransplantRequest(getRequestKidney4);

        //add organ donating
        try {
            testClient3.setOrganDonationStatus(Organ.HEART, true);
            testClient3.setOrganDonationStatus(Organ.INTESTINE, true);
        }
        catch (OrganAlreadyRegisteredException e) {
            System.out.println("Organ donating not handled correctly.");
        }
    }

    @Test
    public void filterDefault() {
        verifyThat("#ageMinField", hasText("0"));
        verifyThat("#ageMaxField", hasText("120"));

        // check that all 4 clients are visible by default
        clickOn((Node) lookup("john 1").query());
        clickOn((Node) lookup("jack 2").query());
        clickOn((Node) lookup("steve 3").query());
        clickOn((Node) lookup("tom 4").query());
    }

    /**
     * Test to make sure that the lowest age that can be set is zero.
     */
    @Test
    public void ageFilterUnderMin() {
        doubleClickOn("#ageMinField").type(KeyCode.BACK_SPACE).write("-500").type(KeyCode.ENTER);
        release(KeyCode.ENTER);
        verifyThat("#ageMinField", hasText("0"));
    }

    /**
     * Test to make sure that the maximum age is 120.
     */
    @Test
    public void ageFilterOverMax() {
        doubleClickOn("#ageMaxField").type(KeyCode.BACK_SPACE).write("500").type(KeyCode.ENTER);
        release(KeyCode.ENTER);
        verifyThat("#ageMaxField", hasText("120"));
    }

    /**
     * Checks that the default min and max ages to filter are 0 and 120 respectively.
     */
    @Test
    public void ageFilterDefault() {
        verifyThat("#ageMinField", hasText("0"));
        verifyThat("#ageMaxField", hasText("120"));
    }

    /**
     * Tests for the filter of an age of one year.
     */
    @Test
    public void ageFilterOneYear() {
        doubleClickOn("#ageMaxField").type(KeyCode.BACK_SPACE).write("2").type(KeyCode.ENTER);
        release(KeyCode.ENTER);
        // check 1 value in table
        clickOn((Node) lookup("john 1").query());
    }

    /**
     * Test for the filtering of ten years.
     */
    @Test
    public void ageFilterTenYears() {
        doubleClickOn("#ageMaxField").type(KeyCode.BACK_SPACE).write("12").type(KeyCode.ENTER);
        release(KeyCode.ENTER);
        // check 3 values in table
        clickOn((Node) lookup("john 1").query());
        clickOn((Node) lookup("jack 2").query());
        clickOn((Node) lookup("steve 3").query());
    }

    /**
     * Test to filter between 0 and 100 years.
     */
    @Test
    public void ageFilter100Years() {
        doubleClickOn("#ageMinField").type(KeyCode.BACK_SPACE).write("50").type(KeyCode.ENTER);
        release(KeyCode.ENTER);
        // check 1 value in table
        clickOn((Node) lookup("tom 4").query());
    }

    @Test
    public void genderFilterMale() {
        clickOn("#birthGenderFilter");
        clickOn((Node) lookup(".check-box").nth(0).query());
        clickOn("#birthGenderFilter");
        clickOn((Node) lookup("john 1").query());
    }

    @Test
    public void genderFilterFemale() {
        clickOn("#birthGenderFilter");
        clickOn((Node) lookup(".check-box").nth(1).query());
        clickOn("#birthGenderFilter");
        clickOn((Node) lookup("jack 2").query());
        clickOn((Node) lookup("steve 3").query());
        clickOn((Node) lookup("tom 4").query());
    }


    @Test(expected = NullPointerException.class) // There shouldn't be any of the test data in the results
    public void genderFilterOther() {
        clickOn("#birthGenderFilter");
        clickOn((Node) lookup(".check-box").nth(2).query());
        clickOn("#birthGenderFilter");
        clickOn((Node) lookup("john 1").query());
        clickOn((Node) lookup("jack 2").query());
        clickOn((Node) lookup("steve 3").query());
        clickOn((Node) lookup("tom 4").query());
    }

    /**
     * Tests for filtering one region.
     */
    @Test
    public void regionFilterOneRegion(){
        clickOn("#regionFilter");
        clickOn( (Node) lookup(".check-box").nth(0).query());
        verifyThat("#tableView",containsRowAtIndex(0,
                testClient3.getUid(),
                testClient3.getFullName(),
                testClient3.getAge(),
                testClient3.getGender(),
                testClient3.getRegion()));
        verifyThat("#tableView",hasNumRows(1));
    }

    /**
     * Tests for filtering two regions.
     */
    @Test
    public void regionFilterTwoRegions(){
        clickOn("#regionFilter");
        clickOn( (Node) lookup(".check-box").nth(0).query());
        clickOn( (Node) lookup(".check-box").nth(1).query());
        verifyThat("#tableView",containsRowAtIndex(0,
                testClient1.getUid(),
                testClient1.getFullName(),
                testClient1.getAge(),
                testClient1.getGender(),
                testClient1.getRegion()));
        verifyThat("#tableView",containsRowAtIndex(1,
                testClient2.getUid(),
                testClient2.getFullName(),
                testClient2.getAge(),
                testClient2.getGender(),
                testClient2.getRegion()));
        verifyThat("#tableView",containsRowAtIndex(2,
                testClient3.getUid(),
                testClient3.getFullName(),
                testClient3.getAge(),
                testClient3.getGender(),
                testClient3.getRegion()));
        verifyThat("#tableView",hasNumRows(3));
    }

    /**
     * Tests for filtering a gender and region.
     */
    @Test
    public void GenderAndRegionFilterTest(){
        clickOn("#regionFilter");
        clickOn( (Node) lookup(".check-box").nth(0).query()); //Northland
        clickOn("#birthGenderFilter");
        clickOn( (Node) lookup(".check-box").nth(1).query());
        verifyThat("#tableView",containsRowAtIndex(0,
                testClient3.getUid(),
                testClient3.getFullName(),
                testClient3.getAge(),
                testClient3.getGender(),
                testClient3.getRegion()));
        verifyThat("#tableView",hasNumRows(1));
    }

    /**
     * Tests for the filtering of a gender and a region within the same time.
     */
    @Test
    public void GenderAgeAndRegionFilterTest(){
        clickOn("#regionFilter");
        clickOn( (Node) lookup(".check-box").nth(1).query()); //Auckland
        clickOn("#birthGenderFilter");
        clickOn( (Node) lookup(".check-box").nth(1).query()); //Female
        doubleClickOn("#ageMaxField").type(KeyCode.BACK_SPACE).write("12").type(KeyCode.ENTER);
        verifyThat("#tableView",containsRowAtIndex(0,
                testClient2.getUid(),
                testClient2.getFullName(),
                testClient2.getAge(),
                testClient2.getGender(),
                testClient2.getRegion()));
        verifyThat("#tableView",hasNumRows(1));

    }

    /**
     * Male gender Test
     */
    @Test
    public void MaleGenderFilterTest(){
        clickOn("#birthGenderFilter");
        clickOn( (Node) lookup(".check-box").nth(0).query());
        verifyThat("#tableView",containsRowAtIndex(0,
                testClient1.getUid(),
                testClient1.getFullName(),
                testClient1.getAge(),
                testClient1.getGender(),
                testClient1.getRegion()));
        verifyThat("#tableView",hasNumRows(1));
    }
    /**
     * Tests for the filtering of a female.
     */
    @Test
    public void FemaleGenderFilterTest(){
        clickOn("#birthGenderFilter");
        clickOn( (Node) lookup(".check-box").nth(1).query());
        verifyThat("#tableView",containsRowAtIndex(0,
                testClient2.getUid(),
                testClient2.getFullName(),
                testClient2.getAge(),
                testClient2.getGender(),
                testClient2.getRegion()));
        verifyThat("#tableView",containsRowAtIndex(1,
                testClient3.getUid(),
                testClient3.getFullName(),
                testClient3.getAge(),
                testClient3.getGender(),
                testClient3.getRegion()));
        verifyThat("#tableView",containsRowAtIndex(2,
                testClient4.getUid(),
                testClient4.getFullName(),
                testClient4.getAge(),
                testClient4.getGender(),
                testClient4.getRegion()));
        verifyThat("#tableView",hasNumRows(3));
    }

    /**
     * Tests for the filtering of both male and female genders.
     */
    @Test
    public void MaleAndFemaleGenderFilterTest(){
        clickOn("#birthGenderFilter");
        clickOn( (Node) lookup(".check-box").nth(0).query());
        clickOn( (Node) lookup(".check-box").nth(1).query());
        verifyThat("#tableView",hasNumRows(4));
    }

    /**
     * Tests for the filtering of an "Other" gender.
     */
    @Test
    public void OtherGenderFilter(){
        clickOn("#birthGenderFilter");
        testClient1.setGender(Gender.OTHER);
        testClient2.setGender(Gender.OTHER);
        clickOn( (Node) lookup(".check-box").nth(2).query());
        verifyThat("#tableView",hasNumRows(2));
    }

    /**
     * Tests for the filtering of an unspecified gender.
     */
    @Test
    public void UnspecifiedGenderFilterTest(){
        clickOn("#birthGenderFilter");
        testClient1.setGender(Gender.UNSPECIFIED);
        testClient2.setGender(Gender.UNSPECIFIED);
        clickOn( (Node) lookup(".check-box").nth(3).query());
        verifyThat("#tableView",hasNumRows(2));
    }

    /**
     * Tests for the requesting organs are filtered correctly with one organ to filter selected.
     */
    @Test
    public void requestOrganFilterOne() {
        clickOn("#organsRequestingFilter");
        clickOn("#organsRequestingFilter");
        clickOn("#organsRequestingFilter");
        clickOn((Node) lookup(".check-box").nth(1).query());
        verifyThat("#tableView", containsRowAtIndex(0,
                getRequestKidney1.getClient().getUid(),
                getRequestKidney1.getClient().getFullName(),
                testClient1.getAge(),
                testClient1.getGender(),
                getRequestKidney1.getClient().getRegion()));
        verifyThat("#tableView", hasNumRows(3));
    }

    /**
     * Tests for the requesting organs are filtered correctly with >1 organ filtered.
     */
    @Test
    public void requestOrganFilterMultiple() {
        clickOn("#organsRequestingFilter");
        clickOn("#organsRequestingFilter");
        clickOn("#organsRequestingFilter");
        clickOn((Node) lookup(".check-box").nth(1).query());
        clickOn((Node) lookup(".check-box").nth(3).query());
        verifyThat("#tableView", containsRowAtIndex(0,
                getRequestKidney1.getClient().getUid(),
                getRequestLiver1.getClient().getFullName(),
                testClient1.getAge(),
                testClient1.getGender(),
                getRequestKidney1.getClient().getRegion()));
        verifyThat("#tableView", hasNumRows(3));

    }

    /**
     * Test for a requesting organ and a specific gender
     */
    @Test
    public void filterRequestOrgansAndGender() {
        clickOn("#birthGenderFilter");
        clickOn( (Node) lookup(".check-box").nth(0).query());
        clickOn("#organsRequestingFilter");
        clickOn("#organsRequestingFilter");
        clickOn("#organsRequestingFilter");
        clickOn((Node) lookup(".check-box").nth(1).query());
        verifyThat("#tableView", containsRowAtIndex(0,
                testClient1.getUid(),
                testClient1.getFullName(),
                testClient1.getAge(),
                testClient1.getGender(),
                testClient1.getRegion()));
        verifyThat("#tableView", hasNumRows(1));
    }

    /**
     * Tests for a requesting organ and a specific age filter
     */
    @Test
    public void filterRequestOrgansAndAge() {
        doubleClickOn("#ageMaxField").type(KeyCode.BACK_SPACE).write("12").type(KeyCode.ENTER);
        clickOn("#organsRequestingFilter");
        clickOn("#organsRequestingFilter");
        clickOn("#organsRequestingFilter");
        clickOn((Node) lookup(".check-box").nth(1).query());
        verifyThat("#tableView", containsRowAtIndex(1,
                testClient2.getUid(),
                testClient2.getFullName(),
                testClient2.getAge(),
                testClient2.getGender(),
                testClient2.getRegion()));
        verifyThat("#tableView", hasNumRows(2));
    }

    /**
     * Tests that the client type is filtered correctly
     */
    @Test
    public void filterClientType() {
        clickOn("#clientTypeFilter");
        clickOn("Only Receiver");
        verifyThat("#tableView", containsRowAtIndex(1,
                testClient2.getUid(),
                testClient2.getFullName(),
                testClient2.getAge(),
                testClient2.getGender(),
                testClient2.getRegion()));
        verifyThat("#tableView", hasNumRows(3));
    }

    /**
     * Tests for the organ donating filter
     */
    @Test
    public void donateOrganFilterOne() {
        clickOn("#organsDonatingFilter");
        clickOn((Node) lookup(".check-box").nth(5).query());
        verifyThat("#tableView", containsRowAtIndex(0,
                testClient3.getUid(),
                testClient3.getFullName(),
                testClient3.getAge(),
                testClient3.getGender(),
                testClient3.getRegion()));
        verifyThat("#tableView", hasNumRows(1));
    }

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
        verifyThat("#displayingXToYOfZText", TextMatchers.hasText("Displaying 1-30 of " + Integer.toString(totalRows)));
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
