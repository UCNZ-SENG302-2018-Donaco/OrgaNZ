package seng302.Controller.Clinician;


import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.containsRowAtIndex;
import static org.testfx.matcher.control.TableViewMatchers.hasNumRows;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

import java.time.LocalDate;

import javafx.scene.Node;
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
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Test;
import org.testfx.api.FxRobotException;

/**
 * Class to test the search clients controller. Used only for the clinician/admin to search and find a particular client.
 */
public class SearchClientsControllerTest extends ControllerTest{

    private Clinician testClinician = new Clinician("Admin", "Da", "Nimda", "2 Two Street", Region.CANTERBURY,
            55, "admin");
    private Client testClient1 = new Client("john", null, "1", LocalDate.now().minusDays(365), 1); //One year old
    private Client testClient2 = new Client("jack", null, "2", LocalDate.now().minusDays(3650), 2); // Ten years old
    private Client testClient3 = new Client("steve", null, "3", LocalDate.now().minusDays(3650), 3);
    private Client testClient4 = new Client("tom", null, "4", LocalDate.now().minusDays(36500), 4); // 100 years old
    private Client[] testClients = {testClient1, testClient2, testClient3, testClient4};

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
}