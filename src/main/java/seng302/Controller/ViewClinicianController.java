package seng302.Controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

public class ViewClinicianController {
    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy\nh:mm:ss a");

    @FXML
    private Pane sidebarPane, idPane, inputsPane;
    @FXML
    private Label creationDate, lastModified, fnameLabel, lnameLabel, passwordLabel;
    @FXML
    private TextField staffID, fname, lname, mname, workAddress, password;
    @FXML
    private ChoiceBox<Region> region;

    private Clinician currentClinician;

    @FXML
    private void initialize() {
        ClinicianSidebarController.loadSidebar(sidebarPane);
        region.setItems(FXCollections.observableArrayList(Region.values()));

        inputsPane.setVisible(true);
        currentClinician = (Clinician) State.getPageParam("currentClinician");

        loadClinicianData();
    }

    private void loadClinicianData() {
        fname.setText(currentClinician.getFirstName());
        mname.setText(currentClinician.getMiddleName());
        lname.setText(currentClinician.getLastName());
        workAddress.setText(currentClinician.getWorkAddress());
        staffID.setText(String.valueOf(currentClinician.getStaffId()));
        region.setValue(currentClinician.getRegion());
        password.setText(currentClinician.getPassword());
        creationDate.setText(currentClinician.getCreated_on().format(dateTimeFormat));
        if (currentClinician.getModified_on() == null) {
            lastModified.setText("Not yet modified.");
        } else {
            lastModified.setText(currentClinician.getModified_on().format(dateTimeFormat));
        }
        // Need last modified.
    }

    /**
     * Saves the changes a user makes to the viewed donor if all their inputs are valid. Otherwise the invalid fields
     * text turns red.
     */
    @FXML
    private void saveChanges() {
        if (checkMandatoryFields()) {
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

        if (password.getText().equals("")) {
            passwordLabel.setTextFill(Color.RED);
            update = false;
        } else {
            passwordLabel.setTextFill(Color.BLACK);
        }

        return update;
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
            action.addChange("setPassword", currentClinician.getPassword(), password.getText());
            action.addChange("setRegion", currentClinician.getRegion(), region.getValue());

            State.getInvoker().execute(action);

            HistoryItem save = new HistoryItem("UPDATE CLINICIAN", "The Clinician's information was updated. New details are: " + currentClinician.getUpdateLog());
            JSONConverter.updateHistory(save, "action_history.json");

            PageNavigator.showAlert(Alert.AlertType.INFORMATION,
                    "Success",
                    String.format("Successfully updated clinician %s %s %s.",
                            currentClinician.getFirstName(), currentClinician.getMiddleName(),
                            currentClinician.getLastName()));

        } catch (NoSuchFieldException | NoSuchMethodException exc) {
            exc.printStackTrace();
        }
    }

}
