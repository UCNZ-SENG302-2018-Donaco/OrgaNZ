package com.humanharvest.organz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.views.ActionResponseView;
import org.controlsfx.control.Notifications;

/**
 * Controller for the sidebar pane imported into every page in the main part of the GUI.
 */
public class SidebarController extends SubController {

    @FXML
    private Button viewClientButton, registerOrganDonationButton, viewMedicationsButton, viewClinicianButton,
            searchButton, transplantsButton, logoutButton, requestOrganDonationButton, illnessHistoryButton,
            viewProceduresButton, createAdminButton, createClinicianButton, undoButton, redoButton, staffListButton,
            commandLineButton;

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

        Button staffButtons[] = {viewClinicianButton, searchButton, transplantsButton};
        Button adminButtons[] = {createAdminButton, createClinicianButton, staffListButton, commandLineButton};
        Button clinicianButtons[] = {};
        Button clientButtons[] = {viewClientButton, registerOrganDonationButton, viewMedicationsButton,
                illnessHistoryButton, viewProceduresButton};

        // Hide buttons depending on the type of user logged in/the view window type
        if (userType == UserType.CLIENT || windowContext.isClinViewClientWindow()) {
            hideButtons(staffButtons);
            hideButtons(adminButtons);
            hideButtons(clinicianButtons);
        } else if (userType == UserType.CLINICIAN) {
            hideButtons(adminButtons);
            hideButtons(clientButtons);
        } else if (userType == UserType.ADMINISTRATOR) {
            hideButtons(clinicianButtons);
            hideButtons(clientButtons);
        }

        // Staff viewing a client shouldn't see the logout button
        if (windowContext.isClinViewClientWindow()) {
            hideButton(logoutButton);
        }

        // Non-receivers shouldn't see the receiver tab
        if (!shouldShowRequestOrgans(userType)) {
            hideButton(requestOrganDonationButton);
        }

        // Set undo and redo button depending on if they're able to be pressed
        refresh();
    }

    /**
     * Evaluates if the request organs button should be displayed for the current user.
     * @param userType the type of current user
     * @return true if the button should be shown, false otherwise
     */
    private boolean shouldShowRequestOrgans(Session.UserType userType) {
        if (userType == UserType.CLIENT) {
            Client currentClient = session.getLoggedInClient();
            return currentClient.isReceiver();
        } else {
            return windowContext.isClinViewClientWindow();
        }
    }

    /**
     * Hides all the buttons in the passed-in array.
     * @param buttons The buttons to hide
     */
    private void hideButtons(Button buttons[]) {
        for (Button button : buttons) {
            hideButton(button);
        }
    }

    /**
     * Hides the button from the sidebar.
     * @param button the button to hide
     */
    private void hideButton(Button button) {
        button.setVisible(false);
        button.setManaged(false);
    }

    /**
     * Refreshes the undo/redo buttons based on if there are changes to be made
     */
    @Override
    public void refresh() {
        ActionResponseView responseView = State.getActionResolver().getUndo();
        undoButton.setDisable(!responseView.isCanUndo());
        redoButton.setDisable(!responseView.isCanRedo());
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
     * Redirects the GUI to the View Client page.
     */
    @FXML
    private void goToViewClinician() {
        PageNavigator.loadPage(Page.VIEW_CLINICIAN, mainController);
    }

    /**
     * Redirects the GUI to the Search clients page.
     */
    @FXML
    private void goToSearch() {
        PageNavigator.loadPage(Page.SEARCH, mainController);
    }

    /**
     * Redirects the GUI to the Staff list page.
     */
    @FXML
    private void goToStaffList() {
        PageNavigator.loadPage(Page.STAFF_LIST, mainController);
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

    /**
     * Redirects the GUI to the Create administrator page.
     */
    @FXML
    private void goToCreateAdmin() {
        PageNavigator.loadPage(Page.CREATE_ADMINISTRATOR, mainController);
    }

    /**
     * Redirects the GUI to the Create clinician page.
     */
    @FXML
    private void goToCreateClinician() {
        PageNavigator.loadPage(Page.CREATE_CLINICIAN, mainController);
    }

    /**
     * Redirects the GUI to the Admin command line page.
     */
    @FXML
    private void goToCommandLine() {
        PageNavigator.loadPage(Page.COMMAND_LINE, mainController);
    }

    /**
     * Logs out the current user and sends them to the Landing page.
     * TODO duplicated in menubarcontroller - code smell
     */
    @FXML
    private void logout() {
        State.logout();
        for (MainController controller : State.getMainControllers()) {
            if (controller != mainController) {
                controller.closeWindow();
            }
        }
        State.clearMainControllers();
        State.addMainController(mainController);
        mainController.resetWindowContext();
        PageNavigator.loadPage(Page.LANDING, mainController);
    }

    /**
     * Undoes the most recent action performed in the system, and refreshes the current page to reflect the change.
     * TODO duplicated in menubarcontroller - code smell
     */
    @FXML
    private void undo() {
        ActionResponseView responseView = State.getActionResolver().executeUndo(State.getClientEtag());
        Notifications.create().title("Undo").text(responseView.getResultText()).showInformation();
        PageNavigator.refreshAllWindows();
    }

    /**
     * Redoes the most recent action performed in the system, and refreshes the current page to reflect the change.
     * TODO duplicated in menubarcontroller - code smell
     */
    @FXML
    private void redo() {
        ActionResponseView responseView = State.getActionResolver().executeRedo(State.getClientEtag());
        Notifications.create().title("Redo").text(responseView.getResultText()).showInformation();
        PageNavigator.refreshAllWindows();
    }
}
