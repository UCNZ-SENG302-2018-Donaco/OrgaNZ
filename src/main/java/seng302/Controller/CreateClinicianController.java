package seng302.Controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.control.PasswordField;
import seng302.*;
import seng302.Actions.ActionInvoker;
import seng302.Utilities.*;

/**
 * This controller provides the user with an interface allowing them to enter clinician details. This creates a
 * clinician login for them and takes them to the view clinician page.
 */
public class CreateClinicianController {

	@FXML
	private TextField id, fname, lname, mname, staffId, workAddress;
	@FXML
	private PasswordField password;
	@FXML
	private Label fnameLabel, mnameLabel, lnameLabel, staffIdLabel, regionLabel, passwordLabel;
	@FXML
	private ChoiceBox<Region> region;

	private ClinicianManager clinicianManager;
	private ActionInvoker invoker;

	/**
	 * Initialize the controller to display appropriate items.
	 */
	@FXML
	private void initialize() {
		clinicianManager = State.getClinicianManager();
		invoker = State.getInvoker();
		region.setItems(FXCollections.observableArrayList(Region.values()));
	}

	/**
	 * Checks that all mandatory fields have had valid input correctly input. Invalid input results in the text beside
	 * the instigating field turning red.
	 * @return if all mandatory fields have valid input. False otherwise
	 */
	private boolean checkMandatoryFields() {
		boolean update = true;
		if (fname.getText().equals("")) {   // First name
			fnameLabel.setTextFill(Color.RED);
			update = false;
		} else {
			fnameLabel.setTextFill(Color.BLACK);
		}

		if (lname.getText().equals("")) {   // Last name
			lnameLabel.setTextFill(Color.RED);
			update = false;
		} else {
			lnameLabel.setTextFill(Color.BLACK);
		}

		try {
			int id = Integer.parseInt(staffId.getText()); // Staff ID
			if( id < -1) {
				staffIdLabel.setTextFill(Color.RED);
				update = false;
			} else {
				staffIdLabel.setTextFill(Color.BLACK);
			}

		} catch (NumberFormatException ex) {
			staffIdLabel.setTextFill(Color.RED);
		}

		if (password.getText().equals("")) { // Password
			passwordLabel.setTextFill(Color.RED);
			update = false;
		} else {
			passwordLabel.setTextFill(Color.BLACK);
		}
		return update;
	}


	/**
	 * Creates a Clinician if all of the fields have valid input. These are recorded in State and a success message is
	 * shown. The user is then taken to the view clinician page. If the StaffId already exists in the current state,
	 * an error alert is shown.
	 */
	@FXML
	private void createUser() {
		if (checkMandatoryFields()) {
			System.out.println("all valid");

			if (clinicianManager.collisionExists(Integer.parseInt(staffId.getText()))) {
				staffIdLabel.setTextFill(Color.RED);
				PageNavigator.showAlert(Alert.AlertType.ERROR, "Staff ID in Use",
					"This staff ID is already in use and has an existing login.");
			} else {
				Clinician clinician = new Clinician(fname.getText(), mname.getText(), lname.getText(),
						workAddress.getText(),region.getValue(), Integer.parseInt(staffId.getText()), password.getText());
				clinicianManager.addDonor(clinician);

				HistoryItem save = new HistoryItem("CREATE CLINICIAN", "Clinician " + fname.getText() + " " + lname.getText() + " with staff ID " + staffId.getText() + " Created.");
				JSONConverter.updateHistory(save, "action_history.json");

				State.setPageParam("currentUserType", "clinician");
				State.setPageParam("currentClinician", clinician);

				PageNavigator.showAlert(Alert.AlertType.CONFIRMATION, "Clinician created",
						String.format("Successfully created clinician with Staff ID %s.",
						staffId.getText()));
				PageNavigator.loadPage(Page.VIEW_CLINICIAN.getPath());
			}
		}
	}


	/**
	 * Takes the user back to the landing page.
	 * @param event user clicks the go back button
	 */
	@FXML
	private void goBack(ActionEvent event) {
		PageNavigator.loadPage(Page.LANDING.getPath());
	}
}
