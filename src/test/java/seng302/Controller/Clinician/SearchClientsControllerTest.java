package seng302.Controller.Clinician;


import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

import java.time.LocalDate;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;

import seng302.Client;
import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Test;

public class SearchClientsControllerTest extends ControllerTest{

    private Clinician testClinician = new Clinician("Admin", "Da", "Nimda", "2 Two Street", Region.CANTERBURY,
            55, "admin");
    private Client testClient1 = new Client("john", null, "1", LocalDate.now().minusDays(365), 1); //One year old
    private Client testClient2 = new Client("jack", null, "2", LocalDate.now().minusDays(3650), 2); // Ten years old
    private Client testClient3 = new Client("steve", null, "3", LocalDate.now().minusDays(3650), 3);
    private Client testClient4 = new Client("tom", null, "4", LocalDate.now().minusDays(36500), 4); // 100 years old
    private Client[] testClients = {testClient1, testClient2, testClient3, testClient4};

    @Override
    protected Page getPage() {
        return Page.SEARCH;
    }

    @Override
    protected void initState() {
        State.init();
        State.login(testClinician);
        //WindowContext.defaultContext();
        setupClientDetails();
        for (Client client: testClients) {
            State.getClientManager().addClient(client);
        }
        mainController.setWindowContext(WindowContext.defaultContext());

    }

    private void setupClientDetails() {
        testClient1.setRegion(Region.AUCKLAND);
        testClient2.setRegion(Region.AUCKLAND);
        testClient3.setRegion(Region.NORTHLAND);
        testClient4.setRegion(Region.WEST_COAST);
    }

    @Test
    public void ageFilterUnderMin() {
        doubleClickOn("#ageMinField").type(KeyCode.BACK_SPACE).write("-500").type(KeyCode.ENTER);
        verifyThat("#ageMinField", hasText("0"));
    }

    @Test
    public void ageFilterOverMax() {
        doubleClickOn("#ageMaxField").type(KeyCode.BACK_SPACE).write("500").type(KeyCode.ENTER);
        verifyThat("#ageMaxField", hasText("120"));
    }

    @Test
    public void ageFilterDefault() {
        verifyThat("#ageMinField", hasText("0"));
        verifyThat("#ageMaxField", hasText("120"));
    }

    @Test
    public void ageFilterOneYear() {
        doubleClickOn("#ageMaxField").type(KeyCode.BACK_SPACE).write("2").type(KeyCode.ENTER);
        // check 1 value in table
//        clickOn((Node) lookup("john").query());
    }

    @Test
    public void ageFilterTenYears() {
        doubleClickOn("#ageMaxField").type(KeyCode.BACK_SPACE).write("12").type(KeyCode.ENTER);
        // check 3 values in table
    }

    @Test
    public void ageFilter100Years() {
        doubleClickOn("#ageMinField").type(KeyCode.BACK_SPACE).write("50").type(KeyCode.ENTER);
        // check 1 value in table
    }

}