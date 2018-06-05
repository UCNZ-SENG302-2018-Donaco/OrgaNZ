package com.humanharvest.organz.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

import com.humanharvest.organz.AppUI;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.JSONConverter;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import org.controlsfx.control.Notifications;

/**
 * Controller for the sidebar pane imported into every page in the main part of the GUI.
 */
public class SidebarController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(SidebarController.class.getName());

    private static final String ERROR_SAVING_MESSAGE = "There was an error saving to the file specified.";
    private static final String ERROR_LOADING_MESSAGE = "There was an error loading the file specified.";

    @FXML
    private Button viewClientButton, registerOrganDonationButton, viewMedicationsButton, viewClinicianButton,
            searchButton, transplantsButton, logoutButton, requestOrganDonationButton, illnessHistoryButton,
            viewProceduresButton, createAdminButton, createClinicianButton, undoButton, redoButton, saveToFileButton,
            loadFromFileButton, staffListButton, commandLineButton;

    private ActionInvoker invoker;
    private Session session;

    /**
     * Gets the ActionInvoker from the current state.
     */
    public SidebarController() {
        invoker = State.getInvoker();
        session = State.getSession();
    }

    @Override
    public void setup(MainController controller) {
        super.setup(controller);
        UserType userType = session.getLoggedInUserType();

        Button staffButtons[] = {viewClinicianButton, searchButton, transplantsButton};
        Button adminButtons[] = {createAdminButton, createClinicianButton, staffListButton, saveToFileButton,
                loadFromFileButton, commandLineButton};
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
        undoButton.setDisable(!invoker.canUndo());
        redoButton.setDisable(!invoker.canRedo());
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
    public void refresh() {
        undoButton.setDisable(!invoker.canUndo());
        redoButton.setDisable(!invoker.canRedo());
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
     * Opens a save file dialog to choose where to save all clients in the system to a file.
     */
    @FXML
    private void save() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Clients File");
            fileChooser.setInitialDirectory(
                    new File(Paths.get(AppUI.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                            .getParent().toString())
            );
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
            File file = fileChooser.showSaveDialog(AppUI.getWindow());
            if (file != null) {
                JSONConverter.saveToFile(file);

                Notifications.create().title("Saved").text(String.format("Successfully saved %s clients to file '%s'.",
                        State.getClientManager().getClients().size(), file.getName())).showInformation();

                HistoryItem historyItem = new HistoryItem("SAVE",
                        String.format("The system's current state was saved to file '%s'.", file.getName()));
                State.getSession().addToSessionHistory(historyItem);

                invoker.resetUnsavedUpdates();
                PageNavigator.refreshAllWindows();
            }
        } catch (URISyntaxException | IOException e) {
            PageNavigator.showAlert(Alert.AlertType.WARNING, "Save Failed", ERROR_SAVING_MESSAGE);
            LOGGER.log(Level.SEVERE, ERROR_SAVING_MESSAGE, e);
        }
    }

    /**
     * Opens a load file dialog to choose a file to load all clients from.
     */
    @FXML
    private void load() {

        // Confirm that the user wants to overwrite current data with data from a file
        Optional<ButtonType> response = PageNavigator.showAlert(AlertType.CONFIRMATION,
                "Confirm load from file",
                "Loading from a file will overwrite all current data. Would you like to proceed?");

        if (response.isPresent() && response.get() == ButtonType.OK) {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Load Clients File");
                fileChooser.setInitialDirectory(
                        new File(Paths.get(AppUI.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                                .getParent().toString())
                );
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
                File file = fileChooser.showOpenDialog(AppUI.getWindow());

                if (file != null) {
                    JSONConverter.loadFromFile(file);

                    HistoryItem historyItem = new HistoryItem("LOAD", "The systems state was loaded from " + file.getName());
                    State.getSession().addToSessionHistory(historyItem);

                    invoker.resetUnsavedUpdates();
                    mainController.resetWindowContext();
                    Notifications.create().title("Loaded data").text(
                            String.format("Successfully loaded %d clients from file",
                                    State.getClientManager().getClients().size()))
                            .showInformation();
                    PageNavigator.loadPage(Page.LANDING, mainController);
                }
            } catch (URISyntaxException | IOException | IllegalArgumentException e) {
                PageNavigator.showAlert(Alert.AlertType.WARNING, "Load Failed",
                        "Warning: unrecognisable or invalid file. please make\n"
                                + "sure that you have selected the correct file type.");
                LOGGER.log(Level.SEVERE, ERROR_LOADING_MESSAGE, e);
            }
        }
    }

    /**
     * Logs out the current user and sends them to the Landing page.
     */
    @FXML
    private void logout() {
        State.logout();
        for (MainController controller : State.getMainControllers()) {
            if (controller != mainController) {
                controller.closeWindow();
            }
        }
        State.getMainControllers().clear();
        State.addMainController(mainController);
        mainController.resetWindowContext();
        PageNavigator.loadPage(Page.LANDING, mainController);
    }

    /**
     * Undoes the most recent action performed in the system, and refreshes the current page to reflect the change.
     */
    @FXML
    private void undo() {
        String undoneText = invoker.undo();
        Notifications.create().title("Undo").text(undoneText).showInformation();
        PageNavigator.refreshAllWindows();
    }

    /**
     * Redoes the most recent action performed in the system, and refreshes the current page to reflect the change.
     */
    @FXML
    private void redo() {
        String redoneText = invoker.redo();
        Notifications.create().title("Redo").text(redoneText).showInformation();
        PageNavigator.refreshAllWindows();
    }
}
