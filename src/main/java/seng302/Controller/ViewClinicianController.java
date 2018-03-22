package seng302.Controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import seng302.Actions.ModifyClinicianAction;
import seng302.Clinician;
import seng302.HistoryItem;
import seng302.State;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.PageNavigator;
import seng302.Utilities.Region;

import java.time.format.DateTimeFormatter;

public class ViewClinicianController implements SubController {
    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy\nh:mm:ss a");

    private MainController mainController;

    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        mainController.loadClinicianSidebar(sidebarPane);

        init();
    }

    @Override
    public MainController getMainController() {
        return this.mainController;
    }

    @FXML
    private Pane sidebarPane, idPane, inputsPane;
    @FXML
    private Label creationDate, lastModified, fnameLabel, lnameLabel, passwordLabel;
    @FXML
    private TextField staffID, fname, lname, mname, workAddress;
    @FXML
    private PasswordField password;
    @FXML
    private ChoiceBox<Region> region;

    private Clinician currentClinician;
    private String updatedPassword;

    @FXML
    private void initialize() {
        region.setItems(FXCollections.observableArrayList(Region.values()));
        inputsPane.setVisible(true);
    }

    private void init() {
        currentClinician = (Clinician) mainController.getPageParam("currentClinician");
        loadClinicianData();
    }

	/**
	 * Loads all of the currently logged in Clinician's details, except for their password.
	 */
	private void loadClinicianData() {
        System.out.println(currentClinician.toString());
        fname.setText(currentClinician.getFirstName());
        mname.setText(currentClinician.getMiddleName());
        lname.setText(currentClinician.getLastName());
        workAddress.setText(currentClinician.getWorkAddress());
        staffID.setText(String.valueOf(currentClinician.getStaffId()));
        region.setValue(currentClinician.getRegion());

        creationDate.setText(currentClinician.getCreated_on().toString());
        if (currentClinician.getModified_on() == null) {
            lastModified.setText("Not yet modified.");
        } else {
            lastModified.setText(currentClinician.getModified_on().format(dateTimeFormat));
        }
    }

    /**
     * Saves the changes a user makes to the viewed donor if all their inputs are valid. Otherwise the invalid fields
     * text turns red.
     */
    @FXML
    private void saveChanges() {
        if (checkMandatoryFields()) {
            updatedPassword = checkPassword();
            updateChanges();
            lastModified.setText(currentClinician.getModified_on().format(dateTimeFormat));
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
		if (lname.getText().equals("")) {
			lnameLabel.setTextFill(Color.RED);
			update = false;
		} else {
			lnameLabel.setTextFill(Color.BLACK);
		}
		return update;
	}


	/**
	 * Checks if the password has been update. If the PasswordField is left blank, the old password remains current.
	 * Otherwise the current password is updated to the newly entered value in the field.
	 * @return the users password.
	 */
	private String checkPassword() {
        if (password.getText().equals("")) {
            return currentClinician.getPassword();
        } else {
            return password.getText();
        }
    }

    /**
     * Records the changes updated as a ModifyDonorAction to trace the change in record.
     */
    private void updateChanges() {
        try {
            ModifyClinicianAction action = new ModifyClinicianAction(currentClinician);

            action.addChange("setFirstName", currentClinician.getFirstName(), fname.getText());
            action.addChange("setLastName", currentClinician.getLastName(), lname.getText());
            action.addChange("setMiddleName", currentClinician.getMiddleName(), mname.getText());
            action.addChange("setWorkAddress", currentClinician.getWorkAddress(), workAddress.getText());
            action.addChange("setPassword", currentClinician.getPassword(), updatedPassword);
            action.addChange("setRegion", currentClinician.getRegion(), region.getValue());

            State.getInvoker().execute(action);

            HistoryItem save = new HistoryItem("UPDATE CLINICIAN", "The Clinician's information was updated. New details are: " + currentClinician.getUpdateLog());
            JSONConverter.updateHistory(save, "action_history.json");

            PageNavigator.showAlert(Alert.AlertType.INFORMATION,
                    "Success",
                    String.format("Successfully updated %s.",
                            currentClinician.getFirstName()));

        } catch (NoSuchFieldException | NoSuchMethodException exc) {
            exc.printStackTrace();
        }
    }

}
