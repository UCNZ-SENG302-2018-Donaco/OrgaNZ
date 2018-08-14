package com.humanharvest.organz.controller.client;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javafx.scene.input.KeyCode;

import com.humanharvest.organz.utilities.enums.Country;
import org.junit.Before;
import org.junit.Test;

public class ViewClientControllerClinician1Test extends ViewClientControllerClinicianBaseTest {

    @Override
    @Before
    public void setClientDetails() {
        testClient.setFirstName("a");
        testClient.setLastName("b");
        testClient.setDateOfBirth(dateOfBirth);
        testClient.setDateOfDeath(dateOfDeath);
        testClient.setTimeOfDeath(timeOfDeath);
        testClient.setCountryOfDeath(Country.US);
        testClient.setRegionOfDeath("New York");
        testClient.setCityOfDeath("New York City");
    }

    // Changing date of death

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
        doubleClickOn("#deathDatePicker").type(KeyCode.BACK_SPACE).write(futureDate);
        clickOn("#applyButton");
        assertEquals(dateOfDeath, testClient.getDateOfDeath());
    }

    @Test
    public void invalidChangeDateOfDeathBeforeBirthday() {
        String beforeBirthday = "10/10/" + (dateOfBirth.getYear() - 2);
        clickOn("#deathDatePicker");
        doubleClickOn("#deathDatePicker").type(KeyCode.BACK_SPACE).write(beforeBirthday);
        clickOn("#applyButton");
        assertEquals(dateOfDeath, testClient.getDateOfDeath());
    }

    // Changing time of death

    @Test
    public void validChangeTimeOfDeath() {
        clickOn("#deathTimeField");
        doubleClickOn("#deathTimeField").type(KeyCode.BACK_SPACE).write("10:10:10");
        clickOn("#applyButton");
        assertEquals(LocalTime.of(10, 10, 10), testClient.getTimeOfDeath());
    }

    @Test
    public void invalidChangeTimeOfDeathBlank() {
        clickOn("#deathTimeField");
        doubleClickOn("#deathTimeField").type(KeyCode.BACK_SPACE);
        clickOn("#applyButton");
        assertEquals(timeOfDeath, testClient.getTimeOfDeath());
    }

    @Test
    public void invalidChangeTimeOfDeathFuture() {
        // Note that this tests a time in the future, but still today.
        // To ensure that we can get a time in the future today, if this test is run in the last ten seconds of the day,
        // it sleeps for 11 seconds to wait until tomorrow.
        if (LocalTime.now().isAfter(LocalTime.of(23, 59, 50))) {
            sleep(11 * 1000);
        }
        clickOn("#deathDatePicker");
        doubleClickOn("#deathDatePicker").type(KeyCode.BACK_SPACE)
                .write(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yy"))); // today

        clickOn("#deathTimeField");
        doubleClickOn("#deathTimeField").type(KeyCode.BACK_SPACE).write("23:59:59");
        clickOn("#applyButton");
        assertEquals(timeOfDeath, testClient.getTimeOfDeath());
    }
}
