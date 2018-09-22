package com.humanharvest.organz.controller;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;

import java.time.LocalDate;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import com.humanharvest.organz.utilities.view.WindowContext.WindowContextBuilder;

import org.junit.Test;

public class SidebarControllerClinicianViewClientTest extends ControllerTest {

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

        State.login(clinician);
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinicianViewClientWindow()
                .viewClient(client)
                .build());
    }

    @Test
    public void testCorrectHiddenButtonsForClinicianAndAdminViewClient() {
        State.reset();

        State.login(clinician);
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinicianViewClientWindow()
                .viewClient(client)
                .build());
        verifyThat("#undoButton", isInvisible());
        verifyThat("#logoutButton", isInvisible());
        verifyThat("#redoButton", isInvisible());
        verifyThat("#searchButton", isInvisible());
        verifyThat("#createClientButton", isInvisible());
        verifyThat("#organsToDonateButton", isInvisible());
        verifyThat("#transplantsButton", isInvisible());
        verifyThat("#actionHistory", isInvisible());
    }

}
