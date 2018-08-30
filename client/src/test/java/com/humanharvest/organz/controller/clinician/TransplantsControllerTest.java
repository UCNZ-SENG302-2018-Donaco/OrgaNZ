package com.humanharvest.organz.controller.clinician;

import static org.junit.Assert.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.TableViewMatchers.hasNumRows;
import static org.testfx.matcher.control.TextMatchers.hasText;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.CheckComboBox;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext.WindowContextBuilder;
import com.humanharvest.organz.views.client.CreateTransplantRequestView;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.util.NodeQueryUtils;
import org.testfx.util.WaitForAsyncUtils;

@Ignore
public class TransplantsControllerTest extends ControllerTest {

    // Test data

    private Clinician testClinician = new Clinician("A", "B", "C", "D",
            Region.UNSPECIFIED.toString(), null, 0, "E");

    private Client client1 = new Client("Client", "Number", "One", LocalDate.now(), 1);
    private TransplantRequest request1a = new TransplantRequest(client1, Organ.LIVER);
    private TransplantRequest request1b = new TransplantRequest(client1, Organ.PANCREAS);
    private TransplantRequest request1c = new TransplantRequest(client1, Organ.LUNG);

    private Client client2 = new Client("Client", "Number", "Two", LocalDate.now(), 2);
    private TransplantRequest request2a = new TransplantRequest(client2, Organ.LIVER);
    private TransplantRequest request2b = new TransplantRequest(client2, Organ.HEART);

    private Client client3 = new Client("Client", "Number", "Three", LocalDate.now(), 3);
    private TransplantRequest request3 = new TransplantRequest(client3, Organ.LIVER);

    private Client[] clients = {client1, client2, client3};
    private TransplantRequest[] requests1 = {request1a, request1b, request1c};
    private TransplantRequest[] requests2 = {request2a, request2b};

    private List<TransplantRequest> requests = new ArrayList<>();

    // Overridden classes from parent class

    @Override
    protected Page getPage() {
        return Page.TRANSPLANTS;
    }

    @Override
    protected void initState() {
        State.reset();
        State.login(testClinician);

        for (Client client : clients) {
            State.getClientManager().addClient(client);
        }

        for (TransplantRequest request : requests1) {
            State.getClientResolver().createTransplantRequest(client1, new CreateTransplantRequestView(request
                    .getRequestedOrgan(), request.getRequestDateTime()));
            //client1.addTransplantRequest(request);
            requests.add(request);
        }
        for (TransplantRequest request : requests2) {
            State.getClientResolver().createTransplantRequest(client2, new CreateTransplantRequestView(request
                    .getRequestedOrgan(), request.getRequestDateTime()));
            requests.add(request);
        }
        State.getClientResolver().createTransplantRequest(client3, new CreateTransplantRequestView(request3
                .getRequestedOrgan(), request3.getRequestDateTime()));
        requests.add(request3);

        client1.setRegion(Region.CANTERBURY.toString());
        client2.setRegion(Region.AUCKLAND.toString()); // Changed to Auckland so that the checkbox is visible.
        // client3's region is left as null

        for (int i = 100; i < 215; i++) {
            Client client = new Client("Client", "Number", createClientName(i), LocalDate.now(), i);
            TransplantRequest request = new TransplantRequest(client, Organ.MIDDLE_EAR);
            State.getClientManager().addClient(client);
            State.getClientResolver().createTransplantRequest(client, new CreateTransplantRequestView(request
                    .getRequestedOrgan(), request.getRequestDateTime()));
            client.setRegion(Region.NELSON.toString());
            State.getClientManager().applyChangesTo(client);
            requests.add(request);
        }

        State.getClientManager().applyChangesTo(client1);
        State.getClientManager().applyChangesTo(client2);
        State.getClientManager().applyChangesTo(client3);

        mainController.setWindowContext(new WindowContextBuilder()
                .build());
    }

    // Helper methods

    private String createClientName(int i) {
        String[] parts = Integer.toString(i).split("");
        StringBuilder stringBuilder = new StringBuilder();
        String alphaPart;
        for (String part : parts) {
            switch (part) {
                case "0":
                    alphaPart = "a";
                    break;
                case "1":
                    alphaPart = "b";
                    break;
                case "2":
                    alphaPart = "c";
                    break;
                case "3":
                    alphaPart = "d";
                    break;
                case "4":
                    alphaPart = "e";
                    break;
                case "5":
                    alphaPart = "f";
                    break;
                case "6":
                    alphaPart = "g";
                    break;
                case "7":
                    alphaPart = "h";
                    break;
                case "8":
                    alphaPart = "i";
                    break;
                case "9":
                    alphaPart = "j";
                    break;
                default:
                    alphaPart = "x";
                    break;
            }
            stringBuilder.append(alphaPart);
        }
        return stringBuilder.toString();
    }

    @Before
    public void startup() {
        //Make sure the filters are open
        TitledPane q = lookup("Filter").query();
        if (!q.isExpanded()) {
            clickOn("Filter");
        }
        CheckComboBox regionChecks = lookup("#regionChoice").query();
        regionChecks.getCheckModel().clearChecks();
        CheckComboBox organChecks = lookup("#organChoice").query();
        organChecks.getCheckModel().clearChecks();
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
        verifyThat("#displayingXToYOfZText", hasText("Displaying 1-30 of 121"));
    }

    /**
     * This test enforces multiple requests per person, pagination, and all elements (name, organ, region, date) being
     * recorded in the table.
     */
    @Test
    public void testFirst30Rows() {
        TransplantRequest request;
        for (int i = 0; i < 30; i++) {
            request = requests.get(i);
            assertTableContainsRequestAtIndex(
                    lookup("#tableView").queryTableView(),
                    request,
                    i
            );
        }
        verifyThat("#tableView", hasNumRows(30));
    }

    @Test
    public void testDoubleClickToOpenClient() {
        // Select Client 1 and double click on them
        clickOn((Node) lookup(NodeQueryUtils.hasText(request1a.getClient().getFullName())).query());
        press(MouseButton.PRIMARY);
        release(MouseButton.PRIMARY);

        // Get a list of windows but ordered from top[0] to bottom[n] ones.
        List<Window> allWindows = new ArrayList<>(new FxRobot().robotContext().getWindowFinder().listWindows());
        Collections.reverse(allWindows);

        // Get all the windows that are stages
        List<Window> stages = allWindows
                .stream()
                .filter(window -> window instanceof Stage)
                .collect(Collectors.toList());

        assertEquals(2, stages.size());

        WaitForAsyncUtils.asyncFx(((Stage) stages.get(0))::close);
    }

    @Test
    public void testOneWindowIsOpen() {
        List<Window> allWindows = new ArrayList<>(new FxRobot().robotContext().getWindowFinder().listWindows());
        Collections.reverse(allWindows);

        List<Window> stages = allWindows
                .stream()
                .filter(window -> window instanceof Stage)
                .collect(Collectors.toList());

        assertEquals(1, stages.size());
    }

    @Test
    public void testNext30Rows() {
        clickOn(".right-arrow-button");

        // Check it has 30 rows
        verifyThat("#tableView", hasNumRows(30));

        // Check all 30 requests are correct
        TransplantRequest request;
        for (int i = 0; i < 30; i++) {
            request = requests.get(i + 30);
            assertTableContainsRequestAtIndex(
                    lookup("#tableView").queryTableView(),
                    request,
                    i
            );
        }

        // Check pagination description
        verifyThat("#displayingXToYOfZText", hasText("Displaying 31-60 of 121"));
    }

    @Test
    public void testPaginationLastPage() {
        // Click on the next page button 4 times
        for (int i = 0; i < 4; i++) {
            clickOn(".right-arrow-button");
        }

        // Check it only has 1 row
        verifyThat("#tableView", hasNumRows(1));

        // Check pagination description
        verifyThat("#displayingXToYOfZText", hasText("Displaying 121 of 121"));
    }

    @Test
    public void testReorderByDate() {
        clickOn("#dateCol");

        // Sort requests by client name
        requests.sort(Comparator.comparing(TransplantRequest::getRequestDateTime));

        // Check all 30 requests are correct
        for (int i = 0; i < 30; i++) {
            TransplantRequest request = requests.get(i);
            assertTableContainsRequestAtIndex(
                    lookup("#tableView").queryTableView(),
                    request,
                    i
            );
        }
    }

    // ------- Filtering Tests --------

    /**
     * Test that the filter button works as normal if there isn't anything to filter
     * --Should just come as the table is unchanged
     */
    @Test
    public void noFilter() {
        verifyThat("#tableView", hasNumRows(30));  // 30 rows is max that can be displayed on one page
    }

    /**
     * Test for one organ to be filtered
     */
    @Test
    public void testFilterOneOrgan() {
        clickOn("#organChoice");
        clickOn((Node) lookup(".check-box").nth(3).query());
        assertTableContainsRequestAtIndex(
                lookup("#tableView").queryTableView(),
                request2b,
                0
        );
        verifyThat("#tableView", hasNumRows(1));
    }

    /**
     * Test for multiple organs to be filtered
     */
    @Test
    public void testFilterMultipleOrgans() {
        clickOn("#organChoice");
        clickOn((Node) lookup(".check-box").nth(3).query());
        clickOn((Node) lookup(".check-box").nth(4).query());
        clickOn((Node) lookup(".check-box").nth(5).query());
        assertTableContainsRequestAtIndex(
                lookup("#tableView").queryTableView(),
                request2b,
                1
        );
        verifyThat("#tableView", hasNumRows(2));
    }

    /**
     * Test for one region to be filtered
     */
    @Test
    public void testFilterOneRegion() {
        clickOn("#regionChoice");
        clickOn((Node) lookup(".check-box").nth(1).query());
        assertTableContainsRequestAtIndex(
                lookup("#tableView").queryTableView(),
                request2b,
                1
        );
        verifyThat("#tableView", hasNumRows(2));
    }

    /**
     * Test for multiple regions to be filtered
     */
    @Test
    public void testFilterMultipleRegions() {
        clickOn("#regionChoice");
        clickOn((Node) lookup(".check-box").nth(1).query());
        clickOn((Node) lookup(".check-box").nth(2).query());
        clickOn((Node) lookup(".check-box").nth(3).query());
        assertTableContainsRequestAtIndex(
                lookup("#tableView").queryTableView(),
                request2b,
                1
        );
        verifyThat("#tableView", hasNumRows(2));
    }

    /**
     * Test for one region and one organ to be filtered
     */
    @Test
    public void testFilterOneRegionAndOrgan() {
        clickOn("#regionChoice");
        clickOn((Node) lookup(".check-box").nth(1).query());
        clickOn("#organChoice");
        clickOn((Node) lookup(".check-box").nth(3).query());
        assertTableContainsRequestAtIndex(
                lookup("#tableView").queryTableView(),
                request2b,
                0
        );
        verifyThat("#tableView", hasNumRows(1));
    }

    /**
     * Test for one region and multiple organs to be filtered
     */
    @Test
    public void testFilterOneRegionAndMultipleOrgans() {
        clickOn("#regionChoice");
        clickOn((Node) lookup(".check-box").nth(1).query());
        clickOn("#organChoice");
        clickOn((Node) lookup(".check-box").nth(3).query());
        clickOn((Node) lookup(".check-box").nth(4).query());
        clickOn((Node) lookup(".check-box").nth(5).query());
        assertTableContainsRequestAtIndex(
                lookup("#tableView").queryTableView(),
                request2b,
                0
        );
        verifyThat("#tableView", hasNumRows(1));
    }

    /**
     * Test for multiple regions and one organ to be filtered
     */
    @Test
    public void testFilterMultipleRegionsAndOneOrgan() {
        clickOn("#regionChoice");
        clickOn((Node) lookup(".check-box").nth(1).query());
        clickOn((Node) lookup(".check-box").nth(2).query());
        clickOn((Node) lookup(".check-box").nth(3).query());
        clickOn("#organChoice");
        clickOn((Node) lookup(".check-box").nth(3).query());
        assertTableContainsRequestAtIndex(
                lookup("#tableView").queryTableView(),
                request2b,
                0
        );
        verifyThat("#tableView", hasNumRows(1));
    }

    /**
     * Test for multiple regions and organs to be filtered
     */
    @Test
    public void testFilterMultipleRegionsAndOrgans() {
        clickOn("#regionChoice");
        clickOn((Node) lookup(".check-box").nth(1).query());
        clickOn((Node) lookup(".check-box").nth(2).query());
        clickOn((Node) lookup(".check-box").nth(3).query());
        clickOn("#organChoice");
        clickOn((Node) lookup(".check-box").nth(3).query());
        clickOn((Node) lookup(".check-box").nth(4).query());
        clickOn((Node) lookup(".check-box").nth(5).query());
        assertTableContainsRequestAtIndex(
                lookup("#tableView").queryTableView(),
                request2b,
                0
        );
        verifyThat("#tableView", hasNumRows(1));
    }

    /**
     * Test that existing features work after one organ has been filtered
     */
    @Test
    public void testFilterOrganAndCheckExistingFeaturesWork() {
        clickOn("#organChoice");
        clickOn((Node) lookup(".check-box").nth(3).query());
        assertTableContainsRequestAtIndex(
                lookup("#tableView").queryTableView(),
                request2b,
                0
        );
        verifyThat("#tableView", hasNumRows(1));
    }

    /**
     * Test that existing features work after one region has been filtered
     */
    @Test
    public void testFilterRegionAndCheckExistingFeaturesWork() {
        clickOn("#regionChoice");
        clickOn((Node) lookup(".check-box").nth(1).query());
        assertTableContainsRequestAtIndex(
                lookup("#tableView").queryTableView(),
                request2b,
                1
        );
        verifyThat("#tableView", hasNumRows(2));
    }

    /**
     * Test that existing features work after both organs and regions have been filtered
     */
    @Test
    public void testFilterBothRegionAndOrganAndCheckExistingFeaturesWork() {
        clickOn("#regionChoice");
        clickOn((Node) lookup(".check-box").nth(1).query());
        clickOn("#organChoice");
        clickOn((Node) lookup(".check-box").nth(3).query());
        assertTableContainsRequestAtIndex(
                lookup("#tableView").queryTableView(),
                request2b,
                0
        );
        verifyThat("#tableView", hasNumRows(1));
    }

    /**
     * Verifies that the given {@link TableView} contains the given {@link TransplantRequest} at the given rowIndex.
     * Uses asserts that will error if it is not a match
     *
     * @param table The table to check within.
     * @param request The request to check for.
     * @param rowIndex The rowIndex to search at.
     */
    private void assertTableContainsRequestAtIndex(TableView<TransplantRequest> table, TransplantRequest request,
            int rowIndex) {
        TransplantRequest requestAtIndex = table.getItems().get(rowIndex);

        assertEquals(request.getClient(), requestAtIndex.getClient());
        assertEquals(request.getRequestedOrgan(), requestAtIndex.getRequestedOrgan());
        assertEquals(request.getRequestDateTime(), requestAtIndex.getRequestDateTime());
    }
}
