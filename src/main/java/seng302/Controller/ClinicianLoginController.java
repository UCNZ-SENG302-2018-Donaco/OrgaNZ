package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import seng302.Clinician;
import seng302.State;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;
import sun.security.util.Password;

public class ClinicianLoginController {

	@FXML
	private TextField staffID;
	@FXML
	private PasswordField password;


	@FXML
	private void goBack(ActionEvent event) {
		PageNavigator.loadPage(Page.LANDING.getPath());
	}

	private boolean validStaffIDinput() {
		try {
			Integer.parseInt(staffID.getText());
		}
	}

	@FXML
	private void signIn(ActionEvent event) {
		if (validStaffIDinput()) {

		}

		Clinician clinician = State.getClinicianManager().getClinicianByStaffId(staffID.getText());
		if (clinician != null && clinician.getPassword().equals(password.getText())) {

		}

		//State.setPageParam("currentUserId", Integer.parseInt(idTextField.getText()));
		State.setPageParam("currentUserType", "donor");


		PageNavigator.loadPage(Page.VIEW_DONOR.getPath());}
}
