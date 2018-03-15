package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
}
