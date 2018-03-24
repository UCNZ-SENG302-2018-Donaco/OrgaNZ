package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import seng302.Actions.ActionInvoker;
import seng302.AppUI;
import seng302.HistoryItem;
import seng302.State;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * Controller for the sidebar pane imported into every page in the main part of the GUI.
 */
public class SidebarController extends SubController {
    private ActionInvoker invoker;

    /**
     * Gets the ActionInvoker from the current state.
     */
    public SidebarController() {
        invoker = State.getInvoker();
    }

    /**
     * Redirects the GUI to the View Donor page.
     * @param event When the view donor button is clicked.
     */
    @FXML
    private void goToViewDonor(ActionEvent event) {
        PageNavigator.loadPage(Page.VIEW_DONOR, mainController);
    }

    /**
     * Redirects the GUI to the Register Organs page.
     * @param event When the register organs button is clicked.
     */
    @FXML
    private void goToRegisterOrgans(ActionEvent event) {
        PageNavigator.loadPage(Page.REGISTER_ORGANS, mainController);
    }

    /**
     * Redirects the GUI to the History page.
     * @param event When the history button is clicked.
     */
    @FXML
    private void goToHistory(ActionEvent event) {
        PageNavigator.loadPage(Page.HISTORY, mainController);
    }

    /**
     * Opens a save file dialog to choose where to save all donors in the system to a file.
     * @param event When the save button is clicked.
     */
    @FXML
    private void save(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Donors File");
            fileChooser.setInitialDirectory(
                    new File(Paths.get(AppUI.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toString())
            );
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
            File file = fileChooser.showSaveDialog(AppUI.getWindow());
            if (file != null) {
                JSONConverter.saveToFile(file);
                // TODO Make alert with number of donors saved
                HistoryItem save = new HistoryItem("SAVE", "The systems current state was saved.");
                JSONConverter.updateHistory(save, "action_history.json");
                PageNavigator.showAlert(Alert.AlertType.INFORMATION,
                        "Save Successful",
                        "Successfully saved Donors to " + file.getName());
            }
        } catch (URISyntaxException | IOException exc) {
            // TODO Make alert when save fails
            System.err.println(exc.getMessage());
        }
    }

    /**
     * Opens a load file dialog to choose a file to load all donors from.
     * @param event When the load button is clicked.
     */
    @FXML
    private void load(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Donors File");
            fileChooser.setInitialDirectory(
                    new File(Paths.get(AppUI.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toString())
            );
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
            File file = fileChooser.showOpenDialog(AppUI.getWindow());

            if (file != null) {
                JSONConverter.loadFromFile(file);

                HistoryItem load = new HistoryItem("LOAD", "The systems state was loaded from " + file.getName());
                JSONConverter.updateHistory(load, "action_history.json");

                State.logout();
                mainController.clearPageParams();
                PageNavigator.showAlert(Alert.AlertType.INFORMATION,
                        "Load Successful",
                        "Successfully loaded Donors from " + file.getName());
                PageNavigator.loadPage(Page.LANDING, mainController);
            }
        } catch (URISyntaxException | IOException exc) {
            // TODO Make alert when load fails
            PageNavigator.showAlert(Alert.AlertType.WARNING, "Load Failed",
                    "Warning: unrecognisable or invalid file. please make \n sure that you have selected the correct file type.");
        }
    }

    /**
     * Logs out the current user and sends them to the Landing page.
     * @param event When the logout button is clicked.
     */
    @FXML
    private void logout(ActionEvent event) {
        State.logout();
        mainController.clearPageParams();
        PageNavigator.loadPage(Page.LANDING, mainController);
        HistoryItem save = new HistoryItem("LOGOUT", "The Donor logged out");
        JSONConverter.updateHistory(save, "action_history.json");
    }

    /**
     * Undo-s the most recent action performed in the system, and refreshes the current page to reflect the change.
     * @param event When the undo button is clicked.
     */
    @FXML
    private void undo(ActionEvent event) {
        if (invoker.canUndo()) {
            invoker.undo();
            //TODO show what was undone
            HistoryItem save = new HistoryItem("UNDO", "Something was undone.");
            JSONConverter.updateHistory(save, "action_history.json");
            PageNavigator.refreshPage(mainController);
        } else {
            PageNavigator.showAlert(Alert.AlertType.ERROR,
                    "No Undoable Actions",
                    "There are no actions left to undo.");
        }
    }

    /**
     * Redo-s the most recent action performed in the system, and refreshes the current page to reflect the change.
     * @param event When the redo button is clicked.
     */
    @FXML
    private void redo(ActionEvent event) {
        if (invoker.canRedo()) {
            invoker.redo();
            HistoryItem save = new HistoryItem("REDO", "Something was redone");
            JSONConverter.updateHistory(save, "action_history.json");
            PageNavigator.refreshPage(mainController);
        } else {
            PageNavigator.showAlert(Alert.AlertType.ERROR,
                    "No Redoable Actions",
                    "There are no actions left to redo.");
        }
    }
}
