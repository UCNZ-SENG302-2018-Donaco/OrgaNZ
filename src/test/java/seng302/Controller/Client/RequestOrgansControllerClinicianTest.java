package seng302.Controller.Client;

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

import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;

import seng302.Client;
import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext.WindowContextBuilder;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class RequestOrgansControllerClinicianTest extends ControllerTest {

    private Collection<TransplantRequest> sampleRequests = new ArrayList<>();

    private Clinician testClinician = new Clinician("Mr", null, "Tester", "9 Fake St", Region.AUCKLAND, 1000, "qwerty");
    private Client testClient = new Client();

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
        sampleRequests.add(new TransplantRequest(HEART));
        sampleRequests.add(new TransplantRequest(BONE));

        TransplantRequest pastRequest = new TransplantRequest(LIVER);
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

    @Ignore
    @Test
    public void cancelRequestTest() {
        TableView<TransplantRequest> currRequestsTable = lookup("#currentRequestsTable").queryTableView();

        Node heartCell = from(currRequestsTable)
                .lookup(hasText("Heart"))
                .query();

        System.out.println("heartRow: " + heartCell);
        clickOn(heartCell);
    }
}
