package com.humanharvest.organz.controller.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import javafx.scene.input.KeyCode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ViewClientControllerClinician2Test extends ViewClientControllerClinicianBaseTest {

    @Before
    public void setClientDetails() {
        testClient = new Client(1);
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