package seng302.Controller.Client;

import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import org.junit.Test;
import org.testfx.api.FxRobotException;
import seng302.Client;
import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.Utilities.Enums.BloodType;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.isVisible;

public class ViewClientControllerTest extends ControllerTest {
    private Client testClient = new Client("a", "", "b", LocalDate.now(), 1);

    @Override
    protected Page getPage() {
        return Page.VIEW_CLIENT;
    }

    @Override
    protected void initState() {
        State.init();
        setClientDetails();
        State.getClientManager().addClient(testClient);
        State.login(testClient);
        mainController.setWindowContext(WindowContext.defaultContext());
    }

    private void setClientDetails() {
        testClient.setBloodType(BloodType.A_POS);
        testClient.setRegion(Region.AUCKLAND);
        testClient.setHeight(180);
        testClient.setWeight(80);
        testClient.setDateOfBirth(LocalDate.now().minusDays(10));
        testClient.setCurrentAddress("1 Test Road");
    }

    @Test (expected = FxRobotException.class) // Exception should be thrown because the robot cannot find the id!
    public void correctSetupClient() { // Only Clinicians should be able to see this the id field.
        clickOn("#id");
    }

    @Test
    public void validChangesAll() throws Exception {
        clickOn("#fname").type(KeyCode.BACK_SPACE).write("z");
        clickOn("#lname").type(KeyCode.BACK_SPACE).write("q");
        clickOn("#dod").write(LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        clickOn("#region");
        clickOn("West Coast");
        clickOn("#saveChanges");
        press(KeyCode.ENTER);

        assertEquals("z", testClient.getFirstName());
        assertEquals("q", testClient.getLastName());
        assertTrue(testClient.getDateOfDeath() != null);
        assertEquals(Region.WEST_COAST, testClient.getRegion());
    }

    @Test
    public void invalidChangesWeightAndHeight1() {
        clickOn("#weight").type(KeyCode.BACK_SPACE).write("z");
        clickOn("#height").type(KeyCode.BACK_SPACE).write("z");
        clickOn("#saveChanges");
        assertEquals(180, testClient.getHeight(), 0.1);
        assertEquals(80, testClient.getWeight(), 0.1);
    }

    @Test
    public void invalidChangesWeightAndHeight2() {
        clickOn("#weight").type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).write("-50");
        clickOn("#height").type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).write("-2");
        clickOn("#saveChanges");
        assertEquals(180, testClient.getHeight(), 0.1);
        assertEquals(80, testClient.getWeight(), 0.1);
    }

    @Test
    public void invalidChangesNames() {
        clickOn("#fname").type(KeyCode.BACK_SPACE);
        clickOn("#lname").type(KeyCode.BACK_SPACE);
        clickOn("#saveChanges");
        assertEquals("a", testClient.getFirstName());
        assertEquals("b", testClient.getLastName());
    }

    @Test
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
        clickOn("#saveChanges");
        assertEquals(LocalDate.now(), testClient.getDateOfBirth());
    }

    @Test
    public void invalidChangesDODAfterToday() {
        clickOn("#dod").write(LocalDate.of(3000, 1, 1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        clickOn("#saveChanges");
        assertEquals(LocalDate.now(), testClient.getDateOfBirth());
    }

    @Test
    public void viewOrgansButtonTest() {
        clickOn("#viewOrgans");
        assertEquals(Page.REGISTER_ORGAN_DONATIONS, mainController.getCurrentPage());

    }
}