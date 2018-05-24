package seng302.Controller;

import static org.junit.Assert.*;

import java.time.LocalDate;

import seng302.Administrator;
import seng302.Client;
import seng302.State.State;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Test;

public class MenuBarAdminViewClientTest extends ControllerTest {

    private Administrator testAdmin = new Administrator("username", "password");
    private Client testClient = new Client("first", "middle", "last", LocalDate.now().minusYears(32), 1);


    @Test
    public void initState() {
        State.reset(false);
        State.login(testAdmin);
        State.getAdministratorManager().addAdministrator(testAdmin);
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinViewClientWindow()
                .viewClient(testClient)
                .build());
    }

    @Override
    protected Page getPage() {
        return Page.MENU_BAR;
    }

    @Test
    public void refresh() {
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