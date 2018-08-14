package com.humanharvest.organz.controller.client;

import static org.junit.Assert.assertEquals;

import javafx.scene.input.KeyCode;

import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import org.junit.Before;
import org.junit.Test;

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

    // Date of death not editable when has overridden organs

    @Test
    public void dateOfDeathIsNotEditableTest() {

        clickOn("#deathDatePicker");
        doubleClickOn("#deathDatePicker").type(KeyCode.BACK_SPACE).write("10/10/" + recentYear);
        clickOn("#applyButton");
        assertEquals(dateOfDeath, testClient.getDateOfDeath());
    }
}