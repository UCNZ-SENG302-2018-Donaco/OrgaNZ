package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

public class SidebarController {
    private ActionInvoker invoker;

    public SidebarController() {
        invoker = State.getInvoker();
    }

    /**
     * Redirects the GUI to the View Donor Page
     * @param event when view button is clicked
     */
    @FXML
    private void goToViewDonor(ActionEvent event) {
        PageNavigator.loadPage(Page.VIEW_DONOR.getPath());
    }

    @FXML
    private void goToRegisterOrgans(ActionEvent event) {
        PageNavigator.loadPage(Page.REGISTER_ORGANS.getPath());
    }

    /**
     * Redirects the GUI to the History Page
     * @param event when view button is clicked
     */
    @FXML
    private void goToHistory(ActionEvent event) {
        PageNavigator.loadPage(Page.HISTORY.getPath());
    }

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

            JSONConverter.saveToFile(file);
            // TODO Make alert with number of donors saved
            HistoryItem save = new HistoryItem("SAVE", "The systems current state was saved.");
            JSONConverter.updateHistory(save, "action_history.json");
        } catch (URISyntaxException | IOException exc) {
            // TODO Make alert when save fails
            System.err.println(exc.getMessage());
        }
    }

    @FXML
    private void load(ActionEvent event) throws URISyntaxException {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Donors File");
            fileChooser.setInitialDirectory(
                    new File(Paths.get(AppUI.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toString())
            );
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
            File file = fileChooser.showOpenDialog(AppUI.getWindow());

            JSONConverter.loadFromFile(file);
            // TODO Make alert with number of donors loaded
            HistoryItem load = new HistoryItem("LOAD", "The systems state was loaded from " + file.getName());
            JSONConverter.updateHistory(load, "action_history.json");
        } catch (URISyntaxException | IOException exc) {
            // TODO Make alert when load fails
            System.err.println(exc.getMessage());
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        PageNavigator.loadPage(Page.LANDING.getPath());
    }

    @FXML
    private void undo(ActionEvent event) {
        invoker.undo();
    }

    @FXML
    private void redo(ActionEvent event) {
        invoker.redo();
    }
}
