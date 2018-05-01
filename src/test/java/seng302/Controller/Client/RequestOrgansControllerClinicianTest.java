package seng302.Controller.Client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.containsRow;
import static org.testfx.matcher.control.TextMatchers.hasText;
import static seng302.TransplantRequest.RequestStatus.CANCELLED;
import static seng302.TransplantRequest.RequestStatus.WAITING;
import static seng302.Utilities.Enums.Organ.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.scene.Camera;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;

import seng302.Client;
import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext.WindowContextBuilder;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.util.NodeQueryUtils;

public class RequestOrgansControllerClinicianTest extends ControllerTest {

    private Collection<TransplantRequest> sampleRequests = new ArrayList<>();
    TransplantRequest heartRequest;

    private Clinician testClinician = new Clinician("Mr", null, "Tester", "9 Fake St", Region.AUCKLAND, 1000, "qwerty");
    private Client testClient = new Client(1);

    @Override
    protected Page getPage() {
        return Page.REQUEST_ORGANS;
    }

    @Override
    protected void initState() {
        State.init();
        State.login(testClinician);
        mainController.setWindowContext(new WindowContextBuilder()
                .setAsClinViewClientWindow()
                .viewClient(testClient)
                .build());
    }

    private void setSampleRequests() {
        heartRequest = new TransplantRequest(testClient, HEART);
        sampleRequests.add(heartRequest);
        sampleRequests.add(new TransplantRequest(testClient, BONE));

        TransplantRequest pastRequest = new TransplantRequest(testClient, LIVER);
        pastRequest.setStatus(CANCELLED);
        pastRequest.setResolvedDate(LocalDateTime.now());
        sampleRequests.add(pastRequest);
    }

    private Collection<TransplantRequest> getCurrSampleRequests() {
        return sampleRequests.stream()
                .filter(req -> req.getStatus() == WAITING)
                .collect(Collectors.toList());
    }

    private Collection<TransplantRequest> getPastSampleRequests() {
        return sampleRequests.stream()
                .filter(req -> req.getStatus() != WAITING)
                .collect(Collectors.toList());
    }

    @Before
    public void beforeEach() {
        setSampleRequests();

        testClient.getOrganDonationStatus().put(BONE, true);
        testClient.getOrganDonationStatus().put(LIVER, true);
        for (TransplantRequest request : sampleRequests) {
            testClient.addTransplantRequest(request);
        }
        pageController.refresh();
    }

    @Test
    public void existingCurrRequestsAreShownTest() {
        for (TransplantRequest request : getCurrSampleRequests()) {
            verifyThat("#currentRequestsTable", containsRow(
                    request.getRequestedOrgan(),
                    request.getRequestDate()));
        }
    }

    @Test
    public void existingPastRequestsAreShownTest() {
        for (TransplantRequest request : getPastSampleRequests()) {
            verifyThat("#pastRequestsTable", containsRow(
                    request.getRequestedOrgan(),
                    request.getRequestDate()));
        }
    }

    @Ignore
    @Test
    public void conflictingRequestsAreColouredTest() {
        TableView<TransplantRequest> currRequestsTable = lookup("#currentRequestsTable").queryTableView();
        for (TransplantRequest request : currRequestsTable.getItems()) {
            if (testClient.getOrganDonationStatus().get(request.getRequestedOrgan())) {
                String searchTerm = request.getRequestedOrgan().toString();
                System.out.println(searchTerm);
                Node rowNode = lookup(hasText(searchTerm)).query();
                System.out.println(rowNode);
                System.out.println(rowNode.getStyle());
                assertTrue(rowNode.getStyle().contains("-fx-background-color: lightcoral"));
            }
        }
    }

    @Test
    public void submitNewRequestTest() {
        Node organChoiceBox = lookup("#newOrganChoiceBox").queryAs(ChoiceBox.class);
        clickOn(organChoiceBox);
        clickOn((Node) lookup(LUNG.toString()).query());
        clickOn("Submit Request");

        Optional<TransplantRequest> findRequest = testClient.getTransplantRequests()
                .stream()
                .filter(req -> req.getRequestedOrgan() == LUNG)
                .findFirst();

        if (findRequest.isPresent()) {
            TransplantRequest request = findRequest.get();
            verifyThat("#currentRequestsTable", containsRow(
                    request.getRequestedOrgan(),
                    request.getRequestDate()));
        } else {
            fail("The request was not added to the client.");
        }
    }

    @Test
    public void cancelRequestTest() {
        TableView<TransplantRequest> currRequestsTable = lookup("#currentRequestsTable").queryTableView();
        clickOn(currRequestsTable); // click on the table so lookups know where abouts to look

        TableRow<TransplantRequest> heartCell = lookup(".table-row-cell").nth(0).query();

        // Check that we start with two items, and this is indeed the heart row
        assertEquals(2, currRequestsTable.getItems().size());
        assertEquals(Organ.HEART, heartCell.getTableView().getItems().get(0).getRequestedOrgan());

        clickOn(heartCell);
        clickOn("#markAsCancelledButton");

        // Check that we now have 1 item
        assertEquals(1, currRequestsTable.getItems().size());
    }
}
