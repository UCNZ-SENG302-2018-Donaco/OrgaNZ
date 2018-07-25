package com.humanharvest.organz.controller.client;

import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.containsRow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;
import com.humanharvest.organz.utilities.exceptions.OrganAlreadyRegisteredException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext.WindowContextBuilder;
import org.junit.Before;
import org.junit.Test;

public class RequestOrgansControllerClinicianTest extends ControllerTest {

    private final Collection<TransplantRequest> sampleRequests = new ArrayList<>();
    private final Clinician testClinician = new Clinician(
            "Mr", null, "Tester", "9 Fake St", Region.AUCKLAND, 1000, "qwerty");
    private final Client testClient = new Client(1);

    @Override
    protected Page getPage() {
        return Page.REQUEST_ORGANS;
    }

    @Override
    protected void initState() {
        State.reset();
        State.getClientManager().addClient(testClient);
        State.login(testClinician);
        mainController.setWindowContext(new WindowContextBuilder()
                .setAsClinicianViewClientWindow()
                .viewClient(testClient)
                .build());
    }

    private void setSampleRequests() {
        TransplantRequest heartRequest = new TransplantRequest(testClient, Organ.HEART);
        sampleRequests.add(heartRequest);
        sampleRequests.add(new TransplantRequest(testClient, Organ.BONE));

        TransplantRequest pastRequest = new TransplantRequest(testClient, Organ.LIVER);
        pastRequest.setStatus(TransplantRequestStatus.CANCELLED);
        pastRequest.setResolvedDate(LocalDateTime.now());
        sampleRequests.add(pastRequest);
    }

    private Collection<TransplantRequest> getCurrSampleRequests() {
        return sampleRequests.stream()
                .filter(req -> req.getStatus() == TransplantRequestStatus.WAITING)
                .collect(Collectors.toList());
    }

    private Collection<TransplantRequest> getPastSampleRequests() {
        return sampleRequests.stream()
                .filter(req -> req.getStatus() != TransplantRequestStatus.WAITING)
                .collect(Collectors.toList());
    }

    @Before
    public void beforeEach() throws OrganAlreadyRegisteredException {
        setSampleRequests();

        testClient.setOrganDonationStatus(Organ.BONE, true);
        testClient.setOrganDonationStatus(Organ.LIVER, true);

        for (TransplantRequest request : sampleRequests) {
            testClient.addTransplantRequest(request);
        }
        State.getClientManager().applyChangesTo(testClient);
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

    @Test
    public void conflictingRequestsAreColouredTest() {
        TableView<TransplantRequest> currRequestsTable = lookup("#currentRequestsTable").queryTableView();
        clickOn(currRequestsTable); // click on the table so lookups know where abouts to look

        // Get conflicting request
        TransplantRequest conflictingRequest = currRequestsTable.getItems().get(1);

        // Check that it is conflicting
        assertTrue(testClient.getOrganDonationStatus().get(conflictingRequest.getRequestedOrgan()));

        // Get the row it should be in
        TableRow<TransplantRequest> boneRow = lookup(".table-row-cell").nth(1).query();

        // Check that it is the bone
        assertEquals(Organ.BONE, boneRow.getTableView().getItems().get(1).getRequestedOrgan());

        // Check that it is coloured
        assertTrue(boneRow.getStyle().contains("-fx-background-color: lightcoral"));
    }

    private void testOrganBeingAdded(Organ organ) {
        // Select the organ and submit the request
        Node organChoiceBox = lookup("#newOrganChoiceBox").queryAs(ChoiceBox.class);
        clickOn(organChoiceBox);
        clickOn((Node) lookup(organ.toString()).query());
        clickOn("Submit Request");

        // Look up the request for that organ in the data
        Optional<TransplantRequest> findRequest = testClient.getTransplantRequests()
                .stream()
                .filter(req -> req.getRequestedOrgan() == organ)
                .findFirst();

        // Check that the request exists, and that it is in the current requests table
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
    public void submitNewRequestTest() {
        testOrganBeingAdded(Organ.LUNG);
    }

    @Test
    public void submitTwoRequestsTest() {
        testOrganBeingAdded(Organ.LUNG);
        testOrganBeingAdded(Organ.MIDDLE_EAR);
    }

    @Test
    public void resolveRequestDeceasedTest() {
        setSampleRequests();
        testClient.setDateOfBirth(LocalDate.now());

        TableView<TransplantRequest> currRequestsTable = lookup("#currentRequestsTable").queryTableView();
        clickOn(currRequestsTable);

        clickOn((Node) lookup(".table-row-cell").nth(1).query());

        // Selects "deceased" from the options
        clickOn("#cancelTransplantOptions")
                .type(KeyCode.DOWN)
                .type(KeyCode.DOWN)
                .type(KeyCode.DOWN)
                .type(KeyCode.ENTER);

        // Check that death date picker is now visible
        assertTrue((lookup("#deathDatePicker").query()).isVisible());

        clickOn("Resolve Request");
        // Press enter to confirm marking the client as deceased
        type(KeyCode.ENTER);

        //Checks that the client had been marked dead and the request table is empty
        assertNotNull(testClient.getDateOfDeath());
        assertTrue(currRequestsTable.getItems().isEmpty());
    }

    @Test
    public void resolveRequestCuredTest() {
        setSampleRequests();

        TableView<TransplantRequest> currRequestsTable = lookup("#currentRequestsTable").queryTableView();
        clickOn(currRequestsTable);

        TableRow<TransplantRequest> boneRow = lookup(".table-row-cell").nth(1).query();

        // Selects "cured" from the options
        clickOn("#cancelTransplantOptions")
                .type(KeyCode.DOWN)
                .type(KeyCode.DOWN)
                .type(KeyCode.ENTER);
        clickOn(boneRow);

        clickOn("Resolve Request");
        // Press esc to not go to medical history page
        type(KeyCode.ESCAPE);

        // Checks that the selected organ has been removed from the clients transplant request list
        for (Organ organ : testClient.getCurrentlyRequestedOrgans()) {
            System.out.println(organ);
        }
        boolean resolvedOrgansContainsResolvedOrgan = false;
        for (TransplantRequest transplantRequest : testClient.getTransplantRequests()) {
            if (transplantRequest.getRequestedOrgan() == Organ.BONE) {
                resolvedOrgansContainsResolvedOrgan = true;
            }
        }
        assertTrue(resolvedOrgansContainsResolvedOrgan);
        assertFalse(testClient.getCurrentlyRequestedOrgans().contains(Organ.BONE));
    }

    @Test
    public void resolveRequestCustomTest() {
        String reason = "panda";
        setSampleRequests();

        TableView<TransplantRequest> currRequestsTable = lookup("#currentRequestsTable").queryTableView();
        clickOn(currRequestsTable);

        TableRow<TransplantRequest> boneRow = lookup(".table-row-cell").nth(1).query();
        Organ organ = boneRow.getTableView().getItems().get(1).getRequestedOrgan();
        clickOn(boneRow);

        // Selects "Error" from the options
        clickOn("#cancelTransplantOptions")
                .type(KeyCode.DOWN)
                .type(KeyCode.DOWN)
                .type(KeyCode.DOWN)
                .type(KeyCode.DOWN)
                .type(KeyCode.ENTER);

        // Check that death date picker is now visible
        assertTrue((lookup("#customReason").query()).isVisible());

        // Enter custom reason into the reason textField
        clickOn("#customReason")
                .write(reason);

        clickOn("Resolve Request");

        // Checks that the selected organ has been removed from the clients transplant request list
        assertFalse(testClient.getCurrentlyRequestedOrgans().contains(organ));

        // Check that reason has been added to transplantRequest
        Optional<TransplantRequest> findRequest = testClient.getTransplantRequests()
                .stream()
                .filter(req -> req.getRequestedOrgan() == organ)
                .findFirst();

        if (findRequest.isPresent()) {
            TransplantRequest request = findRequest.get();
            assertEquals(reason, request.getResolvedReason());
        } else {
            fail("Request not found");
        }


    }

    private void testOrganBeingResolvedBecauseOfInputError() {
        TableView<TransplantRequest> currRequestsTable = lookup("#currentRequestsTable").queryTableView();
        clickOn(currRequestsTable);

        TableRow<TransplantRequest> firstRow = lookup(".table-row-cell").nth(0).query();
        Organ organ = firstRow.getTableView().getItems().get(0).getRequestedOrgan();

        clickOn(firstRow);

        // Selects "Error" from the options
        clickOn("#cancelTransplantOptions")
                .type(KeyCode.ENTER);

        clickOn("Resolve Request");

        // Checks that the selected organ has been removed from the clients transplant request list
        assertFalse(testClient.getCurrentlyRequestedOrgans().contains(organ));
    }

    @Test
    public void resolveRequestErrorTest() {
        setSampleRequests();
        testOrganBeingResolvedBecauseOfInputError();
    }

    @Test
    public void resolveTwoRequestsTest() {
        setSampleRequests();
        testOrganBeingResolvedBecauseOfInputError();
        testOrganBeingResolvedBecauseOfInputError();
    }
}
