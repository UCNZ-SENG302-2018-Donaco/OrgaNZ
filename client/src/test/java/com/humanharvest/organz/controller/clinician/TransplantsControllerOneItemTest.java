package com.humanharvest.organz.controller.clinician;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.TableViewMatchers.containsRowAtIndex;
import static org.testfx.matcher.control.TableViewMatchers.hasNumRows;
import static org.testfx.matcher.control.TextMatchers.hasText;

import java.time.LocalDate;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext.WindowContextBuilder;
import org.junit.Test;

public class TransplantsControllerOneItemTest extends ControllerTest {


    // Test data

    private Clinician testClinician = new Clinician("A", "B", "C", "D",
            Region.UNSPECIFIED.toString(), 0, "E");
    private Client client = new Client("Client", "Number", "One", LocalDate.now(), 1);
    private TransplantRequest request = new TransplantRequest(client, Organ.LIVER);

    // Overridden classes from parent class

    @Override
    protected Page getPage() {
        return Page.TRANSPLANTS;
    }

    @Override
    protected void initState() {
        State.reset();
        State.login(testClinician);

        State.getClientManager().addClient(client);
        client.addTransplantRequest(request);

        mainController.setWindowContext(new WindowContextBuilder()
                .build());
    }

    // Tests

    @Test
    public void testComponentsAreVisible() {
        verifyThat("#tableView", isVisible());
        verifyThat("#displayingXToYOfZText", isVisible());
        verifyThat("#menuBarPane", isVisible());
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
