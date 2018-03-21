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
	private void goToCreateDonor(ActionEvent event) {
		PageNavigator.loadPage(Page.CREATE_USER.getPath());
	}

	/**
	 * Redirects the GUI to login page
	 * @param event when login button is clicked
	 */
	@FXML
	private void goToDonorLogin(ActionEvent event) {
		PageNavigator.loadPage(Page.LOGIN.getPath());
	}

	@FXML
	private void goToClinicianLogin(ActionEvent event) {
		PageNavigator.loadPage(Page.CLINICIAN_LOGIN.getPath());
	}

	@FXML
	private void goToCreateClinician(ActionEvent event) {
		PageNavigator.loadPage(Page.CREATE_CLINICIAN.getPath());
	}

	@FXML
	private void gotosearch(ActionEvent event) {
		PageNavigator.loadPage(Page.SEARCH.getPath());
	}
}
