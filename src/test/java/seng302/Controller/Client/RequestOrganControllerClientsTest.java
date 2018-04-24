package seng302.Controller.Client;

import javafx.scene.input.KeyCode;
import org.junit.Test;
import seng302.Client;
import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.State.Session;
import seng302.State.State;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

/**
 * Clients should not be able to edit any fields but they should be allowed to view which organs they're requesting and
 * view their request history.
 */
public class RequestOrganControllerClientsTest extends ControllerTest {
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
        // A Client must have past/present transplant requests to view this page. See the sidebar tests for more info
        try {
            testClient.setOrganRequestStatus(Organ.LIVER, true);
            TransplantRequest t = new TransplantRequest(Organ.LIVER, true);
            testClient.addTransplantRequest(t);
        } catch (OrganAlreadyRegisteredException ex){
            System.out.println(ex);
        }
        State.getClientManager().addClient(testClient);
        State.login(Session.UserType.CLIENT, testClient);
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .build());
    }

    @Test
    public void tickSingleOrganNoUpdates() {
        clickOn("#checkBoxLiver");
        assertEquals(1, testClient.getTransplantRequests().size());
    }

    @Test
    public void untickSingleOrganNoUpdates() {
        clickOn("#checkBoxLiver");
        assertEquals(1, testClient.getTransplantRequests().size());
    }

    @Test
    public void tickMultipleOrganNoUpdates() {
        clickOn("#checkBoxLiver");
        clickOn("#checkBoxHeart");
        clickOn("#checkBoxBone");
        assertEquals(1, testClient.getTransplantRequests().size());
    }


    @Test
    public void navigateToHistory() {
        clickOn("#requestHistoryButton");
        assertEquals(Page.ORGAN_REQUEST_HISTORY, mainController.getCurrentPage());
    }
}