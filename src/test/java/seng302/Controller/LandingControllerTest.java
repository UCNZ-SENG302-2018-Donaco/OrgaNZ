package seng302.Controller;

import org.junit.Test;
import seng302.State.State;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import static org.junit.Assert.*;

/**
 * Testing that the landing page allows for navigation to the correct pages.
 */
public class LandingControllerTest extends ControllerTest {

    @Override
    protected Page getPage() {
        return Page.LANDING;
    }

    @Override
    protected void initState() {
        State.init();
    }

    @Test
    public void goToCreateClientTest() {
        clickOn("#createClientButton");
        assertEquals(Page.CREATE_CLIENT, mainController.getCurrentPage());
    }

    @Test
    public void goToClientLoginTest() {
        clickOn("#loginClientButton");
        assertEquals(Page.LOGIN_CLIENT, mainController.getCurrentPage());
    }

    @Test
    public void goToCreateClinicianTest() {
        clickOn("#createClinicianButton");
        assertEquals(Page.CREATE_CLINICIAN, mainController.getCurrentPage());
    }

    @Test
    public void goToClinicianLoginTest() {
        clickOn("#loginClinicianButton");
        assertEquals(Page.LOGIN_CLINICIAN, mainController.getCurrentPage());
    }
}