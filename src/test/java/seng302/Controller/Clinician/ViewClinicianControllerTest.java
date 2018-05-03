package seng302.Controller.Clinician;

import static org.testfx.api.FxAssert.verifyThat;

import javafx.scene.input.KeyCode;
import javafx.stage.Window;
import org.junit.Test;
import org.testfx.api.FxRobotException;
import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import static org.junit.Assert.*;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;

public class ViewClinicianControllerTest extends ControllerTest {
    private Clinician testClinician = new Clinician("x", "y", "z", "t", Region.UNSPECIFIED, 3, "p");

    @Override
    protected Page getPage() {
        return Page.VIEW_CLINICIAN;
    }

    @Override
    protected void initState() {
        State.init();
        State.getClinicianManager().addClinician(testClinician);
        State.login(testClinician);
        mainController.setWindowContext(WindowContext.defaultContext());
    }

    @Test
    public void correctSetup() {
        verifyThat("#staffID", isDisabled()); // Clinician should not be able to edit their staff id as this
        assertEquals("x", testClinician.getFirstName()); // is the unique identifier.
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

//    private void alterFieldsValid() {
//        clickOn("#fname").write("a");
//        clickOn("#mname").type(KeyCode.BACK_SPACE);
//        clickOn("#lname").write("b");
//        clickOn("#workAddress").write("s");
//        clickOn("#region").clickOn("West Coast");
//        clickOn("#password").write("q");
//    }

}