package com.humanharvest.organz.controller.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import org.junit.Test;

public class SubmitDeathDetailsControllerTest extends ControllerTest {

    Client testClient;

    @Override
    protected Page getPage() {
        return Page.SUBMIT_DEATH_DETAILS;
    }

    @Override
    protected void initState() {
        State.reset();

        Clinician testClinician = new Clinician("A", "B", "C", "D", Region.UNSPECIFIED.toString(), null, 0, "E");
        testClient = new Client("Test", "Testy", "String", LocalDate.of(1990, 2, 2), 20000);
        setClientDetails();

        State.getClientManager().addClient(testClient);
        State.login(testClinician);
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinicianViewClientWindow()
                .viewClient(testClient)
                .build());
    }

    private void setClientDetails() {
        testClient.setDateOfBirth(LocalDate.now().minusDays(10));
        testClient.setBloodType(BloodType.A_POS);
        testClient.setRegion(Region.AUCKLAND.toString());
        testClient.setHeight(180);
        testClient.setWeight(80);
        testClient.setCountry(Country.NZ);
        testClient.setCurrentAddress("1 Test Road");
    }

    @Test
    public void testDefaultValuesCorrect() {
        // Death date picker should default to current date
        assertEquals(LocalDate.now(), lookup("#deathDatePicker").queryAs(DatePicker.class).getValue());
        // Death time picker should default to empty
        assertEquals("", lookup("#deathTimeField").queryAs(TextField.class).getText());
        // Death country picker should default to client's country
        assertEquals(testClient.getCountry(), lookup("#deathCountry").queryAs(ChoiceBox.class).getValue());
        // Death region picker should default to client's region
        assertEquals(testClient.getRegion(), lookup("#deathRegionCB").queryAs(ChoiceBox.class).getValue().toString());
        // Death city field should default to client's address
        assertEquals(testClient.getCurrentAddress(), lookup("#deathCity").queryAs(TextField.class).getText());
    }

    @Test
    public void testValidSubmit() {
        LocalTime time = LocalTime.of(5, 35);

        clickOn("#deathTimeField").write(time.toString());
        clickOn("Submit");
        type(KeyCode.ENTER); // OK on popup

        assertTrue(testClient.isDead());
        assertEquals(LocalDate.now(), testClient.getDateOfDeath());
        assertEquals(time, testClient.getTimeOfDeath());
        assertEquals(Country.NZ, testClient.getCountryOfDeath());
        assertEquals(Region.AUCKLAND.toString(), testClient.getRegionOfDeath());
        assertEquals("1 Test Road", testClient.getCityOfDeath());
    }

    @Test
    public void testInvalidSubmit() {
        clickOn("#deathTimeField").write("25:69:45");
        clickOn("Submit");

        type(KeyCode.ENTER); // OK on popup
        assertFalse(testClient.isDead());
    }
}
