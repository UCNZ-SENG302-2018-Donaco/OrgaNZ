package seng302.Controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import seng302.*;
import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.CreateDonorAction;
import seng302.Utilities.Gender;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;
import seng302.Utilities.Region;

public class CreateClinicianController {

	@FXML
	private TextField id, fname, lname, mname, staffId, workAddress, password;

	@FXML
	private Label fnameLabel, mnameLabel, lnameLabel, staffIdLabel, workAddressLabel, regionLabel, passwordLabel;

	@FXML
	private ChoiceBox<Region> region;

	private ClinicianManager clinicianManager;
	private ActionInvoker invoker;

	@FXML
	private void initialize() {
		clinicianManager = State.getClinicianManager();
		invoker = State.getInvoker();
		region.setItems(FXCollections.observableArrayList(Region.values()));
	}

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

	private void createClinician() {

	}


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
				PageNavigator.showAlert(Alert.AlertType.CONFIRMATION, "Clinician created",
						String.format("Successfully created clinician with Staff ID %s.",
						staffId.getText()));
				PageNavigator.loadPage(Page.VIEW_DONOR.getPath());
			}
		}
	}


	@FXML
	private void goBack(ActionEvent event) {
		PageNavigator.loadPage(Page.LANDING.getPath());
	}
}
