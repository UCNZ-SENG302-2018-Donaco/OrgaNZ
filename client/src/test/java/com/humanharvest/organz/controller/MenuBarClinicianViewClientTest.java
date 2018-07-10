package com.humanharvest.organz.controller;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import org.junit.Test;

public class MenuBarClinicianViewClientTest extends ControllerTest {

    private Clinician testClinician = new Clinician("Mr", null, "Tester",
            "9 Fake St", Region.AUCKLAND, 3, "k");
    private Client testClient1 = new Client("tom", "Delta", "1", LocalDate.now().minusYears(32), 1); // 100 years old

    @Override
    protected Page getPage() { return Page.MENU_BAR;  }

    @Override
    protected void initState() {
        State.reset(false);
        State.login(testClinician);
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinViewClientWindow()
                .viewClient(testClient1)
                .build());
    }

    @Test
    public void testViewClient() {
        clickOn("#clientPrimaryItem");
        clickOn("#viewClientItem");
    }

    @Test
    public void testDonateOrgans() {
        clickOn("#organPrimaryItem");
        clickOn("#donateOrganItem");
        assertEquals(Page.REGISTER_ORGAN_DONATIONS, mainController.getCurrentPage());

    }

    @Test
    public void testRequestOrgans() {
        clickOn("#organPrimaryItem");
        clickOn("#requestOrganItem");
        assertEquals(Page.REQUEST_ORGANS, mainController.getCurrentPage());

    }

    @Test
    public void testViewMedications() {
        clickOn("#medicationsPrimaryItem");
        clickOn("#viewMedicationsItem");
        assertEquals(Page.VIEW_MEDICATIONS, mainController.getCurrentPage());
    }

    @Test
    public void testMedicalHistory() {
        clickOn("#medicationsPrimaryItem");
        clickOn("#medicalHistoryItem");
        assertEquals(Page.VIEW_MEDICAL_HISTORY, mainController.getCurrentPage());
    }

    @Test
    public void testProcedures() {
        clickOn("#medicationsPrimaryItem");
        clickOn("#proceduresItem");
        assertEquals(Page.VIEW_PROCEDURES, mainController.getCurrentPage());

    }

    @Test
    public void testCloseWindow() {
        clickOn("#filePrimaryItem");
        clickOn("#closeItem");
    }

}