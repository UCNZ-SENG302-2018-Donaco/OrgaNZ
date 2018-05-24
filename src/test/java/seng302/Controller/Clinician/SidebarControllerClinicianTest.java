package seng302.Controller.Clinician;

import static org.junit.Assert.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;

import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext.WindowContextBuilder;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class SidebarControllerClinicianTest extends ControllerTest {

    private Clinician testClinician = new Clinician("Mr", null, "Tester",
            "9 Fake St", Region.AUCKLAND, 3, "k");


    @Override
    protected Page getPage() {
        return Page.SIDEBAR;
    }

    @Override
    protected void initState() {
        State.reset(false);
        State.login(testClinician);
        State.getClinicianManager().addClinician(testClinician);
        mainController.setWindowContext(new WindowContextBuilder().build());
    }

    // Test clicking on action buttons

    @Test
    public void testClickOnUndo() {
        clickOn("#undoButton");
    }

    @Test
    public void testClickOnRedo() {
        clickOn("#redoButton");
    }

    @Test
    public void testClickOnLogout() {
        clickOn("#logoutButton");
        assertEquals(Page.LANDING, mainController.getCurrentPage());
    }

    // Test clicking on buttons to go to another screen

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
        clickOn("Action history");
        assertEquals(Page.HISTORY, mainController.getCurrentPage());
    }

    // Test admin-only buttons are hidden

    @Test
    public void testCreateAdminHidden() {
        verifyThat("#createAdminButton", isInvisible());
    }

    @Test
    public void testCreateClinicianHidden() {
        verifyThat("#createClinicianButton", isInvisible());
    }

    @Test
    public void testSaveHidden() {
        verifyThat("#saveToFileButton", isInvisible());
    }

    @Test
    public void testoadHidden() {
        verifyThat("#loadFromFileButton", isInvisible());
    }

    @Test
    public void testStaffListHidden() {
        verifyThat("#staffListButton", isInvisible());
    }

    // Test client-only buttons are hidden

    @Test
    public void testClientDetailsHidden() {
        verifyThat("#viewClientButton", isInvisible());
    }

    @Test
    public void testDonateOrgansHidden() {
        verifyThat("#registerOrganDonationButton", isInvisible());
    }

    @Test
    public void testRequestOrgansHidden() {
        verifyThat("#requestOrganDonationButton", isInvisible());
    }

    @Test
    public void testMedicationsHidden() {
        verifyThat("#viewMedicationsButton", isInvisible());
    }

    @Test
    public void testMedicalHistoryHidden() {
        verifyThat("#illnessHistoryButton", isInvisible());
    }


}