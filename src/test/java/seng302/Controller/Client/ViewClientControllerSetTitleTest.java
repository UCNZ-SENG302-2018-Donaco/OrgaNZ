package seng302.Controller.Client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.scene.input.KeyCode;

import seng302.Client;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.Utilities.Enums.BloodType;
import seng302.Utilities.Enums.Gender;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxRobotException;

public class ViewClientControllerSetTitleTest extends ControllerTest {
    private Client testClient = new Client("a", "", "b", LocalDate.now().minusDays(10), 1);

    @Override
    protected Page getPage() {
        return Page.VIEW_CLIENT;
    }

    @Override
    protected void initState() {
        State.reset(false);
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

    @Test
    public void checkTitleSetTest() {
        testClient = new Client("a", "", "b", LocalDate.now().minusDays(10), 1);
        clickOn("#pname").type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).write("Dad");
        sleep(1000);
        clickOn("#applyButton");
        assertEquals("View Client: Dad", mainController.getTitle());
    }
}