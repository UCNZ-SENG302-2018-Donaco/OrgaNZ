package com.humanharvest.organz.controller.client;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javafx.scene.input.KeyCode;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ViewClientControllerClinicianTest extends ControllerTest {

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

    @Ignore("Ignored until manually overridden organs has been properly implemented")
    @Test
    public void invalidChangeDateOfDeathManuallyOverriddenOrgans() {
        testClient.donateOrgan(Organ.LIVER);
        clickOn("#deathDatePicker");
        doubleClickOn("#deathDatePicker").type(KeyCode.BACK_SPACE).write("10/10/" + recentYear);
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

    // Date of death not editable when has overridden organs

    @Ignore
    @Test
    public void dateOfDeathIsNotEditableTest() {
        testClient.donateOrgan(Organ.LIVER);
        testClient.markDead(LocalDate.now(), LocalTime.now(), Country.NZ, Region.CANTERBURY.toString(), "ChCh");
        testClient.getDonatedOrgans().get(0).manuallyOverride("dropped it");

        clickOn("#deathTimeField");
        doubleClickOn("#deathTimeField").type(KeyCode.BACK_SPACE).write("10:10:10");
        clickOn("#applyButton");
        assertEquals(LocalDate.of(recentYear, 10, 10), testClient.getDateOfDeath());
    }

}