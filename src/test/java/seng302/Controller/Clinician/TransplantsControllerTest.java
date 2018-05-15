package seng302.Controller.Clinician;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.TableViewMatchers.containsRowAtIndex;
import static org.testfx.matcher.control.TableViewMatchers.hasNumRows;
import static org.testfx.matcher.control.TextMatchers.hasText;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

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
import org.testfx.api.FxRobot;
import org.testfx.util.NodeQueryUtils;
import org.testfx.util.WaitForAsyncUtils;

public class TransplantsControllerTest extends ControllerTest {

    // Test data

    private Clinician testClinician = new Clinician("A", "B", "C", "D",
            Region.UNSPECIFIED, 0, "E");

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
        State.init();
        State.login(testClinician);

        for (Client client : clients) {
            State.getClientManager().addClient(client);
        }

        for (TransplantRequest request : requests1) {
            client1.addTransplantRequest(request);
            requests.add(request);
        }
        for (TransplantRequest request : requests2) {
            client2.addTransplantRequest(request);
            requests.add(request);
        }
        client3.addTransplantRequest(request3);
        requests.add(request3);

        client1.setRegion(Region.CANTERBURY);
        client2.setRegion(Region.AUCKLAND); // Changed to Auckland so that the checkbox is visible.
        // client3's region is left as null

        for (int i = 100; i < 215; i++) {
            Client client = new Client("Client", "Number", createClientName(i), LocalDate.now(), i);
            TransplantRequest request = new TransplantRequest(client, Organ.MIDDLE_EAR);
            client.addTransplantRequest(request);
            client.setRegion(Region.NELSON);
            requests.add(request);
            State.getClientManager().addClient(client);
        }

        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
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

    /**
     * Get the top modal window.
     * @return the top modal window
     */
    private Stage getTopModalStage() {
        // Get a list of windows but ordered from top[0] to bottom[n] ones.
        List<Window> allWindows = new ArrayList<>(new FxRobot().robotContext().getWindowFinder().listWindows());
        Collections.reverse(allWindows);

        // Return the first found modal window.
        return (Stage) allWindows
                .stream()
                .filter(window -> window instanceof Stage)
                .findFirst()
                .orElse(null);
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
        verifyThat("#displayingXToYOfZText", hasText("Displaying 1-30 of 121"));
    }

    /**
     * This test enforces multiple requests per person, pagination, and all elements (name, organ, region, date) being
     * recorded in the table.
     */
    @Test
    public void testFirst30Rows() {
        TransplantRequest request;
        Client reqClient;
        for (int i = 0; i < 30; i++) {
            request = requests.get(i);
            reqClient = request.getClient();
            verifyThat("#tableView", containsRowAtIndex(i,
                    reqClient.getFullName(),
                    request.getRequestedOrgan(),
                    reqClient.getRegion(),
                    request.getRequestDate()));
        }
        verifyThat("#tableView", hasNumRows(30));
    }

    @Test
    public void testDoubleClickToOpenClient() {
        // Select Client 1 and double click on them
        clickOn((Node) lookup(NodeQueryUtils.hasText(request1a.getClient().getFullName())).query());
        press(MouseButton.PRIMARY);
        release(MouseButton.PRIMARY);

        // Get the top pane
        final Stage topModalStage = getTopModalStage();
        assertNotNull(topModalStage);
        final AnchorPane pane = (AnchorPane) topModalStage.getScene().getRoot();

        // Check each subnode is there
        VBox mainVbox = (VBox) pane.getChildren().get(0); // Main pane
        assertNotNull(mainVbox);

        StackPane stackPane = (StackPane) mainVbox.getChildren().get(0); // page holder
        assertNotNull(stackPane);
        assertEquals("pageHolder", stackPane.getId());

        VBox clientVbox = (VBox) stackPane.getChildren().get(0); // Main VBox in client viewer
        assertNotNull(clientVbox);

        SplitPane splitPane = (SplitPane) clientVbox.getChildren().get(0); // Main SplitPane
        assertNotNull(splitPane);

        VBox vbox2 = (VBox) splitPane.getItems().get(1); // Vbox containing a borderpane
        assertNotNull(vbox2);

        BorderPane borderPane = (BorderPane) vbox2.getChildren().get(0); //Borderpane containing header and data
        assertNotNull(borderPane);

        VBox vbox3 = (VBox) borderPane.getCenter(); // Vbox containing two gridpanes
        assertNotNull(vbox3);

        GridPane gridPaneId = (GridPane) vbox3.getChildren().get(0); //Gridpane containing ID fields
        assertNotNull(gridPaneId);
        assertEquals("idPane", gridPaneId.getId());

        GridPane gridPaneFields = (GridPane) vbox3.getChildren().get(1); //Gridpane containing all fields
        assertNotNull(gridPaneFields);
        assertEquals("inputsPane", gridPaneFields.getId());

        // Check all nodes we need to look in are visible
        verifyThat("#id", isVisible());
        verifyThat("#fname", isVisible());
        verifyThat("#mname", isVisible());
        verifyThat("#lname", isVisible());

        // Create a list of all nodes to search in to find user's details
        ArrayList<Node> nodes = new ArrayList<>();
        nodes.addAll(gridPaneFields.getChildren());
        nodes.addAll(gridPaneId.getChildren());

        // Iterate through nodes in both grid panes, check if they are one that we want, and if so, check they are
        // as expected.
        String expectedString = "";
        boolean gotAField;
        int totalChecks = 0;
        for (Node node : nodes) {
            if (node.getId() != null) {
                switch (node.getId()) {
                    case "id":
                        expectedString = Integer.toString(client1.getUid());
                        gotAField = true;
                        break;
                    case "fname":
                        expectedString = client1.getFirstName();
                        gotAField = true;
                        break;
                    case "mname":
                        expectedString = client1.getMiddleName();
                        gotAField = true;
                        break;
                    case "lname":
                        expectedString = client1.getLastName();
                        gotAField = true;
                        break;
                    default:
                        gotAField = false;
                }
                if (gotAField) {
                    TextField textField = (TextField) node;
                    assertEquals(expectedString, textField.getText());
                    totalChecks++;
                }
            }
        }
        assertEquals(4, totalChecks); // it should have checked 4 fields

        WaitForAsyncUtils.asyncFx(topModalStage::close);
        WaitForAsyncUtils.waitForFxEvents();
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
            verifyThat("#tableView", containsRowAtIndex(i,
                    request.getClient().getFullName(),
                    request.getRequestedOrgan(),
                    request.getClient().getRegion(),
                    request.getRequestDate()));
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
    public void testReorderByName() {
        clickOn("#clientCol");

        // Sort requests by client name
        requests.sort(Comparator.comparing(req -> req.getClient().getFullName().toLowerCase()));

        // Check all 30 requests are correct
        for (int i = 0; i < 30; i++) {
            TransplantRequest request = requests.get(i);
            verifyThat("#tableView", containsRowAtIndex(i, request.getClient().getFullName(), request.getRequestedOrgan(),
                    request.getClient().getRegion(), request.getRequestDate()));
        }
    }

    @Test
    public void testReorderByOrgan() {
        clickOn("#organCol");

        // Sort requests by organ
        requests.sort(Comparator.comparing(req -> req.getRequestedOrgan().toString()));

        // Check all 30 requests are correct
        for (int i = 0; i < 30; i++) {
            TransplantRequest request = requests.get(i);
            System.out.println(request);
            verifyThat("#tableView", containsRowAtIndex(i, request.getClient().getFullName(), request.getRequestedOrgan(),
                    request.getClient().getRegion(), request.getRequestDate()));
        }
    }

    @Test
    public void testReorderByRegion() {
        clickOn("#regionCol");

        // Sort requests by client name
        requests.sort((req1, req2) -> {
            if (req1.getClient().getRegion() == null) {
                if (req2.getClient().getRegion() == null) {
                    return 0;
                } else {
                    return -1;
                }
            } else if (req2.getClient().getRegion() == null) {
                return 1;
            }
            return req1.getClient().getRegion().toString().compareTo(req2.getClient().getRegion().toString());
        });

        // Check all 30 requests are correct
        for (int i = 0; i < 30; i++) {
            TransplantRequest request = requests.get(i);
            System.out.println(request.getClient().getRegion());
            verifyThat("#tableView", containsRowAtIndex(i, request.getClient().getFullName(), request.getRequestedOrgan(),
                    request.getClient().getRegion(), request.getRequestDate()));
        }
    }

    @Test
    public void testReorderByDate() {
        clickOn("#dateCol");

        // Sort requests by client name
        requests.sort(Comparator.comparing(TransplantRequest::getRequestDate));

        // Check all 30 requests are correct
        for (int i = 0; i < 30; i++) {
            TransplantRequest request = requests.get(i);
            verifyThat("#tableView", containsRowAtIndex(i, request.getClient().getFullName(), request.getRequestedOrgan(),
                    request.getClient().getRegion(), request.getRequestDate()));
        }
    }

    // ------- Filtering Tests --------

    /**
     * Test that the filter button works as normal if there isn't anything to filter
     * --Should just come as the table is unchanged
     */
    @Test
    public void noFilter() {
        clickOn("#filterButton");
        verifyThat("#tableView", hasNumRows(30));  // 30 rows is max that can be displayed on one page
    }

    /**
     * Test for one organ to be filtered
     */
    @Test
    public void testFilterOneOrgan() {
        clickOn("#organChoice");
        clickOn((Node) lookup(".check-box").nth(3).query());
        clickOn("#filterButton");
        verifyThat("#tableView", containsRowAtIndex(0, request2b.getClient().getFullName(), request2b.getRequestedOrgan(),
                request2b.getClient().getRegion(), request2b.getRequestDate()));
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
        clickOn("#filterButton");
        verifyThat("#tableView", containsRowAtIndex(1, request2b.getClient().getFullName(), request2b.getRequestedOrgan(),
                request2b.getClient().getRegion(), request2b.getRequestDate()));
        verifyThat("#tableView", hasNumRows(2));
    }

    /**
     * Test for one region to be filtered
     */
    @Test
    public void testFilterOneRegion() {
        clickOn("#regionChoice");
        clickOn((Node) lookup(".check-box").nth(1).query());
        clickOn("#filterButton");
        verifyThat("#tableView", containsRowAtIndex(1, request2b.getClient().getFullName(), request2b.getRequestedOrgan(),
                request2b.getClient().getRegion(), request2b.getRequestDate()));
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
        clickOn("#filterButton");
        verifyThat("#tableView", containsRowAtIndex(1, request2b.getClient().getFullName(), request2b.getRequestedOrgan(),
                request2b.getClient().getRegion(), request2b.getRequestDate()));
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
        clickOn("#filterButton");
        verifyThat("#tableView", containsRowAtIndex(0, request2b.getClient().getFullName(), request2b.getRequestedOrgan(),
                request2b.getClient().getRegion(), request2b.getRequestDate()));
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
        clickOn("#filterButton");
        verifyThat("#tableView", containsRowAtIndex(0, request2b.getClient().getFullName(), request2b.getRequestedOrgan(),
                request2b.getClient().getRegion(), request2b.getRequestDate()));
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
        clickOn("#filterButton");
        verifyThat("#tableView", containsRowAtIndex(0, request2b.getClient().getFullName(), request2b.getRequestedOrgan(),
                request2b.getClient().getRegion(), request2b.getRequestDate()));
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
        clickOn("#filterButton");
        verifyThat("#tableView", containsRowAtIndex(0, request2b.getClient().getFullName(), request2b.getRequestedOrgan(),
                request2b.getClient().getRegion(), request2b.getRequestDate()));
        verifyThat("#tableView", hasNumRows(1));
    }

    //TODO Just uncomment out the (//testOrderByX) methods once merged onto s24 branch.
    /**
     * Test that existing features work after one organ has been filtered
     */
    @Test
    public void testFilterOrganAndCheckExistingFeaturesWork() {
        clickOn("#organChoice");
        clickOn((Node) lookup(".check-box").nth(3).query());
        clickOn("#filterButton");
        verifyThat("#tableView", containsRowAtIndex(0, request2b.getClient().getFullName(), request2b.getRequestedOrgan(),
                request2b.getClient().getRegion(), request2b.getRequestDate()));
        verifyThat("#tableView", hasNumRows(1));
        //testReorderByRegion();
    }

    /**
     * Test that existing features work after one region has been filtered
     */
    @Test
    public void testFilterRegionAndCheckExistingFeaturesWork() {
        clickOn("#regionChoice");
        clickOn((Node) lookup(".check-box").nth(1).query());
        clickOn("#filterButton");
        verifyThat("#tableView", containsRowAtIndex(1, request2b.getClient().getFullName(), request2b.getRequestedOrgan(),
                request2b.getClient().getRegion(), request2b.getRequestDate()));
        verifyThat("#tableView", hasNumRows(2));
        //testReorderByDate();
    }

    /**
     * Test that existing features work after both organs and regions have been filtered
     */
    @Test
    public void testFilterBothRegionAndOrganAndCheckExistingFeaturesWork() {
        clickOn("#regionChoice");
        clickOn((Node) lookup(".check-box").nth(1).query());
        clickOn("#filterButton");
        clickOn("#organChoice");
        clickOn((Node) lookup(".check-box").nth(3).query());
        clickOn("#filterButton");
        verifyThat("#tableView", containsRowAtIndex(0, request2b.getClient().getFullName(), request2b.getRequestedOrgan(),
                request2b.getClient().getRegion(), request2b.getRequestDate()));
        verifyThat("#tableView", hasNumRows(1));
        //testReorderByOrgan();
    }
}
