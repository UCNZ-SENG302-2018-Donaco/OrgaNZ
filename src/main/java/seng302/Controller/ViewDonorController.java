package seng302.Controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import seng302.State;
import seng302.Donor;
import seng302.Utilities.Gender;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;

import java.io.IOException;

public class ViewDonorController {
    @FXML
    private Pane sidebarPane;
    @FXML
	private Pane inputsPane;
	@FXML
	private Label creationDate, lastModified, noDonorLabel;
	@FXML
	private TextField id, fname, lname, mname, height, weight, btype, address, region;
	@FXML
	private DatePicker dob, dod;
	@FXML
    private ChoiceBox<Gender> gender;

	@FXML
    private void initialize() {
	    // IMPORTING SIDEBAR //
	    try {
            VBox sidebar = FXMLLoader.load(getClass().getResource(Page.SIDEBAR.getPath()));
            sidebarPane.getChildren().setAll(sidebar);
        } catch (IOException exc) {
            System.err.println("Couldn't load sidebar from fxml file.");
            exc.printStackTrace();
        }
        // FINISHED IMPORT //

	    gender.setItems(FXCollections.observableArrayList(Gender.values()));
	    setFieldsDisabled(true);
    }

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

		//int id_value = new Integer(id.getText());
		//displayInformation(id_value);


		Donor donor = State.getManager().getDonorByID(id_value);
		if (donor == null) {
			noDonorLabel.setVisible(true);
            setFieldsDisabled(true);
		} else {
			noDonorLabel.setVisible(false);
            setFieldsDisabled(false);

			fname.setText(donor.getFirstName());
			lname.setText(donor.getLastName());
			mname.setText(donor.getMiddleName());
			dob.setValue(donor.getDateOfBirth());
			dod.setValue(donor.getDateOfDeath());
			gender.setValue(donor.getGender());
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
	
    private void setFieldsDisabled(boolean disabled) {
        inputsPane.setVisible(!disabled);
    }

	@FXML
	private void saveChanges(ActionEvent event) {

	}

	// Add something to view organs being donated.
}
