package com.humanharvest.organz.controller;


import javafx.fxml.FXML;

import javafx.scene.control.Button;

import com.humanharvest.organz.Client;
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

    private Session session;


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

        Button[] allButtons = {viewClientButton, registerOrganDonationButton, requestOrganDonationButton,
                viewMedicationsButton, illnessHistoryButton, viewProceduresButton, searchButton, createClientButton,
                organsToDonateButton, transplantsButton, actionHistory, spiderwebButton};

        Button[] clientButtons = {viewProceduresButton, illnessHistoryButton, registerOrganDonationButton, viewClientButton,
                requestOrganDonationButton, viewMedicationsButton};

        Button[] clinicianButtons = {searchButton, createClientButton, organsToDonateButton, transplantsButton, actionHistory};

        Button[] clinicianViewClientButtons = {registerOrganDonationButton, requestOrganDonationButton,
                viewMedicationsButton, illnessHistoryButton, viewProceduresButton, viewClientButton, spiderwebButton};


        // Hide all buttons then only show buttons relevant to that user type.
        hideButtons(allButtons);

        if (userType == UserType.CLIENT) {
            showButtons(clientButtons);
        } else {

            if (windowContext.isClinViewClientWindow()) {
                showButtons(clinicianViewClientButtons);
            } else {
                showButtons(clinicianButtons);
            }
        }
        refresh();
    }


    /**
     * Evaluates if the request organs button should be displayed for the current user.
     *
     * @param userType the type of current user
     * @return true if the button should be shown, false otherwise
     */
    private boolean shouldShowRequestOrgans(Session.UserType userType) { //TODO Find out if we want this hidden if
        // they haven't requested organs.
        if (userType == UserType.CLIENT) {
            Client currentClient = session.getLoggedInClient();
            return currentClient.isReceiver();
        } else {
            return windowContext.isClinViewClientWindow();
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
    }

    /**
     * Redirects the GUI to the Register Organs page.
     */
    @FXML
    private void goToRegisterOrganDonation() {
        PageNavigator.loadPage(Page.REGISTER_ORGAN_DONATIONS, mainController);
    }

    /**
     * Redirects the GUI to the Request Organs page.
     */
    @FXML
    private void goToRequestOrganDonation() {
        PageNavigator.loadPage(Page.REQUEST_ORGANS, mainController);
    }

    /**
     * Redirects the GUI to the View Medications page.
     */
    @FXML
    private void goToViewMedications() {
        PageNavigator.loadPage(Page.VIEW_MEDICATIONS, mainController);
    }

    /**
     * Redirects the GUI to the Search clients page.
     */
    @FXML
    private void goToSearch() {
        PageNavigator.loadPage(Page.SEARCH, mainController);
    }


    /**
     * Redirects the GUI to the Transplants page.
     */
    @FXML
    private void goToTransplants() {
        PageNavigator.loadPage(Page.TRANSPLANTS, mainController);
    }

    /**
     * Redirects the GUI to the History page.
     */
    @FXML
    private void goToHistory() {
        PageNavigator.loadPage(Page.HISTORY, mainController);
    }

    /**
     * Redirects the GUI to the Illness History page.
     */
    @FXML
    private void goToIllnessHistory() {
        PageNavigator.loadPage(Page.VIEW_MEDICAL_HISTORY, mainController);
    }

    /**
     * Redirects the GUI to the View Procedures page.
     */
    @FXML
    private void goToViewProcedures() {
        PageNavigator.loadPage(Page.VIEW_PROCEDURES, mainController);
    }


    public void goToCreateClient() {
        PageNavigator.loadPage(Page.CREATE_CLIENT, mainController);
    }

    public void goToOrgansToDonate() {
        PageNavigator.loadPage(Page.ORGANS_TO_DONATE, mainController);
    }

    public void goToSpiderweb() {
        State.setSpiderwebDonor(windowContext.getViewClient());
        SpiderWebController spiderWebController = new SpiderWebController(windowContext.getViewClient());
    }
}
