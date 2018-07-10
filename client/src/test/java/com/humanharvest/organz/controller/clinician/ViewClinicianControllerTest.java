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

@Ignore
public class ViewClinicianControllerTest extends ControllerTest {

    private Clinician testClinician = new Clinician("x", "y", "z", "t", Region.UNSPECIFIED, 3, "p");

    @Override
    protected Page getPage() {
        return Page.VIEW_CLINICIAN;
    }

    @Override
    protected void initState() {
        State.reset(false);
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
    public void validChanges() {
        clickOn("#fname").write("a");
        clickOn("#lname").write("b");
        clickOn("#saveChangesButton");
        assertEquals("xa", testClinician.getFirstName());
        assertEquals("zb", testClinician.getLastName());
    }

    // TODO Notifications get in the way of the save changes button! Way around this needs to be found.
//    @Test
//    public void updatePassword() {
//        clickOn("#password").write("hi");
//
//        clickOn("#saveChangesButton");
//        assertEquals("hi", testClinician.getPassword());
//    }

    @Test(expected = FxRobotException.class)
    public void updateDisplayed() {
        clickOn("#fname").write("a");
        clickOn("#saveChangesButton");
        clickOn("Not yet modified."); // This text should be updated to the time of updates.
    }

    @Test
    public void invalidNames() {
        clickOn("#fname").type(KeyCode.BACK_SPACE);
        clickOn("#lname").type(KeyCode.BACK_SPACE);
        clickOn("#saveChangesButton");
        assertEquals("x", testClinician.getFirstName());
        assertEquals("z", testClinician.getLastName());
    }

    @Test
    public void testLoadClinicianPaneIsHidden() {
        verifyThat("#loadClinicianPane", isInvisible());
    }

//    private void alterFieldsValid() {
//        clickOn("#fname").write("a");
//        clickOn("#mname").type(KeyCode.BACK_SPACE);
//        clickOn("#lname").write("b");
//        clickOn("#workAddress").write("s");
//        clickOn("#region").clickOn("West Coast");
//        clickOn("#password").write("q");
//    }

}