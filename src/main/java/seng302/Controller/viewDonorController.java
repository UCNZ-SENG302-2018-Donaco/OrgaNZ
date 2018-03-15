package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import seng302.App;

import java.io.IOException;

public class viewDonorController {

	@FXML
	private AnchorPane donorValues;

	@FXML
	private Button search_button;

	@FXML
	private TextField id, fname, lname, mname, dob, dod, gender, height, weight, btype, address, region;

	@FXML
	private void searchDonor() {
		int id_value = new Integer(id.getText());

		donorValues.setVisible(true);
	}
}
