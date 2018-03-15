package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;

/**
 * Controls the Landing Screen View.
 */
public class LandingController {

	/**
	 * Redirects the GUI to create user page
	 * @param event when create user button is clicked
	 */
	@FXML
	private void goToCreateUser(ActionEvent event) {
		PageNavigator.loadPage(Page.CREATE_USER.getPath());
	}

	/**
	 * Redirects the GUI to login page
	 * @param event when login button is clicked
	 */
	@FXML
	private void goToLogin(ActionEvent event) {
		PageNavigator.loadPage(Page.LOGIN.getPath());
	}

	@FXML
	private void goToViewDonor(ActionEvent event) {
		PageNavigator.loadPage(Page.VIEW_DONOR.getPath());
	}
}
