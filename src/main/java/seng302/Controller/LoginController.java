package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import seng302.State;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;

public class LoginController {

    public TextField idTextField;


    @FXML
    private void goBack(ActionEvent event) {
        PageNavigator.loadPage(Page.LANDING.getPath());
    }

    @FXML
    private void signIn(ActionEvent event) {
        State.setPageParam("currentUserId", Integer.parseInt(idTextField.getText()));
        State.setPageParam("currentUserType", "donor");
        PageNavigator.loadPage(Page.VIEW_DONOR.getPath());}
}
