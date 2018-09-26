package com.humanharvest.organz.controller;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import java.time.LocalDate;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;

import org.junit.Test;

public class SidebarControllerClinicianTest extends ControllerTest {

    private final Client client = new Client("Client", "Number", "One", LocalDate.now(), 1);
    private Clinician clinician = new Clinician("Mr", null, "Tester",
            "9 Fake St", Region.AUCKLAND.toString(), Country.NZ, 3, "k");

    @Override
    protected Page getPage() {
        return Page.SIDEBAR;
    }

    @Override
    protected void initState() {
        State.reset();
        State.logout();
        State.login(clinician);
        mainController.setWindowContext(WindowContext.defaultContext());
    }

    @Test
    public void testCorrectButtonsForClinicianAndAdminDefault() {
        // Buttons that should be visible
        verifyThat("#searchButton", isVisible());
        verifyThat("#createClientButton", isVisible());
        verifyThat("#organsToDonateButton", isVisible());
        verifyThat("#transplantsButton", isVisible());
        verifyThat("#actionHistory", isVisible());

        // Buttons that shouldn't be visible
        verifyThat("#registerOrganDonationButton", isInvisible());
        verifyThat("#requestOrganDonationButton", isInvisible());
        verifyThat("#viewMedicationsButton", isInvisible());
        verifyThat("#registerOrganDonationButton", isInvisible());
        verifyThat("#illnessHistoryButton", isInvisible());
        verifyThat("#viewProceduresButton", isInvisible());
        verifyThat("#spiderwebButton", isInvisible());
    }

}
