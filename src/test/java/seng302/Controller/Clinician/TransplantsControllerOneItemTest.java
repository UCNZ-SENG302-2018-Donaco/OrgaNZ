package seng302.Controller.Clinician;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.TableViewMatchers.containsRowAtIndex;
import static org.testfx.matcher.control.TableViewMatchers.hasNumRows;
import static org.testfx.matcher.control.TextMatchers.hasText;

import java.time.LocalDate;

import seng302.Client;
import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Test;

public class TransplantsControllerOneItemTest extends ControllerTest {


    // Test data

    private Clinician testClinician = new Clinician("A", "B", "C", "D",
            Region.UNSPECIFIED, 0, "E");
    private Client client = new Client("Client", "Number", "One", LocalDate.now(), 1);
    private TransplantRequest request = new TransplantRequest(client, Organ.LIVER);

    // Overridden classes from parent class

    @Override
    protected Page getPage() {
        return Page.TRANSPLANTS;
    }

    @Override
    protected void initState() {
        State.reset(false);
        State.login(testClinician);

        State.getClientManager().addClient(client);
        client.addTransplantRequest(request);

        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .build());
    }

    // Tests

    @Test
    public void testComponentsAreVisible() {
        verifyThat("#tableView", isVisible());
        verifyThat("#displayingXToYOfZText", isVisible());
        verifyThat("#sidebarPane", isVisible());
        verifyThat("#pagination", isVisible());
    }

    @Test
    public void testPaginationDescription() {
        verifyThat("#displayingXToYOfZText", hasText("Displaying 1 of 1"));
    }

    @Test
    public void testOneRow() {
        verifyThat("#tableView", hasNumRows(1));
        verifyThat("#tableView", containsRowAtIndex(0,
                request.getClient().getFullName(),
                request.getRequestedOrgan(),
                request.getClient().getRegion(),
                request.getRequestDate()));
    }
}
