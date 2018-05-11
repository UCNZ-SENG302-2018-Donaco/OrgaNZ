package seng302.Controller.Client;

import static org.junit.Assert.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;

import java.time.LocalDate;
import org.junit.Test;

import seng302.Client;
import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;
import seng302.Utilities.View.WindowContext.WindowContextBuilder;

public class SidebarControllerClientTest extends ControllerTest {

    private Client client = new Client("Client", "Number", "One", LocalDate.now(), 1);


    @Override
    protected Page getPage() {
        return Page.SIDEBAR;
    }

    @Override
    protected void initState() {
        State.init();
        State.login(client);
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

    // Test clicking on buttons to go to another screen

    @Test
    public void testClickOnViewClient() {
        clickOn("#viewClientButton");
        assertEquals(Page.VIEW_CLIENT, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnRegisterOrgans() {
        clickOn("#registerOrganDonationButton");
        assertEquals(Page.REGISTER_ORGAN_DONATIONS, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnViewMedications() {
        clickOn("#viewMedicationsButton");
        assertEquals(Page.VIEW_MEDICATIONS, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnMedicalConditions() {
        clickOn("#illnessHistoryButton");
        assertEquals(Page.VIEW_MEDICAL_HISTORY, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnHistory() {
        clickOn("Action history");
        assertEquals(Page.HISTORY, mainController.getCurrentPage());
    }

    @Test
    public void testClickOnLogout() {
        clickOn("#logoutButton");
        assertEquals(Page.LANDING, mainController.getCurrentPage());
    }

    // shouldn't be able to see request organs tab as the client has no organs requested

    @Test
    public void testRequestOrgansHidden() {
        verifyThat("#requestOrganDonationButton", isInvisible());
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

    // Test staff-only buttons are hidden

    @Test
    public void testClinicianDetailsHidden() {
        verifyThat("#viewClinicianButton", isInvisible());
    }

    @Test
    public void testClientSearchHidden() {
        verifyThat("#searchButton", isInvisible());
    }

    @Test
    public void testTranplantRequestsHidden() {
        verifyThat("#transplantsButton", isInvisible());
    }
}