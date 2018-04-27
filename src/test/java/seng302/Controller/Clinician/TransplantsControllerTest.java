package seng302.Controller.Clinician;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.TableViewMatchers.containsRowAtIndex;
import static org.testfx.matcher.control.TableViewMatchers.hasNumRows;
import static org.testfx.matcher.control.TextMatchers.hasText;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
/*
//todo fix in headless
    @Test
    public void testNext30Rows() {
        moveTo("#pagination");

        // Move across to the next page button
        moveTo(new Point2D(MouseInfo.getPointerInfo().getLocation().x + 65, MouseInfo.getPointerInfo().getLocation()
                .y));

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
    }

    @Test
    public void testPaginationLastPage() {
        moveTo("#pagination");

        // Move across to the next page button
        moveTo(new Point2D(MouseInfo.getPointerInfo().getLocation().x + 65, MouseInfo.getPointerInfo().getLocation()
                .y));

        // Click on the next page button 4 times
        for (int i = 0; i < 4; i ++) {
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
