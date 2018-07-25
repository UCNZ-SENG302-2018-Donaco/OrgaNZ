package com.humanharvest.organz.controller.clinician;

import static org.junit.Assert.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;

import javafx.scene.input.KeyCode;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.api.FxRobotException;

public class ViewClinicianControllerTest extends ControllerTest {

    private final Clinician testClinician = new Clinician("x", "y", "z", "t", Region.UNSPECIFIED, 3, "p");

    @Override
    protected Page getPage() {
        return Page.VIEW_CLINICIAN;
    }

    @Override
    protected void initState() {
        State.reset();
        State.getClinicianManager().addClinician(testClinician);
        State.login(testClinician);
        mainController.setWindowContext(WindowContext.defaultContext());
    }

    @Test
    public void correctSetup() {
        assertEquals("x", testClinician.getFirstName());
        assertEquals("y", testClinician.getMiddleName());
        assertEquals("z", testClinician.getLastName());
        assertEquals("t", testClinician.getWorkAddress());
        assertEquals(Region.UNSPECIFIED, testClinician.getRegion());
        assertEquals(3, testClinician.getStaffId());
        clickOn("Not yet modified."); // Checking the FxRobot can see this text.
    }

    @Test
    @Ignore
    public void validChanges() {
        clickOn("#fname").write("a");
        clickOn("#lname").write("b");
        clickOn("#applyButton");
        assertEquals("xa", testClinician.getFirstName());
        assertEquals("zb", testClinician.getLastName());
    }

    @Test(expected = FxRobotException.class)
    @Ignore
    public void updateDisplayed() {
        clickOn("#fname").write("a");
        clickOn("#applyButton");
        clickOn("Not yet modified."); // This text should be updated to the time of updates.
    }

    @Test
    public void invalidNames() {
        clickOn("#fname").type(KeyCode.BACK_SPACE);
        clickOn("#lname").type(KeyCode.BACK_SPACE);
        clickOn("#applyButton");
        assertEquals("x", testClinician.getFirstName());
        assertEquals("z", testClinician.getLastName());
    }

    @Test
    public void testLoadClinicianPaneIsHidden() {
        verifyThat("#loadClinicianPane", isInvisible());
    }
}