package seng302.Controller.Clinician;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.TableViewMatchers.hasNumRows;
import static org.testfx.matcher.control.TextMatchers.hasText;

import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Test;

public class TransplantsControllerZeroItemsTest extends ControllerTest {


    // Test data

    private Clinician testClinician = new Clinician("A", "B", "C", "D",
            Region.UNSPECIFIED, 0, "E");

    // Overridden classes from parent class

    @Override
    protected Page getPage() {
        return Page.TRANSPLANTS;
    }

    @Override
    protected void initState() {
        State.init();
        State.login(testClinician);
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
        verifyThat("#displayingXToYOfZText", hasText("Displaying 0 of 0"));
    }

    @Test
    public void testHasNoRows() {
        verifyThat("#tableView", hasNumRows(0));
    }

}
