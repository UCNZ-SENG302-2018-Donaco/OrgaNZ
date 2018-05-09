package seng302.Controller.Clinician;

import static org.junit.Assert.assertEquals;

import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Test;

public class StaffLoginControllerTest extends ControllerTest {
    private Clinician testClinician = new Clinician("Mr", null, "Tester", "9 Fake St", Region.AUCKLAND, 3, "k");

    @Override
    protected Page getPage() {
        return Page.LOGIN_STAFF;
    }

    @Override
    protected void initState() {
        State.init();
        State.getClinicianManager().addClinician(testClinician);
        mainController.setWindowContext(WindowContext.defaultContext());
    }

    @Test
    public void loginDefaultAdmin() {
        clickOn("#staffId").write("0");
        clickOn("#password").write("admin");
        clickOn("Log in");
        assertEquals(Page.VIEW_CLINICIAN, mainController.getCurrentPage());
    }

    @Test
    public void loginTestAdmin() {
        clickOn("#staffId").write("3");
        clickOn("#password").write("k");
        clickOn("Log in");
        assertEquals(Page.VIEW_CLINICIAN, mainController.getCurrentPage());
    }

    @Test
    public void nonExistingId() {
        clickOn("#staffId").write("9");
        clickOn("#password").write("k");
        clickOn("Log in");
        clickOn("OK");
        assertEquals(Page.LOGIN_STAFF, mainController.getCurrentPage());
    }

    @Test
    public void incorrectPassword() {
        clickOn("#staffId").write("0");
        clickOn("#password").write("k");
        clickOn("Log in");
        clickOn("OK");
        assertEquals(Page.LOGIN_STAFF, mainController.getCurrentPage());
    }

    @Test
    public void invalidStaffIdInput() {
        clickOn("#staffId").write("a");
        clickOn("#password").write("k");
        clickOn("Log in");
        clickOn("OK");
        assertEquals(Page.LOGIN_STAFF, mainController.getCurrentPage());
    }

    @Test
    public void goBackButtonTest() {
        clickOn("Back");
        assertEquals(Page.LANDING, mainController.getCurrentPage());
    }

}