package seng302.Controller;

import static org.junit.Assert.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import seng302.Client;
import seng302.Clinician;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;



import org.junit.Test;

import seng302.Utilities.View.WindowContext;

import java.time.LocalDate;

public class MenuBarClinicianViewClientTest extends ControllerTest {

    private Clinician testClinician = new Clinician("Mr", null, "Tester",
            "9 Fake St", Region.AUCKLAND, 3, "k");
    private Client testClient1 = new Client("tom", "Delta", "1", LocalDate.now().minusYears(32), 1); // 100 years old

    @Override
    protected Page getPage() { return Page.MENU_BAR;  }

    @Override
    protected void initState() {
        State.init();
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