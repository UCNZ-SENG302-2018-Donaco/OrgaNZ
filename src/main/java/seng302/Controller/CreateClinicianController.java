package seng302.Controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import seng302.Clinician;
import seng302.ClinicianManager;
import seng302.HistoryItem;
import seng302.State;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;
import seng302.Utilities.Region;

public class CreateClinicianController implements SubController {

	private MainController mainController;

	@FXML
	private TextField id, fname, lname, mname, staffId, workAddress, password;

	@FXML
	private Label fnameLabel, mnameLabel, lnameLabel, staffIdLabel, workAddressLabel, regionLabel, passwordLabel;

	@FXML
	private ChoiceBox<Region> region;

	private ClinicianManager clinicianManager;

	@FXML
	private void initialize() {
		clinicianManager = State.getClinicianManager();
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

				mainController.setPageParam("currentUserType", "clinician");
				mainController.setPageParam("currentClinician", clinician);

				PageNavigator.showAlert(Alert.AlertType.INFORMATION, "Clinician created",
						String.format("Successfully created clinician with Staff ID %s.",
						staffId.getText()));
				PageNavigator.loadPage(Page.VIEW_CLINICIAN.getPath(), mainController);
			}
		}
	}


	@FXML
	private void goBack(ActionEvent event) {
		PageNavigator.loadPage(Page.LANDING.getPath(), mainController);
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
