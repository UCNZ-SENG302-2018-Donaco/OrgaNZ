package seng302.Controller;

import static org.junit.Assert.assertEquals;

import seng302.State.State;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Ignore;
import org.junit.Test;

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
        mainController.setWindowContext(WindowContext.defaultContext());
    }

    @Test@Ignore
    public void goToCreateClientTest() {
        clickOn("#createClientButton");
        assertEquals(Page.CREATE_CLIENT, mainController.getCurrentPage());
    }

    @Test@Ignore
    public void goToClientLoginTest() {
        clickOn("#loginClientButton");
        assertEquals(Page.LOGIN_CLIENT, mainController.getCurrentPage());
    }

    @Test@Ignore
    public void goToCreateClinicianTest() {
        clickOn("#createClinicianButton");
        assertEquals(Page.CREATE_CLINICIAN, mainController.getCurrentPage());
    }

    @Test@Ignore
    public void goToClinicianLoginTest() {
        clickOn("#loginClinicianButton");
        assertEquals(Page.LOGIN_STAFF, mainController.getCurrentPage());
    }
}