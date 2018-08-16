package com.humanharvest.organz.controller.client;

import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import javafx.scene.input.KeyCode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Testing that date and time of death are not editable when client has overridden organs
 */
public class ViewClientControllerClinician2Test extends ViewClientControllerClinicianBaseTest {

    @Override
    @Before
    public void setClientDetails() {
        testClient.setFirstName("a");
        testClient.setLastName("b");
        testClient.setDateOfBirth(dateOfBirth);
        testClient.donateOrgan(Organ.LIVER);
        testClient.markDead(dateOfDeath, timeOfDeath, Country.NZ, Region.CANTERBURY.toString(), "ChCh");
        testClient.getDonatedOrgans().get(0).manuallyOverride("dropped it");
    }

    @Test
    public void dateOfDeathIsNotEditableTest() {
        clickOn("#deathDatePicker");
        doubleClickOn("#deathDatePicker").type(KeyCode.BACK_SPACE).write("10/10/" + recentYear);
        clickOn("#applyButton");
        assertEquals(dateOfDeath, testClient.getDateOfDeath());
    }

    @Test
    public void TimeOfDeathIsNotEditableTest() {
        clickOn("#deathTimeField");
        doubleClickOn("#deathTimeField").type(KeyCode.BACK_SPACE).write(adjustedTimeOfDeathString);
        clickOn("#applyButton");
        assertEquals(timeOfDeath, testClient.getTimeOfDeath());
    }
}