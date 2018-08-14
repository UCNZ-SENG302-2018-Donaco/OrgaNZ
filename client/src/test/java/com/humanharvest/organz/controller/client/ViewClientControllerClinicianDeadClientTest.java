package com.humanharvest.organz.controller.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import javafx.scene.input.KeyCode;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

public class ViewClientControllerClinicianDeadClientTest extends ControllerTest {

    private LocalDate dateOfBirth = LocalDate.now().minusYears(10);
    private LocalDate dateOfDeath = LocalDate.now().minusYears(1);
    private LocalTime timeOfDeath = LocalTime.parse("10:00:00");
    private int futureYear = LocalDate.now().plusYears(2).getYear();
    private int recentYear = LocalDate.now().minusYears(2).getYear();
    private Client testClient = new Client(1);

    @Override
    protected Page getPage() {
        return Page.VIEW_CLIENT;
    }

    @Override
    protected void initState() {
        State.reset();
        setClientDetails();
        State.getClientManager().addClient(testClient);
        State.login(State.getClinicianManager().getDefaultClinician()); // login as default clinician
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinicianViewClientWindow()
                .viewClient(testClient)
                .build());
    }

    @Before
    public void setClientDetails() {
        testClient.setFirstName("a");
        testClient.setLastName("b");
        testClient.setDateOfBirth(dateOfBirth);
        testClient.donateOrgan(Organ.LIVER);
        testClient.markDead(dateOfDeath, timeOfDeath, Country.NZ, Region.CANTERBURY.toString(), "ChCh");
        testClient.getDonatedOrgans().get(0).manuallyOverride("dropped it");
    }

    // Date of death not editable when has overridden organs

    @Test
    public void dateOfDeathIsNotEditableTest() {

        clickOn("#deathTimeField");
        clickOn("#deathDatePicker");
        doubleClickOn("#deathDatePicker").type(KeyCode.BACK_SPACE).write("10/10/" + recentYear);
        clickOn("#applyButton");
        assertEquals(dateOfDeath, testClient.getDateOfDeath());
    }
}