package com.humanharvest.organz.controller.client;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;

import javafx.scene.input.KeyCode;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import org.junit.Before;
import org.junit.Test;

public class ViewClientControllerClinicianTest extends ControllerTest {

    private LocalDate dateOfBirth = LocalDate.now().minusYears(10);
    private LocalDate dateOfDeath = LocalDate.now().minusYears(1);
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
        testClient.setDateOfDeath(dateOfDeath);
        testClient.setTimeOfDeath(LocalTime.now());
        testClient.setCountryOfDeath(Country.US);
        testClient.setRegionOfDeath("New York");
        testClient.setCityOfDeath("New York City");
    }

    @Test
    public void validChangeDateOfDeath() {
        clickOn("#deathDatePicker");
        doubleClickOn("#deathDatePicker").type(KeyCode.BACK_SPACE).write("10/10/" + recentYear);
        clickOn("#applyButton");
        assertEquals(LocalDate.of(recentYear, 10, 10), testClient.getDateOfDeath());
    }

    @Test
    public void invalidChangeDateOfDeathBlank() {
        clickOn("#deathDatePicker");
        doubleClickOn("#deathDatePicker").type(KeyCode.BACK_SPACE);
        clickOn("#applyButton");
        assertEquals(dateOfDeath, testClient.getDateOfDeath());
    }

    @Test
    public void invalidChangeDateOfDeathFuture() {
        String futureDate = "10/10/" + futureYear;
        clickOn("#deathDatePicker");
        doubleClickOn("#deathDatePicker").type(KeyCode.BACK_SPACE);
        clickOn("#applyButton");
        assertEquals(dateOfDeath, testClient.getDateOfDeath());
    }

}