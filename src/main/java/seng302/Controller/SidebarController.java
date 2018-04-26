package seng302.Controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import seng302.Actions.ActionInvoker;
import seng302.AppUI;
import seng302.HistoryItem;
import seng302.State.Session;
import seng302.State.State;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;

import org.controlsfx.control.Notifications;

/**
 * Controller for the sidebar pane imported into every page in the main part of the GUI.
 */
public class SidebarController extends SubController {

    @FXML
    private Button viewDonorButton, registerOrgansButton, viewMedicationsButton, viewClinicianButton, searchButton,
            logoutButton, undoButton, redoButton;

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
        Session.UserType userType = session.getLoggedInUserType();
        if (userType == Session.UserType.DONOR || windowContext.isClinViewDonorWindow()) {
            hideButton(viewClinicianButton);
            hideButton(searchButton);
        } else if (userType == Session.UserType.CLINICIAN) {
            hideButton(viewDonorButton);
            hideButton(registerOrgansButton);
            hideButton(viewMedicationsButton);
        }

        if (windowContext.isClinViewDonorWindow()) {
            hideButton(logoutButton);
        }

        undoButton.setDisable(!invoker.canUndo());
        redoButton.setDisable(!invoker.canRedo());
    }

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
     * Redirects the GUI to the View Donor page.
     */
    @FXML
    private void goToViewDonor() {
        PageNavigator.loadPage(Page.VIEW_DONOR, mainController);
    }

    /**
     * Redirects the GUI to the Register Organs page.
     */
    @FXML
    private void goToRegisterOrgans() {
        PageNavigator.loadPage(Page.REGISTER_ORGANS, mainController);
    }

    /**
     * Redirects the GUI to the View Medications page.
     */
    @FXML
    private void goToViewMedications() {
        PageNavigator.loadPage(Page.VIEW_MEDICATIONS, mainController);
    }

    /**
     * Redirects the GUI to the View Donor page.
     */
    @FXML
    private void goToViewClinician() {
        PageNavigator.loadPage(Page.VIEW_CLINICIAN, mainController);
    }

    /**
     * Redirects the GUI to the Register Organs page.
     */
    @FXML
    private void goToSearch() {
        PageNavigator.loadPage(Page.SEARCH, mainController);
    }

    /**
     * Redirects the GUI to the History page.
     */
    @FXML
    private void goToHistory() {
        PageNavigator.loadPage(Page.HISTORY, mainController);
    }

    /**
     * Opens a save file dialog to choose where to save all donors in the system to a file.
     */
    @FXML
    private void save() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Donors File");
            fileChooser.setInitialDirectory(
                    new File(Paths.get(AppUI.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                            .getParent().toString())
            );
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
            File file = fileChooser.showSaveDialog(AppUI.getWindow());
            if (file != null) {
                JSONConverter.saveToFile(file);

                Notifications.create().title("Saved").text(String.format("Successfully saved %s donors to file %s",
                        State.getDonorManager().getDonors().size(), file.getName())).showInformation();

                HistoryItem save = new HistoryItem("SAVE", "The systems current state was saved.");
                JSONConverter.updateHistory(save, "action_history.json");

                invoker.resetUnsavedUpdates();
                PageNavigator.refreshAllWindows();
            }
        } catch (URISyntaxException | IOException e) {
            PageNavigator.showAlert(Alert.AlertType.WARNING, "Save Failed",
                    "There was an error saving to the file specified.");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Opens a load file dialog to choose a file to load all donors from.
     */
    @FXML
    private void load() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Donors File");
            fileChooser.setInitialDirectory(
                    new File(Paths.get(AppUI.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                            .getParent().toString())
            );
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
            File file = fileChooser.showOpenDialog(AppUI.getWindow());

            if (file != null) {
                JSONConverter.loadFromFile(file);

                HistoryItem load = new HistoryItem("LOAD", "The systems state was loaded from " + file.getName());
                JSONConverter.updateHistory(load, "action_history.json");

                State.logout();
                mainController.resetWindowContext();
                Notifications.create().title("Loaded data").text(
                        String.format("Successfully loaded %d donors from file", State.getDonorManager()
                                .getDonors().size())).showInformation();
                PageNavigator.loadPage(Page.LANDING, mainController);
            }
        } catch (URISyntaxException | IOException e) {
            PageNavigator.showAlert(Alert.AlertType.WARNING, "Load Failed",
                    "Warning: unrecognisable or invalid file. please make \n sure that you have selected the correct file type.");
            System.err.println(e.getMessage());
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
        HistoryItem save = new HistoryItem("LOGOUT", "The user logged out");
        JSONConverter.updateHistory(save, "action_history.json");
    }

    /**
     * Undoes the most recent action performed in the system, and refreshes the current page to reflect the change.
     */
    @FXML
    private void undo() {
        String undoneText = invoker.undo();
        Notifications.create().title("Undo").text(undoneText).showInformation();
        HistoryItem save = new HistoryItem("UNDO", undoneText);
        JSONConverter.updateHistory(save, "action_history.json");
        PageNavigator.refreshAllWindows();
    }

    /**
     * Redoes the most recent action performed in the system, and refreshes the current page to reflect the change.
     */
    @FXML
    private void redo() {
        String redoneText = invoker.redo();
        Notifications.create().title("Redo").text(redoneText).showInformation();
        HistoryItem save = new HistoryItem("REDO", redoneText);
        JSONConverter.updateHistory(save, "action_history.json");
        PageNavigator.refreshAllWindows();
    }
}
