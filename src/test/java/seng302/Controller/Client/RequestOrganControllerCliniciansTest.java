package seng302.Controller.Client;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import javafx.scene.input.KeyCode;

import seng302.Client;
import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Test;

/**
 * Clinicians should be able to view and edit any Clients requests. They can set new requests and expire old requests,
 * which should then display on the history page.
 */
public class RequestOrganControllerCliniciansTest extends ControllerTest {

    private Clinician testClinician = new Clinician("A", "B", "C", "D",
            Region.UNSPECIFIED, 0, "E");
    private Client testClient = new Client("J", "V", "T", LocalDate.now(), 1);

    @Override
    protected Page getPage() {
        return Page.REQUEST_ORGAN;
    }

    @Override
    protected void initState() {
        State.init();
        State.login(testClinician);
        State.getClientManager().addClient(testClient);
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinicianViewingClientWindow()
                .viewClient(testClient)
                .build());
    }

    @Test
    public void tickSingleOrganUpdates() {
        clickOn("#checkBoxLiver");
        assertEquals(1, testClient.getTransplantRequests().size());
    }

    @Test
    public void tickMultipleOrganUpdates() {
        clickOn("#checkBoxLiver");
        clickOn("#checkBoxHeart");
        clickOn("#checkBoxBone");
        clickOn("#checkBoxKidney");
        clickOn("#checkBoxPancreas");
        assertEquals(5, testClient.getTransplantRequests().size());
    }

    @Test
    public void untickOrgansUpdates() {
        clickOn("#checkBoxLiver");
        clickOn("#checkBoxLiver");
        assertEquals(2, testClient.getTransplantRequests().size());
    }

    @Test
    public void updateInvalidUserId1() {
        clickOn("#fieldUserID").press(KeyCode.BACK_SPACE).write("2").press(KeyCode.ENTER);
        clickOn("#requestHistoryButton");
        assertEquals(Page.REQUEST_ORGAN, mainController.getCurrentPage());
    }

    @Test
    public void updateInvalidUserId2() {
        clickOn("#fieldUserID").press(KeyCode.BACK_SPACE).write("hi").press(KeyCode.ENTER);
        clickOn("#requestHistoryButton");
        assertEquals(Page.REQUEST_ORGAN, mainController.getCurrentPage());
    }

    @Test
    public void updateValidUserId() {
        Client validClient = new Client("N", "M", "P", LocalDate.now(), 2);
        State.getClientManager().addClient(validClient);
        clickOn("#fieldUserID").press(KeyCode.BACK_SPACE).write("2").press(KeyCode.ENTER);
        clickOn("#requestHistoryButton");
        assertEquals(Page.ORGAN_REQUEST_HISTORY, mainController.getCurrentPage());
    }

    @Test
    public void clientView() {
        State.logout();
        State.login(testClient);
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .build());
        System.out.println();
    }

    @Test
    public void navigateToHistory() {
        clickOn("#requestHistoryButton");
        assertEquals(Page.ORGAN_REQUEST_HISTORY, mainController.getCurrentPage());
    }
}