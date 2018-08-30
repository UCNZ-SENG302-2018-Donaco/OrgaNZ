package com.humanharvest.organz.controller.clinician;

import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isNull;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.TableViewMatchers.containsRowAtIndex;
import static org.testfx.matcher.control.TableViewMatchers.hasNumRows;
import static org.testfx.matcher.control.TableViewMatchers.hasTableCell;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

import java.time.LocalDate;

import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.exceptions.OrganAlreadyRegisteredException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;

import org.junit.Ignore;
import org.junit.Test;
import org.testfx.matcher.control.TextMatchers;

/**
 * Class to test the search clients controller.
 * Used only for the clinician/admin to search and find a particular client.
 */
@Ignore
public class SearchClientsControllerTest extends ControllerTest {

    private final Clinician testClinician = new Clinician("Admin", "Da", "Nimda", "2 Two Street",
            Region.CANTERBURY.toString(),
            Country.NZ,
            55, "admin");
    private final Client testClient1 = new Client(
            "tom", "Delta", "1", LocalDate.now().minusYears(100), 1); // 100 years old
    private final Client testClient2 = new Client(
            "bobby", "Charlie", "2", LocalDate.now().minusYears(11), 2); // 11 years old
    private final Client testClient3 = new Client(
            "john", "Alpha", "2", LocalDate.now().minusYears(10), 3); // 10 years old
    private final Client testClient4 = new Client(
            "john", "Beta", "2", LocalDate.now().minusYears(1), 4); // 1 year old

    private final Client[] testClients = {testClient1, testClient2, testClient3, testClient4};

    private final TransplantRequest getRequestLiver1 = new TransplantRequest(testClient1, Organ.LIVER);
    private final TransplantRequest getRequestKidney1 = new TransplantRequest(testClient1, Organ.KIDNEY);
    private final TransplantRequest getRequestKidney2 = new TransplantRequest(testClient1, Organ.KIDNEY);
    private final TransplantRequest getRequestKidney4 = new TransplantRequest(testClient1, Organ.KIDNEY);

    @Override
    protected Page getPage() {
        return Page.SEARCH;
    }

    @Override
    protected void initState() {
        State.reset();
        State.login(testClinician);
        setupClientDetails();
        for (Client client : testClients) {
            State.getClientManager().addClient(client);
        }

        testClient1.setRegion(Region.CANTERBURY.toString());
        testClient2.setRegion(Region.AUCKLAND.toString());
        // client3's region is left as null

        mainController.setWindowContext(WindowContext.defaultContext());
    }

    /**
     * Method to set up the test details for the test client
     */
    private void setupClientDetails() {
        testClient1.setRegion(Region.AUCKLAND.toString());
        testClient2.setRegion(Region.AUCKLAND.toString());
        testClient3.setRegion(Region.NORTHLAND.toString());
        testClient4.setRegion(Region.WEST_COAST.toString());

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
        } catch (OrganAlreadyRegisteredException e) {
            throw new RuntimeException(e);
        }
    }

    private void expandFilterPane() {
        clickOn(lookup("Filters").queryAs(TitledPane.class));
        sleep(500);
    }

    @Test
    public void filterDefault() {
        expandFilterPane();
        verifyThat("#ageMinField", hasText("0"));
        verifyThat("#ageMaxField", hasText("120"));

        // check that all 4 clients are visible by default
        clickOn((Node) lookup(testClient1.getFullName()).query());
        clickOn((Node) lookup(testClient2.getFullName()).query());
        clickOn((Node) lookup(testClient3.getFullName()).query());
        clickOn((Node) lookup(testClient4.getFullName()).query());
    }

    /**
     * Test to make sure that the lowest age that can be set is zero.
     */
    @Test
    public void ageFilterUnderMin() {
        expandFilterPane();
        doubleClickOn("#ageMinField").type(KeyCode.BACK_SPACE).write("-500").type(KeyCode.ENTER);
        release(KeyCode.ENTER);
        verifyThat("#ageMinField", hasText("0"));
    }

    /**
     * Test to make sure that the maximum age is 120.
     */
    @Test
    public void ageFilterOverMax() {
        expandFilterPane();
        doubleClickOn("#ageMaxField").type(KeyCode.BACK_SPACE).write("500").type(KeyCode.ENTER);
        release(KeyCode.ENTER);
        verifyThat("#ageMaxField", hasText("120"));
    }

    /**
     * Checks that the default min and max ages to filter are 0 and 120 respectively.
     */
    @Test
    public void ageFilterDefault() {
        expandFilterPane();
        verifyThat("#ageMinField", hasText("0"));
        verifyThat("#ageMaxField", hasText("120"));
    }

    /**
     * Tests for the filter of an age of one year.
     */
    @Test
    public void ageFilterOneYear() {
        expandFilterPane();
        doubleClickOn("#ageMaxField").type(KeyCode.BACK_SPACE).write("2").type(KeyCode.ENTER);
        release(KeyCode.ENTER);
        // check 1 value in table
        assertNotNull(lookup(testClient4.getFullName()).query());
    }

    /**
     * Test for the filtering of ten years.
     */
    @Test
    public void ageFilterTenAndElevenYears() {
        expandFilterPane();
        doubleClickOn("#ageMinField").type(KeyCode.BACK_SPACE).write("9").type(KeyCode.ENTER);
        doubleClickOn("#ageMaxField").type(KeyCode.BACK_SPACE).write("12").type(KeyCode.ENTER);
        release(KeyCode.ENTER);
        // check 2 values in table
        assertNotNull(lookup(testClient2.getFullName()).query());
        assertNotNull(lookup(testClient3.getFullName()).query());
    }

    /**
     * Test to filter between 0 and 100 years.
     */
    @Test
    public void ageFilter100Years() {
        expandFilterPane();
        doubleClickOn("#ageMinField").type(KeyCode.BACK_SPACE).write("50").type(KeyCode.ENTER);
        release(KeyCode.ENTER);
        // check 1 value in table
        assertNotNull(lookup(testClient1.getFullName()).query());
    }

    @Test
    public void genderFilterMale() {
        expandFilterPane();
        clickOn("#birthGenderFilter");
        clickOn((Node) lookup(".check-box").nth(0).query());
        clickOn("#birthGenderFilter");
        assertNotNull(lookup(testClient1.getFullName()).query());
    }

    @Test
    public void genderFilterFemale() {
        expandFilterPane();
        clickOn("#birthGenderFilter");
        clickOn((Node) lookup(".check-box").nth(1).query());
        clickOn("#birthGenderFilter");
        assertNotNull(lookup(testClient2.getFullName()).query());
        assertNotNull(lookup(testClient3.getFullName()).query());
        assertNotNull(lookup(testClient4.getFullName()).query());
    }

    @Test // There shouldn't be any of the test data in the results
    public void genderFilterOther() {
        expandFilterPane();
        clickOn("#birthGenderFilter");
        clickOn((Node) lookup(".check-box").nth(2).query());
        clickOn("#birthGenderFilter");
        assertNull(lookup(testClient1.getFullName()).query());
        assertNull(lookup(testClient2.getFullName()).query());
        assertNull(lookup(testClient3.getFullName()).query());
        assertNull(lookup(testClient4.getFullName()).query());
    }

    /**
     * Tests for filtering one region.
     */
    @Test
    public void regionFilterOneRegion() {
        expandFilterPane();
        clickOn("#regionFilter");
        clickOn((Node) lookup(".check-box").nth(0).query());
        verifyThat("#tableView", containsRowAtIndex(0,
                testClient3.getUid(),
                testClient3,
                testClient3.getAge(),
                testClient3.getGender(),
                testClient3.getRegion(),
                testClient3.isDonor(),
                testClient3.isReceiver()));
        verifyThat("#tableView", hasNumRows(1));
    }

    /**
     * Tests for filtering two regions.
     */
    @Test
    public void regionFilterTwoRegions() {
        expandFilterPane();
        clickOn("#regionFilter");
        clickOn((Node) lookup(".check-box").nth(0).query());
        clickOn((Node) lookup(".check-box").nth(1).query());
        verifyThat("#tableView", containsRowAtIndex(0,
                testClient2.getUid(),
                testClient2,
                testClient2.getAge(),
                testClient2.getGender(),
                testClient2.getRegion(),
                testClient2.isDonor(),
                testClient2.isReceiver()));
        verifyThat("#tableView", containsRowAtIndex(1,
                testClient3.getUid(),
                testClient3,
                testClient3.getAge(),
                testClient3.getGender(),
                testClient3.getRegion(),
                testClient3.isDonor(),
                testClient3.isReceiver()));
        verifyThat("#tableView", hasNumRows(2));
    }

    /**
     * Tests for filtering a gender and region.
     */
    @Test
    public void GenderAndRegionFilterTest() {
        expandFilterPane();
        clickOn("#regionFilter");
        clickOn((Node) lookup(".check-box").nth(0).query()); //Northland
        clickOn("#birthGenderFilter");
        clickOn((Node) lookup(".check-box").nth(1).query());
        verifyThat("#tableView", containsRowAtIndex(0,
                testClient3.getUid(),
                testClient3,
                testClient3.getAge(),
                testClient3.getGender(),
                testClient3.getRegion(),
                testClient3.isDonor(),
                testClient3.isReceiver()));
        verifyThat("#tableView", hasNumRows(1));
    }

    /**
     * Tests for the filtering of a gender and a region within the same time.
     */
    @Test
    public void GenderAgeAndRegionFilterTest() {
        expandFilterPane();
        clickOn("#regionFilter");
        clickOn((Node) lookup(".check-box").nth(1).query()); //Auckland
        clickOn("#birthGenderFilter");
        clickOn((Node) lookup(".check-box").nth(1).query()); //Female
        doubleClickOn("#ageMaxField").type(KeyCode.BACK_SPACE).write("12").type(KeyCode.ENTER);
        verifyThat("#tableView", containsRowAtIndex(0,
                testClient2.getUid(),
                testClient2,
                testClient2.getAge(),
                testClient2.getGender(),
                testClient2.getRegion(),
                testClient2.isDonor(),
                testClient2.isReceiver()));
        verifyThat("#tableView", hasNumRows(1));

    }

    /**
     * Male gender Test
     */
    @Test
    public void MaleGenderFilterTest() {
        expandFilterPane();
        clickOn("#birthGenderFilter");
        clickOn((Node) lookup(".check-box").nth(0).query());
        verifyThat("#tableView", containsRowAtIndex(0,
                testClient1.getUid(),
                testClient1,
                testClient1.getAge(),
                testClient1.getGender(),
                testClient1.getRegion(),
                testClient1.isDonor(),
                testClient1.isReceiver()));
        verifyThat("#tableView", hasNumRows(1));
    }

    /**
     * Tests for the filtering of a female.
     */
    @Test
    public void FemaleGenderFilterTest() {
        expandFilterPane();
        clickOn("#birthGenderFilter");
        clickOn((Node) lookup(".check-box").nth(1).query());
        verifyThat("#tableView", containsRowAtIndex(0,
                testClient2.getUid(),
                testClient2,
                testClient2.getAge(),
                testClient2.getGender(),
                testClient2.getRegion(),
                testClient2.isDonor(),
                testClient2.isReceiver()));
        verifyThat("#tableView", containsRowAtIndex(1,
                testClient3.getUid(),
                testClient3,
                testClient3.getAge(),
                testClient3.getGender(),
                testClient3.getRegion(),
                testClient3.isDonor(),
                testClient3.isReceiver()));
        verifyThat("#tableView", containsRowAtIndex(2,
                testClient4.getUid(),
                testClient4,
                testClient4.getAge(),
                testClient4.getGender(),
                testClient4.getRegion(),
                testClient4.isDonor(),
                testClient4.isReceiver()));
        verifyThat("#tableView", hasNumRows(3));
    }

    /**
     * Tests for the filtering of both male and female genders.
     */
    @Test
    public void MaleAndFemaleGenderFilterTest() {
        expandFilterPane();
        clickOn("#birthGenderFilter");
        clickOn((Node) lookup(".check-box").nth(0).query());
        clickOn((Node) lookup(".check-box").nth(1).query());
        verifyThat("#tableView", hasNumRows(4));
    }

    /**
     * Tests for the filtering of an "Other" gender.
     */
    @Test
    public void OtherGenderFilter() {
        expandFilterPane();
        clickOn("#birthGenderFilter");
        testClient1.setGender(Gender.OTHER);
        testClient2.setGender(Gender.OTHER);
        clickOn((Node) lookup(".check-box").nth(2).query());
        verifyThat("#tableView", hasNumRows(2));
    }

    /**
     * Tests for the filtering of an unspecified gender.
     */
    @Test
    public void UnspecifiedGenderFilterTest() {
        expandFilterPane();
        clickOn("#birthGenderFilter");
        testClient1.setGender(Gender.UNSPECIFIED);
        testClient2.setGender(Gender.UNSPECIFIED);
        clickOn((Node) lookup(".check-box").nth(3).query());
        verifyThat("#tableView", hasNumRows(2));
    }

    /**
     * Tests for the requesting organs are filtered correctly with one organ to filter selected.
     */
    @Test
    public void requestOrganFilterOne() {
        expandFilterPane();
        clickOn("#organsRequestingFilter");
        clickOn("#organsRequestingFilter");
        clickOn("#organsRequestingFilter");
        clickOn((Node) lookup(".check-box").nth(1).query());
        verifyThat("#tableView", containsRowAtIndex(0,
                getRequestKidney1.getClient().getUid(),
                getRequestKidney1.getClient(),
                testClient1.getAge(),
                testClient1.getGender(),
                getRequestKidney1.getClient().getRegion(),
                testClient1.isDonor(),
                testClient1.isReceiver()));
        verifyThat("#tableView", hasNumRows(3));
    }

    /**
     * Tests for the requesting organs are filtered correctly with more than 1 organ filtered.
     */
    @Test
    public void requestOrganFilterMultiple() {
        expandFilterPane();
        clickOn("#organsRequestingFilter");
        clickOn("#organsRequestingFilter");
        clickOn("#organsRequestingFilter");
        clickOn((Node) lookup(".check-box").nth(1).query());
        clickOn((Node) lookup(".check-box").nth(3).query());
        verifyThat("#tableView", containsRowAtIndex(0,
                getRequestKidney1.getClient().getUid(),
                getRequestLiver1.getClient(),
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
        expandFilterPane();
        clickOn("#birthGenderFilter");
        clickOn((Node) lookup(".check-box").nth(0).query());
        clickOn("#organsRequestingFilter");
        clickOn("#organsRequestingFilter");
        clickOn("#organsRequestingFilter");
        clickOn((Node) lookup(".check-box").nth(1).query());
        verifyThat("#tableView", containsRowAtIndex(0,
                testClient1.getUid(),
                testClient1,
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
        expandFilterPane();
        doubleClickOn("#ageMaxField").type(KeyCode.BACK_SPACE).write("12").type(KeyCode.ENTER);
        clickOn("#organsRequestingFilter");
        clickOn((Node) lookup(".check-box").nth(1).query());
        verifyThat("#tableView", containsRowAtIndex(0,
                testClient2.getUid(),
                testClient2,
                testClient2.getAge(),
                testClient2.getGender(),
                testClient2.getRegion(),
                testClient2.isDonor(),
                testClient2.isReceiver()));
        verifyThat("#tableView", hasNumRows(2));
    }

    /**
     * Tests that the client type is filtered correctly
     */
    @Test
    public void filterClientType() {
        expandFilterPane();
        clickOn("#clientTypeFilter");
        clickOn("Only Receiver");
        verifyThat("#tableView", containsRowAtIndex(1,
                testClient2.getUid(),
                testClient2,
                testClient2.getAge(),
                testClient2.getGender(),
                testClient2.getRegion(),
                testClient2.isDonor(),
                testClient2.isReceiver()));
        verifyThat("#tableView", hasNumRows(3));
    }

    /**
     * Tests for the organ donating filter
     */
    @Test
    public void donateOrganFilterOne() {
        expandFilterPane();
        clickOn("#organsDonatingFilter");
        clickOn((Node) lookup(".check-box").nth(5).query());
        verifyThat("#tableView", containsRowAtIndex(0,
                testClient3.getUid(),
                testClient3,
                testClient3.getAge(),
                testClient3.getGender(),
                testClient3.getRegion(),
                testClient3.isDonor(),
                testClient3.isReceiver()));
        verifyThat("#tableView", hasNumRows(1));
    }

    @Test
    public void testUnableToDeleteClient() {
        String clientName = testClient4.getFullName();

        //check the client is in the table
        verifyThat("#tableView", hasTableCell(clientName));

        rightClickOn(clientName);

        // check that Delete is not an option
        verifyThat("Delete", isNull());
    }

    @Test
    public void componentsAreVisibleTest() {
        verifyThat("#tableView", isVisible());
        verifyThat("#displayingXToYOfZText", isVisible());
        verifyThat("#menuBarPane", isVisible());
        verifyThat("#pagination", isVisible());
    }

    private void createManyClients() {
        for (int i = 100; i < 218; i++) {
            Client client = new Client("Client", "Number", "num" + i, LocalDate.now(), i);
            TransplantRequest request = new TransplantRequest(client, Organ.MIDDLE_EAR);
            client.addTransplantRequest(request);
            client.setRegion(Region.NELSON.toString());
            State.getClientManager().addClient(client);
        }
        pageController.refresh();
    }

    @Test
    public void paginationDescriptionTest() {
        createManyClients();
        verifyThat("#tableView", hasNumRows(30));
        int totalRows = testClients.length + 118;
        verifyThat("#displayingXToYOfZText", TextMatchers.hasText("Displaying 1-30 of " + totalRows));
    }

    @Test
    public void clientIsReceiverTest() {
        TransplantRequest transplantRequest = new TransplantRequest(testClient1, Organ.MIDDLE_EAR);
        testClient1.addTransplantRequest(transplantRequest);

        TableView<Client> tableView = lookup("#tableView").query();
        boolean isReceiver = (Boolean) tableView.getColumns().get(6).getCellObservableValue(testClient1).getValue();
        assertTrue(isReceiver);
    }

    @Test
    public void clientIsDonorTest() throws OrganAlreadyRegisteredException {
        testClient2.setOrganDonationStatus(Organ.PANCREAS, true);

        TableView<Client> tableView = lookup("#tableView").query();
        boolean isDonor = (Boolean) tableView.getColumns().get(5).getCellObservableValue(testClient3).getValue();
        assertTrue(isDonor);
    }

    @Test
    public void clientNotDonorOrReceiverTest() {
        TableView<Client> tableView = lookup("#tableView").query();
        boolean isDonor = (Boolean) tableView.getColumns().get(5).getCellObservableValue(testClient1).getValue();
        boolean isReceiver = (Boolean) tableView.getColumns().get(6).getCellObservableValue(testClient1).getValue();

        assertFalse(isDonor);
        assertTrue(isReceiver);
    }

    @Test
    public void testLastNameIsFirstPriority() {
        TableView<Client> tableView = lookup("#tableView").query();

        Client result = tableView.getItems().get(0);

        assertEquals(result, testClient1);
    }

    @Test
    public void testFirstNameIsSecondPriority() {
        TableView<Client> tableView = lookup("#tableView").query();

        Client result = tableView.getItems().get(1);

        assertEquals(result, testClient2);
    }

    @Test
    public void testMiddleNameIsThirdPriority() {
        TableView<Client> tableView = lookup("#tableView").query();
        Client result = tableView.getItems().get(2);

        assertEquals(result, testClient3);
    }

    @Test
    public void testNameColReverseOrder() {
        clickOn("#nameCol");
        TableView<Client> tableView = lookup("#tableView").query();
        Client result0 = tableView.getItems().get(0);
        Client result1 = tableView.getItems().get(1);
        Client result2 = tableView.getItems().get(2);

        assertEquals(testClient4.getFullName(), result0.getFullName());
        assertEquals(testClient3.getFullName(), result1.getFullName());
        assertEquals(testClient2.getFullName(), result2.getFullName());
    }

    @Test
    public void testNameColReverseOrderLastPage() {
        clickOn("#nameCol");

        TableView<Client> tableView = lookup("#tableView").query();

        clickOn((Node) lookup("5").query()); // Click on the last page
        Client result = tableView.getItems().get(6);
        assertEquals("Zeta Zeta Alpha", result.getFullName());
    }

    // Tests to ensure the custom comparator hasn't broken the other column default comps.

    @Test
    public void testIDOrderStillWorks() {
        clickOn("#idCol");
        TableView<Client> tableView = lookup("#tableView").query();
        Client result = tableView.getItems().get(0);
        assertEquals(new Integer(1), result.getUid());
    }

    @Test
    public void testGenderOrderStillWorks() {
        testClient1.setGender(Gender.MALE);
        clickOn("#genderCol");
        TableView<Client> tableView = lookup("#tableView").query();
        Client result = tableView.getItems().get(0);
        assertEquals(Gender.MALE, result.getGender());
    }

    @Test
    public void testRegionOrderStillWorks() {
        doubleClickOn("#regionCol");
        TableView<Client> tableView = lookup("#tableView").query();
        Client result = tableView.getItems().get(0);

        assertEquals(Region.CANTERBURY.toString(), result.getRegion());
    }
}
