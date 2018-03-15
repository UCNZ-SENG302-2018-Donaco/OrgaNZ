package seng302.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import seng302.AppUI;
import seng302.Donor;

public class viewDonorController {

	@FXML
	private AnchorPane donorValues;

	@FXML
	private Label creationDate, lastModified, noDonorLabel;

	@FXML
	private TextField id, fname, lname, mname, dob, dod, gender, height, weight, btype, address, region;

	@FXML
	private void searchDonor() {
		int id_value;
		try {
			id_value = Integer.parseInt(id.getText());
		} catch (Exception e) {
			noDonorLabel.setVisible(true);
			donorValues.setVisible(false);
			return;
		}

		//int id_value = new Integer(id.getText());
		//displayInformation(id_value);


		Donor donor = AppUI.getManager().getDonorByID(id_value);
		if (donor == null) {
			noDonorLabel.setVisible(true);
			donorValues.setVisible(false);
		} else {
			noDonorLabel.setVisible(false);
			donorValues.setVisible(true);

			fname.setText(donor.getFirstName());
			lname.setText(donor.getLastName());
			mname.setText(donor.getMiddleName());
			dob.setText(donor.getDateOfBirth().toString());
			//dod.setText(donor.getDateOfDeath().toString());
			//gender.setText(donor.getGender().toString());
			//height.setText(String.valueOf(donor.getHeight()));
			//weight.setText(String.valueOf(donor.getHeight()));
			//btype.setText(donor.getBloodType().toString());
			//address.setText(donor.getCurrentAddress());
			//region.setText(donor.getRegion());

			creationDate.setText(donor.getCreationdate().toString());
			//creationDate.setVisible(true);
			lastModified.setText(donor.getModified_on().toString());
			//lastModified.setVisible(true);
		}
	}

	@FXML
	private void saveChanges() {

	}

	// Add something to view organs being donated.
}
