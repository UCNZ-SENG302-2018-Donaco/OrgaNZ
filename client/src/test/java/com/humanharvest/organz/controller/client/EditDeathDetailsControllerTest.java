package com.humanharvest.organz.controller.client;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class EditDeathDetailsControllerTest extends ControllerTest{
    Client testClient;

    @Override
    protected Page getPage() {
        return Page.VIEW_CLIENT;
    }

    @Override
    protected void initState() {
        State.reset();

        Clinician testClinician = new Clinician("A", "B", "C", "D", Region.UNSPECIFIED.toString(), null,
                0, "E");
        testClient = new Client("Test","Testy","String",LocalDate.of(1990,02,02),20000);

        State.getClientManager().addClient(testClient);
        State.login(testClinician);
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinicianViewClientWindow()
                .viewClient(testClient)
                .build());
    }

    @Before
    public void setClientDetails() {
        testClient.setDateOfBirth(LocalDate.now().minusDays(10));
        testClient.setBloodType(BloodType.A_POS);
        testClient.setRegion(Region.AUCKLAND.toString());
        testClient.setHeight(180);
        testClient.setWeight(80);
        testClient.setCountry(Country.NZ);
        testClient.setCurrentAddress("1 Test Road");
    }


    @Test
    @Ignore
    public void addDateAndTimeOfDeath(){
        clickOn("#editDeathDetailsButton");
        LocalTime time = LocalTime.now();
        clickOn("#deathTimeField").write(time.toString());
        clickOn("#deathDatePicker").write(LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        clickOn("#applyButton");
        type(KeyCode.ENTER); // Close Check box.
        Assert.assertEquals(time,testClient.getTimeOfDeath());
        clickOn("#editDeathDetailsButton");

    }

    @Test
    @Ignore
    public void setCountryOutsideNZ(){
        clickOn("#editDeathDetailsButton");
        LocalTime time = LocalTime.now();
        clickOn("#deathTimeField").write(time.toString());
        clickOn("#deathDatePicker").write(LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        clickOn("#deathCountry");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        clickOn("#deathRegionTF").write("outsideNewZealand");
        clickOn("#deathCity").write("City in Austria");
        clickOn("#applyButton");
        type(KeyCode.ENTER);
        assertEquals(Country.AZ,testClient.getCountryOfDeath());
    }
}
