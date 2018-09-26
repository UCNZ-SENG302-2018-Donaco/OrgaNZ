package com.humanharvest.organz.controller;

import static org.junit.Assert.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import java.time.LocalDate;

import javafx.scene.Node;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext.WindowContextBuilder;

import org.junit.Test;

public class SidebarControllerTest extends ControllerTest {

    private final Client client = new Client("Client", "Number", "One", LocalDate.now(), 1);
    private Clinician clinician = new Clinician("Mr", null, "Tester",
            "9 Fake St", Region.AUCKLAND.toString(), Country.NZ, 3, "k");

    @Override
    protected Page getPage() {
        return Page.SIDEBAR;
    }

    @Override
    protected void initState() {
        client.addTransplantRequest(new TransplantRequest(client, Organ.LIVER));
        State.reset();
        State.login(client);
        State.getClientManager().addClient(client);
        State.getClinicianManager().addClinician(clinician);

        mainController.setWindowContext(new WindowContextBuilder().build());
    }

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
    public void testClickOnRequestOrgansWithOrgansRequested() {
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
    public void testClickOnProcedures() {
        clickOn("#viewProceduresButton");
        assertEquals(Page.VIEW_PROCEDURES, mainController.getCurrentPage());
    }


    @Test
    public void testCorrectHiddenButtonsForClient() {
        // Buttons that should be visible
        verifyThat("#viewClientButton", isVisible());
        verifyThat("#registerOrganDonationButton", isVisible());
        verifyThat("#requestOrganDonationButton", isVisible());
        verifyThat("#viewMedicationsButton", isVisible());
        verifyThat("#illnessHistoryButton", isVisible());
        verifyThat("#viewProceduresButton", isVisible());

        // Buttons that shouldn't be visible
        verifyThat("#searchButton", isInvisible());
        verifyThat("#createClientButton", isInvisible());
        verifyThat("#organsToDonateButton", isInvisible());
        verifyThat("#transplantsButton", isInvisible());
        verifyThat("#actionHistory", isInvisible());
        verifyThat("#spiderwebButton", isInvisible());
    }
}