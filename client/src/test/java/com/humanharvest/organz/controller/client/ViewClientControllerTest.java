package com.humanharvest.organz.controller.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.scene.input.KeyCode;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.api.FxRobotException;

public class ViewClientControllerTest extends ControllerTest {
    private Client testClient = new Client("a", "", "b", LocalDate.now().minusDays(10), 1);

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
        testClient.setDateOfBirth(LocalDate.now().minusDays(10));
        testClient.setBloodType(BloodType.A_POS);
        testClient.setRegion(Region.AUCKLAND);
        testClient.setHeight(180);
        testClient.setWeight(80);
        testClient.setCurrentAddress("1 Test Road");
    }

    @Test (expected = FxRobotException.class) // Exception should be thrown because the robot cannot find the id!
    @Ignore
    public void correctSetupClient() { // Only Clinicians should be able to see this the id field.
        clickOn("#id");
    }

    @Test
    @Ignore
    public void validChangesAll() {
        clickOn("#fname").type(KeyCode.BACK_SPACE).write("z");
        clickOn("#lname").type(KeyCode.BACK_SPACE).write("q");
        clickOn("#mname").write("m");
        clickOn("#pname").type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).write("p");
        clickOn("#dod").write(LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        clickOn("#region");
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
        assertEquals("p", testClient.getPreferredName());
        assertTrue(testClient.getDateOfDeath() != null);
        assertEquals(Region.WEST_COAST, testClient.getRegion());
        assertEquals(Gender.MALE, testClient.getGender());
        assertEquals(Gender.FEMALE, testClient.getGenderIdentity());

    }

    @Test
    @Ignore
    public void invalidChangesWeightAndHeight1() {
        clickOn("#weight").type(KeyCode.BACK_SPACE).write("z");
        clickOn("#height").type(KeyCode.BACK_SPACE).write("z");
        clickOn("#applyButton");
        assertEquals(180, testClient.getHeight(), 0.1);
        assertEquals(80, testClient.getWeight(), 0.1);
    }

    @Test
    @Ignore
    public void invalidChangesWeightAndHeight2() {
        clickOn("#weight").type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).write("-50");
        clickOn("#height").type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).write("-2");
        clickOn("#applyButton");
        assertEquals(180, testClient.getHeight(), 0.1);
        assertEquals(80, testClient.getWeight(), 0.1);
    }

    @Test
    @Ignore
    public void invalidChangesNames() {
        clickOn("#fname").type(KeyCode.BACK_SPACE);
        clickOn("#lname").type(KeyCode.BACK_SPACE);
        clickOn("#applyButton");
        assertEquals("a", testClient.getFirstName());
        assertEquals("b", testClient.getLastName());
    }

    @Test
    @Ignore
    public void invalidChangesDOB() {
        clickOn("#dob").type(KeyCode.BACK_SPACE)
                .type(KeyCode.BACK_SPACE)
                .type(KeyCode.BACK_SPACE)
                .type(KeyCode.BACK_SPACE)
                .type(KeyCode.BACK_SPACE)
                .type(KeyCode.BACK_SPACE)
                .type(KeyCode.BACK_SPACE)
                .type(KeyCode.BACK_SPACE)
                .type(KeyCode.BACK_SPACE)
                .type(KeyCode.BACK_SPACE);
        clickOn("#applyButton");
        assertEquals(LocalDate.now().minusDays(10), testClient.getDateOfBirth());
    }

    @Test
    @Ignore
    public void invalidChangesDODAfterToday() {
        testClient = new Client("a", "", "b", LocalDate.now().minusDays(10), 1);
        clickOn("#dod").write(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        clickOn("#applyButton");
        press(KeyCode.ENTER);
        assertEquals(null, testClient.getDateOfDeath());
    }
}