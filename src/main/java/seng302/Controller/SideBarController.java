package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;

public class SideBarController {

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
    }

    @FXML
    private void load(ActionEvent event) {
    }

    @FXML
    private void logout(ActionEvent event) {
    }
}
