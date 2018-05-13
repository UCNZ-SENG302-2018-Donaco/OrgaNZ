package seng302.Controller.Clinician;

import javafx.scene.input.KeyCode;
import org.junit.Test;
import seng302.Controller.ControllerTest;
import seng302.State.Session;
import seng302.State.State;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import static org.junit.Assert.*;

public class CreateClinicianControllerTest extends ControllerTest {

    @Override
    protected Page getPage() {
        return Page.CREATE_CLINICIAN;
    }

    @Override
    protected void initState() {
        State.init();
        mainController.setWindowContext(WindowContext.defaultContext());
    }

    @Test
    public void noFname() {
        clickOn("#lname").write("l");
        clickOn("#staffId").write("8");
        clickOn("#password").write("a");
        clickOn("#createButton");
        assertEquals(Page.CREATE_CLINICIAN, mainController.getCurrentPage());
    }

    @Test
    public void noLname() {
        clickOn("#fname").write("f");
        clickOn("#staffId").write("8");
        clickOn("#password").write("a");
        clickOn("#createButton");
        assertEquals(Page.CREATE_CLINICIAN, mainController.getCurrentPage());
    }

    @Test
    public void noPassword() {
        clickOn("#fname").write("f");
        clickOn("#lname").write("l");
        clickOn("#staffId").write("8");
        clickOn("#createButton");
        assertEquals(Page.CREATE_CLINICIAN, mainController.getCurrentPage());
    }

    @Test
    public void invalidStaffId() {
        clickOn("#fname").write("f");
        clickOn("#lname").write("l");
        clickOn("#staffId").write("a");
        clickOn("#password").write("a");
        clickOn("#createButton");
        assertEquals(Page.CREATE_CLINICIAN, mainController.getCurrentPage());
    }

    @Test
    public void takenStaffId() {
        clickOn("#fname").write("f");
        clickOn("#lname").write("l");
        clickOn("#staffId").write("0");
        clickOn("#password").write("a");
        clickOn("#createButton");
        assertEquals(Page.CREATE_CLINICIAN, mainController.getCurrentPage());
    }

    @Test
    public void invalidStaffId2() {
        clickOn("#fname").write("f");
        clickOn("#lname").write("l");
        clickOn("#staffId").write("-5");
        clickOn("#password").write("a");
        clickOn("#createButton");
        assertEquals(Page.CREATE_CLINICIAN, mainController.getCurrentPage());
    }

    //TODO find why page won't navigate?
    @Test
    public void validClinician1() {
        clickOn("#fname").write("f");
        clickOn("#lname").write("l");
        clickOn("#staffId").write("8");
        clickOn("#password").write("a");
        clickOn("#createButton");
        assertEquals(Page.VIEW_CLINICIAN, mainController.getCurrentPage());
    }

    @Test
    public void validClinician2() {
        clickOn("#fname").write("f");
        clickOn("#mname").write("m");
        clickOn("#lname").write("l");
        clickOn("#staffId").write("8");
        clickOn("#workAddress").write("k");
        clickOn("#password").write("a");
        clickOn("#createButton");
        assertEquals(Page.VIEW_CLINICIAN, mainController.getCurrentPage());
    }
}