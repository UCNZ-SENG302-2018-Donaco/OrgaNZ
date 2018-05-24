package seng302.Controller.Clinician;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import seng302.Administrator;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Test;

public class ViewClinicianControllerAdministratorTest extends ControllerTest {

    private Administrator testAdmin = new Administrator("username", "password");

    @Override
    protected Page getPage() {
        return Page.VIEW_CLINICIAN;
    }

    @Override
    protected void initState() {
        State.reset(false);
        State.getAdministratorManager().addAdministrator(testAdmin);
        State.login(testAdmin);
        mainController.setWindowContext(WindowContext.defaultContext());
    }

    @Test
    public void testLoadClinicianPaneIsVisible() {
        verifyThat("#loadClinicianPane", isVisible());
    }

}