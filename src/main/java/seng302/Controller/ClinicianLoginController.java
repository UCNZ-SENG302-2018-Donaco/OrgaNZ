package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import seng302.Clinician;
import seng302.State;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;
import sun.security.util.Password;

/**
 * Controller to handle the login of Clinician users. It allows them to log into a valid Clinician that exists on the
 * system, e.g. the default clinician.
 */
public class ClinicianLoginController {

	@FXML
	private TextField staffId;
	@FXML
	private PasswordField password;
	@FXML
	private int id;


	/**
	 * Navigates a user back to the Landing page.
	 * @param event user clicking the go back button
	 */
	@FXML
	private void goBack(ActionEvent event) {
		PageNavigator.loadPage(Page.LANDING.getPath());
	}

	/**
	 * Checks that the staff ID is an integer and is positive.
	 * @return true if the staffID is a positive integer. False otherwise.
	 */
	private boolean validStaffIDinput() {
		try {
			id = Integer.parseInt(staffId.getText()); // Staff ID
			if (id < -1) {
				return false;
			} else {
				return true;
			}
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	/**
	 * Alert to display that the StaffId doesn't exist.
	 */
	private void staffIdDoesntExistAlert() {
		PageNavigator.showAlert(Alert.AlertType.ERROR, "Staff ID Doesn't exist",
				"This staff ID does not exist in the system.");
	}

	/**
	 * Alert to display the password and StaffId doesn't match.
	 */
	private void staffIdPasswordMismatchAlert() {
		PageNavigator.showAlert(Alert.AlertType.ERROR, "Staff ID & Password do not match",
				"This staff ID does not exist in the system.");
	}

	/**
	 * Alert to display that an invalid StaffId has been entered.
	 */
	private void invalidStaffIdAlert() {
		PageNavigator.showAlert(Alert.AlertType.ERROR, "Invalid Staff ID",
				"Staff ID must be an integer.");
	}

	/**
	 * Alert to display a successful login.
	 */
	private void loginSuccessAlert() {
		PageNavigator.showAlert(Alert.AlertType.CONFIRMATION, "Success",
				"Successfully logged in.");
	}

	/**
	 * Checks if valid input for a staff member who exists in the systems ClinicianManager matches the supplied password.
	 * The user cannot be taken to the view_clinician page until valid parameters are supplied.
	 * @param event user clicking the login button
	 */
	@FXML
	private void signIn(ActionEvent event) {
		if (validStaffIDinput()) {
			if (State.getClinicianManager().getClinicianByStaffId(id) == null) {
				staffIdDoesntExistAlert();
			} else {
				Clinician clinician = State.getClinicianManager().getClinicianByStaffId(id);

				if (clinician.getPassword().equals(password.getText())) {

					State.setPageParam("currentUserType", "clinician");
					State.setPageParam("currentClinician", clinician);
					//PageNavigator.loadPage(Page.VIEW_DONOR.getPath());

					PageNavigator.loadPage(Page.VIEW_CLINICIAN.getPath());
					loginSuccessAlert();
				} else {
					staffIdPasswordMismatchAlert();
				}
			}
		} else {
			invalidStaffIdAlert();
		}
	}

}
