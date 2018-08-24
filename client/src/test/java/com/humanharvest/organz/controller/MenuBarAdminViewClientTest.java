package com.humanharvest.organz.controller;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class MenuBarAdminViewClientTest extends ControllerTest {

    private final Administrator testAdmin = new Administrator("username", "password");
    private final Client testClient = new Client("first", "middle", "last", LocalDate.now().minusYears(32), 1);

    @Override
    public void initState() {
        State.reset();
        State.login(testAdmin);
        State.getAdministratorManager().addAdministrator(testAdmin);
        State.getClientManager().addClient(testClient);
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinicianViewClientWindow()
                .viewClient(testClient)
                .build());
    }

    @Override
    protected Page getPage() {
        return Page.MENU_BAR;
    }

    @Test
    public void testClickViewClientProfile() {
        clickOn("#clientPrimaryItem");
        clickOn("#viewClientItem");
        assertEquals(Page.VIEW_CLIENT, mainController.getCurrentPage());
    }

    @Test
    public void testClickDonateOrgans() {
        clickOn("#organPrimaryItem");
        clickOn("#donateOrganItem");
        assertEquals(Page.REGISTER_ORGAN_DONATIONS, mainController.getCurrentPage());
    }

    @Test
    public void testClickRequestOrgans() {
        clickOn("#organPrimaryItem");
        clickOn("#requestOrganItem");
        assertEquals(Page.REQUEST_ORGANS, mainController.getCurrentPage());
    }

    @Test
    public void testClickViewMedications() {
        clickOn("#medicationsPrimaryItem");
        clickOn("#viewMedicationsItem");
        assertEquals(Page.VIEW_MEDICATIONS, mainController.getCurrentPage());
    }

    @Test
    public void testClickMedicalHistory() {
        clickOn("#medicationsPrimaryItem");
        clickOn("#medicalHistoryItem");
        assertEquals(Page.VIEW_MEDICAL_HISTORY, mainController.getCurrentPage());
    }

    @Test
    public void testClickMedicalProcedures() {
        clickOn("#medicationsPrimaryItem");
        clickOn("#proceduresItem");
        assertEquals(Page.VIEW_PROCEDURES, mainController.getCurrentPage());
    }
}