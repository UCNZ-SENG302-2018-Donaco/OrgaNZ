package com.humanharvest.organz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import com.humanharvest.organz.controller.spiderweb.SpiderWebController;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
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

    private final Session session;

    /**
     * Gets the ActionInvoker from the current state.
     */
    public SidebarController() {
        session = State.getSession();
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        UserType userType = session.getLoggedInUserType();

        Button[] allButtons = {viewClientButton, registerOrganDonationButton, requestOrganDonationButton,
                viewMedicationsButton, illnessHistoryButton, viewProceduresButton, searchButton, createClientButton,
                organsToDonateButton, transplantsButton, actionHistory, spiderwebButton};

        Button[] clientButtons = {viewProceduresButton, illnessHistoryButton, registerOrganDonationButton,
                viewClientButton, requestOrganDonationButton, viewMedicationsButton};

        Button[] clinicianButtons = {
                searchButton, createClientButton, organsToDonateButton, transplantsButton, actionHistory
        };

        Button[] clinicianViewClientButtons = {registerOrganDonationButton, requestOrganDonationButton,
                viewMedicationsButton, illnessHistoryButton, viewProceduresButton, viewClientButton};


        // Hide all buttons then only show buttons relevant to that user type.
        hideButtons(allButtons);

        if (userType == UserType.CLIENT) {
            showButtons(clientButtons);
            // If they're not requesting any organs, don't show them the request organs button
            if (session.getLoggedInClient().getCurrentlyRequestedOrgans().isEmpty()) {
                hideButton(requestOrganDonationButton);
            }
        } else {

            if (windowContext.isClinViewClientWindow()) {
                showButtons(clinicianViewClientButtons);

                if (windowContext.getViewClient().isDead() && windowContext.getViewClient().isDonor()) {
                    showButton(spiderwebButton);
                }

            } else {
                showButtons(clinicianButtons);
            }
        }
        refresh();
    }

    private static void showButton(Button button) {
        button.setVisible(true);
        button.setManaged(true);
    }

    private static void showButtons(Button[] buttons) {
        for (Button button: buttons) {
            showButton(button);
        }
    }

    /**
     * Hides all the buttons in the passed-in array.
     *
     * @param buttons The buttons to hide
     */
    private static void hideButtons(Button[] buttons) {
        for (Button button : buttons) {
            hideButton(button);
        }
    }

    /**
     * Hides the button from the sidebar.
     *
     * @param button the button to hide
     */
    private static void hideButton(Button button) {
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
