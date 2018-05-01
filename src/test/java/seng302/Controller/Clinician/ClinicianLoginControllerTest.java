package seng302.Controller.Clinician;

import org.junit.Test;
import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.Controller.MainController;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import static org.junit.Assert.*;

public class ClinicianLoginControllerTest extends ControllerTest {
    private Clinician testClinician = new Clinician("Mr", null, "Tester", "9 Fake St", Region.AUCKLAND, 3, "k");

    @Override
    protected Page getPage() {
        return Page.LOGIN_CLINICIAN;
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
        clickOn("#signInButton");
        assertEquals(Page.VIEW_CLINICIAN, mainController.getCurrentPage());
    }

    @Test
    public void loginTestAdmin() {
        clickOn("#staffId").write("3");
        clickOn("#password").write("k");
        clickOn("#signInButton");
        assertEquals(Page.VIEW_CLINICIAN, mainController.getCurrentPage());
    }

    @Test
    public void nonExistingId() {
        clickOn("#staffId").write("9");
        clickOn("#password").write("k");
        clickOn("#signInButton");
        clickOn("OK");
        assertEquals(Page.LOGIN_CLINICIAN, mainController.getCurrentPage());
    }

    @Test
    public void incorrectPassword() {
        clickOn("#staffId").write("0");
        clickOn("#password").write("k");
        clickOn("#signInButton");
        clickOn("OK");
        assertEquals(Page.LOGIN_CLINICIAN, mainController.getCurrentPage());
    }

    @Test
    public void invalidStaffIdInput() {
        clickOn("#staffId").write("a");
        clickOn("#password").write("k");
        clickOn("#signInButton");
        clickOn("OK");
        assertEquals(Page.LOGIN_CLINICIAN, mainController.getCurrentPage());
    }

    @Test
    public void goBackButtonTest() {
        clickOn("#goBackButton");
        assertEquals(Page.LANDING, mainController.getCurrentPage());
    }

}