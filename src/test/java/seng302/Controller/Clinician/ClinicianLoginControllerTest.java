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
    private Clinician testClinician = new Clinician("Mr", null, "Tester", "9 Fake St", Region.AUCKLAND, 1000, "qwerty");

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
    public void loginDefaultAdmin() {

    }

}