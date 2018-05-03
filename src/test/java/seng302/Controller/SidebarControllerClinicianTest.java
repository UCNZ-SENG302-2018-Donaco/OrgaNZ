package seng302.Controller;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import seng302.Clinician;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext.WindowContextBuilder;


public class SidebarControllerClinicianTest extends ControllerTest{

    private Clinician testClinician = new Clinician("Mr", null, "Tester",
            "9 Fake St", Region.AUCKLAND, 3, "k");


    @Override
    protected Page getPage() {
        return Page.SIDEBAR;
    }

    @Override
    protected void initState() {
        State.init();
        State.getClinicianManager().addClinician(testClinician);
        State.getClientManager().getClientByID(1);
        State.login(testClinician);
        mainController.setWindowContext(new WindowContextBuilder().build());
    }

    @Test
    public void testClickOnUndo() {
        clickOn("#undoButton");
    }

    @Test
    public void testClickOnRedo() {
        clickOn("#redoButton");
    }

    @Test
    public void testClickOnViewClinician() {
        clickOn("#viewClinicianButton");
        assertEquals(Page.VIEW_CLINICIAN, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnSearch() {
        clickOn("#searchButton");
        assertEquals(Page.SEARCH, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnTransplants() {
        clickOn("#transplantsButton");
        assertEquals(Page.TRANSPLANTS, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnHistory() {
        clickOn("#viewHistoryButton");
        assertEquals(Page.HISTORY, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnLogout() {
        clickOn("#logoutButton");
        assertEquals(Page.LANDING, mainController.getCurrentPage());
    }


}