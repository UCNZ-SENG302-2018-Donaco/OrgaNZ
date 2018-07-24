package com.humanharvest.organz.controller.clinician;

import static org.junit.Assert.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;

import java.time.LocalDate;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext.WindowContextBuilder;
import org.junit.Ignore;
import org.junit.Test;

public class SidebarControllerClinViewClientTest extends ControllerTest {

    private Clinician testClinician = new Clinician("Mr", null, "Tester",
            "9 Fake St", Region.AUCKLAND.toString(), 3, "k");
    private Client client = new Client("Client", "Number","One", LocalDate.now(), 1);


    @Override
    protected Page getPage() {
        return Page.SIDEBAR;
    }

    @Override
    protected void initState() {
        State.reset();
        State.login(testClinician);
        State.getClientManager().addClient(client);
        State.getClinicianManager().addClinician(testClinician);
        mainController.setWindowContext(new WindowContextBuilder().setAsClinicianViewClientWindow().viewClient(client).build());
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
    public void testClickOnRequestOrgans() {
        clickOn("#requestOrganDonationButton");
        assertEquals(Page.REQUEST_ORGANS, mainController.getCurrentPage());
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

    // Test buttons are hidden that should only be visible when LOGGED IN as a client (ie not just viewing a client)

    @Test
    public void testLogoutHidden() {
        verifyThat("#logoutButton", isInvisible());
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
    public void testStaffListHidden() {
        verifyThat("#staffListButton", isInvisible());
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