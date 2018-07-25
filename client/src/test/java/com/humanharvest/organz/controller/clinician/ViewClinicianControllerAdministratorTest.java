package com.humanharvest.organz.controller.clinician;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import org.junit.Test;

public class ViewClinicianControllerAdministratorTest extends ControllerTest {

    private final Administrator testAdmin = new Administrator("username", "password");

    @Override
    protected Page getPage() {
        return Page.VIEW_CLINICIAN;
    }

    @Override
    protected void initState() {
        State.reset();
        State.getAdministratorManager().addAdministrator(testAdmin);
        State.login(testAdmin);
        mainController.setWindowContext(WindowContext.defaultContext());
    }

    @Test
    public void testLoadClinicianPaneIsVisible() {
        verifyThat("#loadClinicianPane", isVisible());
    }
}