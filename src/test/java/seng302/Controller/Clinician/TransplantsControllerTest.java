package seng302.Controller.Clinician;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.TableViewMatchers.containsRowAtIndex;
import static org.testfx.matcher.control.TableViewMatchers.hasNumRows;
import static org.testfx.matcher.control.TextMatchers.hasText;

import java.awt.MouseInfo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.geometry.Point2D;
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

public class TransplantsControllerTest extends ControllerTest {

    // Test data

    private Clinician testClinician = new Clinician("A", "B", "C", "D",
            Region.UNSPECIFIED, 0, "E");

    private Client client1 = new Client("Client", "Number", "One", LocalDate.now(), 1);
    private TransplantRequest request1a = new TransplantRequest(Organ.LIVER, true, 1);
    private TransplantRequest request1b = new TransplantRequest(Organ.KIDNEY, true, 1);
    private TransplantRequest request1c = new TransplantRequest(Organ.LUNG, true, 1);

    private Client client2 = new Client("Client", "Number", "Two", LocalDate.now(), 2);
    private TransplantRequest request2a = new TransplantRequest(Organ.LIVER, true, 2);
    private TransplantRequest request2b = new TransplantRequest(Organ.HEART, true, 2);

    private Client client3 = new Client("Client", "Number", "Three", LocalDate.now(), 3);
    private TransplantRequest request3 = new TransplantRequest(Organ.LIVER, true, 3);


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
        client2.setRegion(Region.OTAGO);

        for (int i = 4; i < 119; i++) {
            Client client = new Client("Client", "Number", Integer.toString(i), LocalDate.now(), i);
            TransplantRequest request = new TransplantRequest(Organ.BONE, true, i);
            client.addTransplantRequest(request);
            requests.add(request);
            State.getClientManager().addClient(client);
        }

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
            verifyThat("#tableView", containsRowAtIndex(i, request.getClientName(), request.getRequestedOrgan(),
                    request.getClientRegion(), request.getRequestDateString()));
        }
        verifyThat("#tableView", hasNumRows(30));
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

    /**
     * Checks the current alert dialog displayed (on the top of the window stack) has the expected contents.
     * @param expectedHeader Expected header of the dialog
     * @param expectedContent Expected content of the dialog
     */
    private void checkNewWindowHasHeaderAndContent(String expectedHeader, String expectedContent) {
    }

    @Test
    public void testDoubleClickToOpenClient() {
        // Select Client 1 and double click on them
        clickOn((Node) lookup(NodeQueryUtils.hasText(request1a.getClientName())).query());
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
            System.out.println(node);
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
    }

/*
    //todo fix in headless
    @Test
    public void testNext30Rows() {
        moveTo("#pagination");

        // Move across to the next page button
        moveTo(new Point2D(MouseInfo.getPointerInfo().getLocation().x + 65, MouseInfo.getPointerInfo().getLocation()
                .y)); //todo headless mode: can't get mouse pointer location, need to move straight to "next page"
                button

        // Click on the next page button
        press(MouseButton.PRIMARY);
        release(MouseButton.PRIMARY);

        // Check it has 30 rows
        verifyThat("#tableView", hasNumRows(30));

        // Check all 30 requests are correct
        TransplantRequest request;
        for (int i = 0; i < 30; i++) {
            request = requests.get(i + 30);
            verifyThat("#tableView", containsRowAtIndex(i, request.getClientName(), request.getRequestedOrgan(),
                    request.getClientRegion(), request.getRequestDateString()));
        }

        // Check pagination description
        verifyThat("#displayingXToYOfZText", hasText("Displaying 31-60 of 121"));
    }

    @Test
    public void testPaginationLastPage() {
        moveTo("#pagination");

        // Move across to the next page button
        moveTo(new Point2D(MouseInfo.getPointerInfo().getLocation().x + 65, MouseInfo.getPointerInfo().getLocation()
                .y));

        // Click on the next page button 4 times
        for (int i = 0; i < 4; i++) {
            press(MouseButton.PRIMARY);
            release(MouseButton.PRIMARY);
        }

        // Check it only has 1 row
        verifyThat("#tableView", hasNumRows(1));

        // Check pagination description
        verifyThat("#displayingXToYOfZText", hasText("Displaying 121 of 121"));
    }

    /*Column names:

    "clientCol"
    "organCol"
    "regionCol"
    "dateCol"

    */

/*

TODO fix

    @Test
    public void testReorderByName() {
        clickOn("#clientCol");

        // Sort requests by client name
        requests.sort(new Comparator<TransplantRequest>() {
            @Override
            public int compare(TransplantRequest o1, TransplantRequest o2) {
                return o1.getClientName().compareTo(o2.getClientName());
            }
        });

        // Check all 30 requests are correct
        TransplantRequest request;
        for (int i = 0; i < 30; i++) {
            request = requests.get(i);
            System.out.println(request.getClientName());
            verifyThat("#tableView", containsRowAtIndex(i, request.getClientName(), request.getRequestedOrgan(),
                    request.getClientRegion(), request.getRequestDateString()));

        }

    }
*/

    }
