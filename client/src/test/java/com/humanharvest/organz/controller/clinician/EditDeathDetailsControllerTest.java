package com.humanharvest.organz.controller.clinician;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class EditDeathDetailsControllerTest extends ControllerTest{
    Client testClient;



    @Override
    protected Page getPage() {
        return Page.EDIT_DEATH_DETAILS;
    }

    @Override
    protected void initState() {
        State.reset();

        Clinician testClinician = new Clinician("A", "B", "C", "D", Region.UNSPECIFIED.toString(), null,
                0, "E");
        testClient = new Client(1);

        State.getClientManager().addClient(testClient);
        State.login(testClinician);
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinicianViewClientWindow()
                .viewClient(testClient)
                .build());
    }

    @Before
    public void setClientDetails() {
        testClient.setFirstName("a");
        testClient.setLastName("b");
        testClient.setDateOfBirth(LocalDate.now().minusDays(10));
        testClient.setBloodType(BloodType.A_POS);
        testClient.setRegion(Region.AUCKLAND.toString());
        testClient.setHeight(180);
        testClient.setWeight(80);
        testClient.setCurrentAddress("1 Test Road");
    }

    @Test
    @Ignore
    public void addDateAndTimeOfDeath(){
        LocalTime time = LocalTime.now();
        clickOn("#deathTimeField").write(time.toString());
        clickOn("#deathDatePicker").write(LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        clickOn("#applyButton");
        System.out.println(testClient.getDateOfDeath());
        assertNotNull(testClient.getDateOfDeath());
        assertEquals(time,testClient.getTimeOfDeath());
    }

    @Test
    @Ignore
    public void setCountryOutsideNZ(){
        LocalTime time = LocalTime.now();
        clickOn("#deathTimeField").write(time.toString());
        clickOn("#deathDatePicker").write(LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        clickOn("#deathCountry");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        clickOn("#deathRegionTF").write("outsideNewZealand");
        clickOn("#deathCity").write("City in Austria");
        clickOn("#applyButton");
        assertEquals(Country.AT,testClient.getCountryOfDeath());
        assertEquals("outsideNewZealand",testClient.getRegionOfDeath());
        assertEquals("City in Austria",testClient.getCityOfDeath());
    }
}
