package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import seng302.Clinician;
import seng302.State;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;

public class ClinicianLoginController implements SubController {

	private MainController mainController;

	@FXML
	private TextField staffId;
	@FXML
	private PasswordField password;
	@FXML
	private int id;


	@FXML
	private void goBack(ActionEvent event) {
		PageNavigator.loadPage(Page.LANDING.getPath(), mainController);
	}

	private boolean validStaffIDinput() {
		try {
			id = Integer.parseInt(staffId.getText()); // Staff ID
            return id >= -1;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	private void staffIdDoesntExistAlert() {
		PageNavigator.showAlert(Alert.AlertType.ERROR, "Staff ID Doesn't exist",
				"This staff ID does not exist in the system.");
	}

	private void staffIdPasswordMismatchAlert() {
		PageNavigator.showAlert(Alert.AlertType.ERROR, "Staff ID & Password do not match",
				"This staff ID does not exist in the system.");
	}

	private void invalidStaffIdAlert() {
		PageNavigator.showAlert(Alert.AlertType.ERROR, "Invalid Staff ID",
				"Staff ID must be an integer.");
	}

	private void loginSuccessAlert() {
		PageNavigator.showAlert(Alert.AlertType.INFORMATION, "Success",
				"Successfully logged in.");
	}

	@FXML
	private void signIn(ActionEvent event) {


		if (validStaffIDinput()) {
			if (State.getClinicianManager().getClinicianByStaffId(id) == null) {
				staffIdDoesntExistAlert();
			} else {
				Clinician clinician = State.getClinicianManager().getClinicianByStaffId(id);

				if (clinician.getPassword().equals(password.getText())) {

					mainController.setPageParam("currentUserType", "clinician");
					mainController.setPageParam("currentClinician", clinician);
					//PageNavigator.loadPage(Page.VIEW_DONOR.getPath());

					PageNavigator.loadPage(Page.VIEW_CLINICIAN.getPath(), mainController);
					loginSuccessAlert();
				} else {
					staffIdPasswordMismatchAlert();
				}
			}
		} else {
			invalidStaffIdAlert();
		}
	}

	@Override
	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

	@Override
	public MainController getMainController() {
		return mainController;
	}
}
