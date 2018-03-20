package seng302.Controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import seng302.State;
import javafx.scene.paint.Color;
import seng302.Actions.ModifyDonorAction;
import seng302.Donor;
import seng302.Utilities.BloodType;
import seng302.Utilities.Gender;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;

import java.time.LocalDate;

public class ViewDonorController {
    @FXML
    private Pane sidebarPane;
    @FXML
	private Pane idPane;
    @FXML
	private Pane inputsPane;
	@FXML
	private Label creationDate, lastModified, noDonorLabel, fnameLabel, mnameLabel, lnameLabel, dobLabel, dodLabel,
	heightLabel, weightLabel, btypeLabel, ageDisplayLabel, ageLabel, BMILabel;
	@FXML
	private TextField id, fname, lname, mname, height, weight, btype, address, region;
	@FXML
	private DatePicker dob, dod;
	@FXML
    private ChoiceBox<Gender> gender;

	private Donor viewedDonor;
	private BloodType bloodType;


	@FXML
    private void initialize() {
        SidebarController.loadSidebar(sidebarPane);

	    gender.setItems(FXCollections.observableArrayList(Gender.values()));
		setFieldsDisabled(true);

		String currentUserType = (String) State.getPageParam("currentUserType");
		if (currentUserType == null) {
			Integer viewUserId = (Integer) State.getPageParam("viewUserId");
			State.removePageParam("viewUserId");
			if (viewUserId != null) {
				id.setText(viewUserId.toString());
				searchDonor();
			}
		} else if (currentUserType.equals("donor")) {
			Integer currentUserId = (Integer) State.getPageParam("currentUserId");
			if (currentUserId != null) {
				id.setText(currentUserId.toString());
				searchDonor();
			}
			idPane.setVisible(false);
			idPane.setManaged(false);
		}
    }

	/**
	 * Searches for a donor based off the id number supplied in the text field. The users fields will be displayed if
	 * this user exists, otherwise an error message will display.
	 */
	@FXML
	private void searchDonor() {
		int id_value;
		try {
			id_value = Integer.parseInt(id.getText());
		} catch (Exception e) {
			noDonorLabel.setVisible(true);
			setFieldsDisabled(true);
			return;
		}

		viewedDonor = State.getManager().getDonorByID(id_value);
		if (viewedDonor == null) {
			noDonorLabel.setVisible(true);
            setFieldsDisabled(true);
		} else {
			noDonorLabel.setVisible(false);
            setFieldsDisabled(false);

			fname.setText(viewedDonor.getFirstName());
			lname.setText(viewedDonor.getLastName());
			mname.setText(viewedDonor.getMiddleName());
			dob.setValue(viewedDonor.getDateOfBirth());
			dod.setValue(viewedDonor.getDateOfDeath());
			gender.setValue(viewedDonor.getGender());
			height.setText(String.valueOf(viewedDonor.getHeight()));
			weight.setText(String.valueOf(viewedDonor.getWeight()));
			if(viewedDonor.getBloodType() != null) {btype.setText(viewedDonor.getBloodType().toString());}
			address.setText(viewedDonor.getCurrentAddress());
			if(viewedDonor.getRegion() != null) {region.setText(viewedDonor.getRegion().toString());}

			creationDate.setText(viewedDonor.getCreationdate().toString());
			lastModified.setText(viewedDonor.getModified_on().toString());

			displayBMI();
			displayAge();
		}
	}

	/**
	 * Disables the view of user fields as these will all be irrelevant to the id number supplied if no such donor
	 * exists with this id. Or sets it to visible so that the user can see all fields relevant to the donor.
	 * @param disabled the state of the pane.
	 */
    private void setFieldsDisabled(boolean disabled) {
        inputsPane.setVisible(!disabled);
    }

	/**
	 * Saves the changes a user makes to the viewed donor if all their inputs are valid. Otherwise the invalid fields
	 * text turns red.
	 */
	@FXML
	private void saveChanges() {
		if (checkMandatoryFields() && checkNonMandatoryFields()) {
			updateChanges();
			displayBMI();
			displayAge();
		}
	}

	/**
	 * Checks that all mandatory fields have valid arguments inside. Otherwise display red text on the invalidly entered
	 * labels.
	 * @return true if all mandatory fields have valid input.
	 */
	private boolean checkMandatoryFields() {
		boolean update = true;
		if (fname.getText().equals("")) {
			fnameLabel.setTextFill(Color.RED);
			update = false;
		} else {
			fnameLabel.setTextFill(Color.BLACK);
		}

		if (lname.getText().equals("")) {
			lnameLabel.setTextFill(Color.RED);
			update = false;
		} else {
			lnameLabel.setTextFill(Color.BLACK);
		}
		if (dob.getValue() == null || dob.getValue().isAfter(LocalDate.now())) {
			dobLabel.setTextFill(Color.RED);
			update = false;
		} else {
			dobLabel.setTextFill(Color.BLACK);
		}
		return update;
	}


	/**
	 * Checks that non mandatory fields have either valid input, or no input. Otherwise red text is shown.
	 * @return true if all non mandatory fields have valid/no input.
	 */
	private boolean checkNonMandatoryFields() {
		boolean update = true;
		if (dod.getValue() == null || dod.getValue().isBefore(LocalDate.now())) {
			dodLabel.setTextFill(Color.BLACK);
		} else {
			dodLabel.setTextFill(Color.RED);
			update = false;
		}

		try {
			double h = Double.parseDouble(height.getText());
			if( h < 0) {
				heightLabel.setTextFill(Color.RED);
				update = false;
			} else {
				heightLabel.setTextFill(Color.BLACK);
			}

		} catch (NumberFormatException ex) {
			heightLabel.setTextFill(Color.RED);
		}

		try {
			double w = Double.parseDouble(weight.getText());
			if( w < 0) {
				weightLabel.setTextFill(Color.RED);
				update = false;
			} else {
				weightLabel.setTextFill(Color.BLACK);
			}

		} catch (NumberFormatException ex) {
			weightLabel.setTextFill(Color.RED);
			update = false;
		}
		if(!btype.getText().equals("")) {
			try {
				bloodType = BloodType.fromString(btype.getText());
				btypeLabel.setTextFill(Color.BLACK);

			} catch(IllegalArgumentException ex){
				btypeLabel.setTextFill(Color.RED);
				update = false;
			}
		}
		return update;
	}

	/**
	 * Records the changes updated as a ModifyDonorAction to trace the change in record.
	 */
	private void updateChanges() {
		try {
			ModifyDonorAction action = new ModifyDonorAction(viewedDonor);

			action.addChange("setFirstName", viewedDonor.getFirstName(), fname.getText());
			action.addChange("setLastName", viewedDonor.getLastName(), lname.getText());
			action.addChange("setMiddleName", viewedDonor.getMiddleName(), mname.getText());
			action.addChange("setDateOfBirth", viewedDonor.getDateOfBirth(), dob.getValue());
			action.addChange("setDateOfDeath", viewedDonor.getDateOfDeath(), dod.getValue());
			action.addChange("setGender", viewedDonor.getGender(), gender.getValue());
			action.addChange("setHeight", viewedDonor.getHeight(), Double.parseDouble(height.getText()));
			action.addChange("setWeight", viewedDonor.getWeight(), Double.parseDouble(weight.getText()));
			action.addChange("setBloodType", viewedDonor.getBloodType(), bloodType);
			action.addChange("setCurrentAddress", viewedDonor.getCurrentAddress(), address.getText());
			action.addChange("setRegion", viewedDonor.getRegion(), region.getText());

		    State.getInvoker().execute(action);

		    PageNavigator.showAlert(Alert.AlertType.INFORMATION,
				"Success",
				String.format("Successfully updated donor %s %s %s %d.",
						viewedDonor.getFirstName(), viewedDonor.getMiddleName(),
						viewedDonor.getLastName(), viewedDonor.getUid()));

		} catch (NoSuchFieldException | NoSuchMethodException exc) {
			exc.printStackTrace();
		}
	}

	/**
	 * Displays the currently viewed donors BMI.
	 */
	private void displayBMI() {
		if (viewedDonor.getDateOfDeath() == null) {
			BMILabel.setText(String.format("%.01f", viewedDonor.getBMI()));
		} else {
			BMILabel.setText(String.format("%.01f", viewedDonor.getBMI()));
		}
	}

	/**
	 * Displays either the current age, or age at death of the donor depending on if the date of death field has been
	 * filled in.
	 */
	private void displayAge() {
		if (viewedDonor.getDateOfDeath() == null) {
			ageDisplayLabel.setText("Age");
		} else {
			ageDisplayLabel.setText("Age at Death");
		}
		ageLabel.setText(String.valueOf(viewedDonor.getAge()));
	}

	@FXML
	public void viewOrgansForDonor() {
		State.setPageParam("viewUserId", viewedDonor.getUid());
		PageNavigator.loadPage(Page.REGISTER_ORGANS.getPath());
	}
}
