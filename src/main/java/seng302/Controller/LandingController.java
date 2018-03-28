package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import seng302.Utilities.Enums.Page;
import seng302.Utilities.View.PageNavigator;

/**
 * Controls the Landing Screen View.
 */
public class LandingController extends SubController {

	/**
	 * Redirects the GUI to create donor page
	 * @param event when create user button is clicked
	 */
	@FXML
	private void goToCreateDonor(ActionEvent event) {
		PageNavigator.loadPage(Page.CREATE_USER, mainController);
	}

	/**
	 * Redirects the GUI to donor login page
	 * @param event when login button is clicked
	 */
	@FXML
	private void goToDonorLogin(ActionEvent event) {
		PageNavigator.loadPage(Page.LOGIN, mainController);
	}

    /**
     * Redirects the GUI to clinician login page
     * @param event when login button is clicked
     */
	@FXML
	private void goToClinicianLogin(ActionEvent event) {
		PageNavigator.loadPage(Page.CLINICIAN_LOGIN, mainController);
	}

    /**
     * Redirects the GUI to create clinician page
     * @param event when login button is clicked
     */
	@FXML
	private void goToCreateClinician(ActionEvent event) {
		PageNavigator.loadPage(Page.CREATE_CLINICIAN, mainController);
	}
}
