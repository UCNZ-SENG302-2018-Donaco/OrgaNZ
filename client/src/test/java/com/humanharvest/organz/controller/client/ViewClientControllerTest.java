package com.humanharvest.organz.controller.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import javafx.scene.input.KeyCode;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

public class ViewClientControllerTest extends ControllerTest {

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
        State.login(testClient);
        mainController.setWindowContext(WindowContext.defaultContext());
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
        testClient.setBloodType(BloodType.A_POS);
        testClient.setCountry(Country.NZ);
        testClient.setRegion(Region.AUCKLAND.toString());
        testClient.setHeight(180);
        testClient.setWeight(80);
        testClient.setCurrentAddress("1 Test Road");
    }

    @Test
    public void validChangesAllAndTitleTest() {
        clickOn("#fname").type(KeyCode.BACK_SPACE).write("z");
        clickOn("#lname").type(KeyCode.BACK_SPACE).write("q");
        clickOn("#mname").write("m");
        clickOn("#pname").type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).write("Dad");
        clickOn("#regionCB");
        clickOn("West Coast");
        clickOn("#gender");
        clickOn("Male");
        clickOn("#genderIdentity");
        clickOn("Female");
        clickOn("#applyButton");
        press(KeyCode.ENTER);

        assertEquals("z", testClient.getFirstName());
        assertEquals("q", testClient.getLastName());
        assertEquals("m", testClient.getMiddleName());
        assertEquals("Dad", testClient.getPreferredName());
        assertEquals(Region.WEST_COAST.toString(), testClient.getRegion());
        assertEquals(Gender.MALE, testClient.getGender());
        assertEquals(Gender.FEMALE, testClient.getGenderIdentity());

        assertEquals("View Client: Dad", mainController.getTitle());
    }

    @Test
    public void invalidChangesWeightAndHeight1() {
        clickOn("#weight").type(KeyCode.BACK_SPACE).write("z");
        clickOn("#height").type(KeyCode.BACK_SPACE).write("z");
        clickOn("#applyButton");
        assertEquals(180, testClient.getHeight(), 0.1);
        assertEquals(80, testClient.getWeight(), 0.1);
    }

    @Test
    public void invalidChangesWeightAndHeight2() {
        clickOn("#weight").
                type(KeyCode.BACK_SPACE)
                .type(KeyCode.BACK_SPACE)
                .type(KeyCode.BACK_SPACE)
                .type(KeyCode.BACK_SPACE)
                .type(KeyCode.BACK_SPACE)
                .write("-50");

        clickOn("#height")
                .type(KeyCode.BACK_SPACE)
                .type(KeyCode.BACK_SPACE)
                .type(KeyCode.BACK_SPACE)
                .type(KeyCode.BACK_SPACE)
                .type(KeyCode.BACK_SPACE)
                .write("-2");

        clickOn("#applyButton");
        assertEquals(180, testClient.getHeight(), 0.1);
        assertEquals(80, testClient.getWeight(), 0.1);
    }

    @Test
    public void invalidChangesNames() {
        clickOn("#fname").type(KeyCode.BACK_SPACE);
        clickOn("#lname").type(KeyCode.BACK_SPACE);
        clickOn("#applyButton");
        assertEquals("a", testClient.getFirstName());
        assertEquals("b", testClient.getLastName());
    }

    @Test
    public void validChangeDateOfBirth() {
        clickOn("#dob");
        doubleClickOn("#dob").type(KeyCode.BACK_SPACE).write("10/10/2000");
        clickOn("#applyButton");
        assertEquals(LocalDate.of(2000, 10, 10), testClient.getDateOfBirth());
    }

    @Test
    public void invalidChangeDateOfBirthBlank() {
        clickOn("#dob");
        doubleClickOn("#dob").type(KeyCode.BACK_SPACE);
        clickOn("#applyButton");
        assertEquals(dateOfBirth, testClient.getDateOfBirth());
    }

    @Test
    public void invalidChangeDateOfBirthFuture() {
        String futureDate = "10/10/" + futureYear;
        clickOn("#dob");
        doubleClickOn("#dob").type(KeyCode.BACK_SPACE).write(futureDate);
        clickOn("#applyButton");
        assertEquals(dateOfBirth, testClient.getDateOfBirth());
    }

    @Test
    public void cannotChangeDateOfDeath() {
        clickOn("#deathDatePicker");
        doubleClickOn("#deathDatePicker").type(KeyCode.BACK_SPACE).write("10/10/" + recentYear);
        clickOn("#applyButton");
        assertEquals(dateOfDeath, testClient.getDateOfDeath());
    }
}