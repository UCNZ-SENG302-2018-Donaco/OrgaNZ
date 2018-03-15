package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;

public class SideBarController {


    public Button historyBtn;
    public Button registerOrgansBtn;
    public Button viewProfileBtn;
    public Button LoadBtn;
    public Button BmiBtn;

    /**
     * Redirects the GUI to the View Donor Page
     * @param event when view button is clicked
     */
    @FXML
    private void goToViewDonor(ActionEvent event) {
        PageNavigator.loadPage(Page.VIEW_DONOR.getPath());
    }

    /**
     * Redirects the GUI to the History Page
     * @param event when view button is clicked
     */
    @FXML
    private void goToHistory(ActionEvent event) {
        PageNavigator.loadPage(Page.HISTORY.getPath());
    }


}
