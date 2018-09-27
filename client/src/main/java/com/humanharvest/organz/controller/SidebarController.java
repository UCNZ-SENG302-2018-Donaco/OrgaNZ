package com.humanharvest.organz.controller;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.controlsfx.control.Notifications;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.controller.spiderweb.SpiderWebController;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;

/**
 * Controller for the sidebar pane imported into every page in the main part of the GUI.
 */
public class SidebarController extends SubController  {

    @FXML
    private Button viewClientButton, registerOrganDonationButton, requestOrganDonationButton,
    viewMedicationsButton, illnessHistoryButton, viewProceduresButton, searchButton, createClientButton,
            organsToDonateButton, transplantsButton, actionHistory, spiderwebButton;

    private Session session;
    private Client client;
    private Set<Organ> currentlyRequestedOrgans;


    /**
     * Gets the ActionInvoker from the current state.
     */
    public SidebarController() {
        session = State.getSession();
    }

    @Override
    public void setup(MainController controller) {
        super.setup(controller);
        UserType userType = session.getLoggedInUserType();

        if (userType == UserType.CLIENT) {
            client = session.getLoggedInClient();
            currentlyRequestedOrgans = client.getCurrentlyRequestedOrgans();
        } else if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
            currentlyRequestedOrgans = client.getCurrentlyRequestedOrgans();
        }

        refreshButtons();
    }

    /**
     * Resets the buttons depending on the clients state
     */
    @Override
    public void refresh() {
        if (client != null) {
            refreshClientDetails();
            refreshClientRequestedOrgans();
        }
    }

    private void refreshClientDetails() {
        Task<Optional<Client>> clientTask = new Task<Optional<Client>>() {
            @Override
            protected Optional<Client> call() throws ServerRestException {
                return com.humanharvest.organz.state.State.getClientManager().getClientByID(client.getUid());
            }
        };

        clientTask.setOnSucceeded(event -> {
            Optional<Client> optionalClient = clientTask.getValue();
            if (optionalClient.isPresent()) {
                client = optionalClient.get();
                refreshButtons();
            } else {
                handleTaskError();
            }
        });

        clientTask.setOnFailed(err -> handleTaskError());

        new Thread(clientTask).start();
    }

    private void refreshClientRequestedOrgans() {
        Task<Set<Organ>> organsTask = new Task<Set<Organ>>() {
            @Override
            protected Set<Organ> call() throws ServerRestException {
                List<TransplantRequest> transplantRequests = com.humanharvest.organz.state.
                        State.getClientResolver().getTransplantRequests(client);
                if (transplantRequests == null) {
                    return null;
                } else {
                    return transplantRequests
                            .stream()
                            .filter(request -> request.getStatus() == TransplantRequestStatus.WAITING)
                            .map(TransplantRequest::getRequestedOrgan)
                            .collect(Collectors.toCollection(() -> EnumSet.noneOf(Organ.class)));
                }

            }
        };

        organsTask.setOnSucceeded(event -> {
            Set<Organ> requestedOrgans = organsTask.getValue();
            if (requestedOrgans != null) {
                currentlyRequestedOrgans = requestedOrgans;
                refreshButtons();
            } else {
                handleTaskError();
            }
        });

        organsTask.setOnFailed(err -> handleTaskError());
        new Thread(organsTask).start();
    }

    private void handleTaskError() {
        Notifications.create()
                .title("Server Error")
                .text("Could not refresh the information for the donor.")
                .showError();
    }

    private void refreshButtons() {
        UserType userType = session.getLoggedInUserType();

        Button[] allButtons = {viewClientButton, registerOrganDonationButton, requestOrganDonationButton,
                viewMedicationsButton, illnessHistoryButton, viewProceduresButton, searchButton, createClientButton,
                organsToDonateButton, transplantsButton, actionHistory, spiderwebButton};

        Button[] clientButtons = {viewProceduresButton, illnessHistoryButton, registerOrganDonationButton, viewClientButton,
                requestOrganDonationButton, viewMedicationsButton};

        Button[] clinicianButtons = {searchButton, createClientButton, organsToDonateButton, transplantsButton, actionHistory};

        Button[] clinicianViewClientButtons = {registerOrganDonationButton, requestOrganDonationButton,
                viewMedicationsButton, illnessHistoryButton, viewProceduresButton, viewClientButton};

        // Hide all buttons then only show buttons relevant to that user type.
        hideButtons(allButtons);

        if (userType == UserType.CLIENT) {
            showButtons(clientButtons);
            // If they're not requesting any organs, don't show them the request organs button
            if (currentlyRequestedOrgans.isEmpty()) {
                hideButton(requestOrganDonationButton);
            }
        } else {

            if (windowContext.isClinViewClientWindow()) {
                showButtons(clinicianViewClientButtons);

                if (client.isDead() && client.isDonor()) {
                    showButton(spiderwebButton);
                }

            } else {
                showButtons(clinicianButtons);
            }
        }
    }

    private void showButton(Button button) {
        button.setVisible(true);
        button.setManaged(true);
    }

    private void showButtons(Button[] buttons) {
        for (Button button: buttons) {
            showButton(button);
        }
    }

    /**
     * Hides all the buttons in the passed-in array.
     *
     * @param buttons The buttons to hide
     */
    private void hideButtons(Button[] buttons) {
        for (Button button : buttons) {
            hideButton(button);
        }
    }

    /**
     * Hides the button from the sidebar.
     *
     * @param button the button to hide
     */
    private void hideButton(Button button) {
        button.setVisible(false);
        button.setManaged(false);
    }


    /**
     * Redirects the GUI to the View Client page.
     */
    @FXML
    private void goToViewClient() {
        PageNavigator.loadPage(Page.VIEW_CLIENT, mainController);
        ProjectionHelper.updateProjection(mainController);
        mainController.closeTouchActionsBar();

    }

    /**
     * Redirects the GUI to the Register Organs page.
     */
    @FXML
    private void goToRegisterOrganDonation() {
        PageNavigator.loadPage(Page.REGISTER_ORGAN_DONATIONS, mainController);
        ProjectionHelper.updateProjection(mainController);
        mainController.closeTouchActionsBar();

    }

    /**
     * Redirects the GUI to the Request Organs page.
     */
    @FXML
    private void goToRequestOrganDonation() {
        PageNavigator.loadPage(Page.REQUEST_ORGANS, mainController);
        ProjectionHelper.updateProjection(mainController);
        mainController.closeTouchActionsBar();

    }

    /**
     * Redirects the GUI to the View Medications page.
     */
    @FXML
    private void goToViewMedications() {
        PageNavigator.loadPage(Page.VIEW_MEDICATIONS, mainController);
        ProjectionHelper.updateProjection(mainController);
        mainController.closeTouchActionsBar();
    }

    /**
     * Redirects the GUI to the Search clients page.
     */
    @FXML
    private void goToSearch() {
        PageNavigator.loadPage(Page.SEARCH, mainController);
        ProjectionHelper.updateProjection(mainController);
        mainController.closeTouchActionsBar();
    }

    /**
     * Redirects the GUI to the Transplants page.
     */
    @FXML
    private void goToTransplants() {
        PageNavigator.loadPage(Page.TRANSPLANTS, mainController);
        ProjectionHelper.updateProjection(mainController);
        mainController.closeTouchActionsBar();
    }

    /**
     * Redirects the GUI to the History page.
     */
    @FXML
    private void goToHistory() {
        PageNavigator.loadPage(Page.HISTORY, mainController);
        ProjectionHelper.updateProjection(mainController);
        mainController.closeTouchActionsBar();
    }

    /**
     * Redirects the GUI to the Illness History page.
     */
    @FXML
    private void goToIllnessHistory() {
        PageNavigator.loadPage(Page.VIEW_MEDICAL_HISTORY, mainController);
        ProjectionHelper.updateProjection(mainController);
        mainController.closeTouchActionsBar();
    }

    /**
     * Redirects the GUI to the View Procedures page.
     */
    @FXML
    private void goToViewProcedures() {
        PageNavigator.loadPage(Page.VIEW_PROCEDURES, mainController);
        ProjectionHelper.updateProjection(mainController);
        mainController.closeTouchActionsBar();
    }


    public void goToCreateClient() {
        PageNavigator.loadPage(Page.CREATE_CLIENT, mainController);
        ProjectionHelper.updateProjection(mainController);
        mainController.closeTouchActionsBar();
    }

    public void goToOrgansToDonate() {
        PageNavigator.loadPage(Page.ORGANS_TO_DONATE, mainController);
        ProjectionHelper.updateProjection(mainController);
        mainController.closeTouchActionsBar();
    }

    public void goToSpiderweb() {
        State.setSpiderwebDonor(windowContext.getViewClient());
        ProjectionHelper.stageClosing();
        new SpiderWebController(windowContext.getViewClient());
    }
}
