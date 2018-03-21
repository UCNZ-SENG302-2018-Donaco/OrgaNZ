package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import seng302.State;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;

/**
 * Controller for the login page.
 */
public class LoginController {
    @FXML
    private TextField idTextField;

    /**
     * Attempts to login with the given donor id.
     * If successful, redirects to the view donor page for that donor.
     * @param event When the sign in button is clicked.
     */
    @FXML
    private void signIn(ActionEvent event) {
        State.setPageParam("currentUserId", Integer.parseInt(idTextField.getText()));
        State.setPageParam("currentUserType", "donor");
        PageNavigator.loadPage(Page.VIEW_DONOR.getPath());
    }

    /**
     * Redirects the UI back to the landing page.
     * @param event When the back button is clicked.
     */
    @FXML
    private void goBack(ActionEvent event) {
        PageNavigator.loadPage(Page.LANDING.getPath());
    }
}
