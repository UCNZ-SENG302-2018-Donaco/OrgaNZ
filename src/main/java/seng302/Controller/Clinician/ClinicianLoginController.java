package seng302.Controller.Clinician;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import seng302.Clinician;
import seng302.Controller.SubController;
import seng302.HistoryItem;
import seng302.State.ClinicianManager;
import seng302.State.Session;
import seng302.State.State;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;

/**
 * Controller to handle the login of Clinician users. It allows them to log into a valid Clinician that exists on the
 * system, e.g. the default clinician.
 */
public class ClinicianLoginController extends SubController {

    @FXML
    private TextField staffId;
    @FXML
    private PasswordField password;

    private ClinicianManager clinicianManager;
    private int id;

    public ClinicianLoginController() {
        clinicianManager = State.getClinicianManager();
    }

    /**
     * Navigates a user back to the Landing page.
     * @param event user clicking the go back button
     */
    @FXML
    private void goBack(ActionEvent event) {
        PageNavigator.loadPage(Page.LANDING, mainController);
    }

    /**
     * Checks that the staff ID is an integer and is positive.
     * @return true if the staffID is a positive integer. False otherwise.
     */
    private boolean validStaffIDinput() {
        try {
            id = Integer.parseInt(staffId.getText()); // Staff ID
            return id >= -1;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Alert to display that the StaffId doesn't exist.
     */
    private void staffIdDoesntExistAlert() {
        PageNavigator.showAlert(Alert.AlertType.ERROR, "Staff ID Doesn't exist",
                "This staff ID does not exist in the system.");
    }

    /**
     * Alert to display the password and StaffId doesn't match.
     */
    private void staffIdPasswordMismatchAlert() {
        PageNavigator.showAlert(Alert.AlertType.ERROR, "Staff ID & Password do not match",
                "This staff ID does not exist in the system.");
    }

    /**
     * Alert to display that an invalid StaffId has been entered.
     */
    private void invalidStaffIdAlert() {
        PageNavigator.showAlert(Alert.AlertType.ERROR, "Invalid Staff ID",
                "Staff ID must be an integer.");
    }

    /**
     * Checks if valid input for a staff member who exists in the systems ClinicianManager matches the supplied
     * password.
     * The user cannot be taken to the view_clinician page until valid parameters are supplied.
     * @param event user clicking the login button
     */
    @FXML
    private void signIn(ActionEvent event) {
        if (validStaffIDinput()) {
            Clinician clinician = clinicianManager.getClinicianByStaffId(id);
            if (clinician == null) {
                staffIdDoesntExistAlert();
            } else if (!clinician.getPassword().equals(password.getText())) {
                staffIdPasswordMismatchAlert();
            } else {
                State.login(Session.UserType.CLINICIAN, clinician);
                PageNavigator.loadPage(Page.VIEW_CLINICIAN, mainController);

                HistoryItem save = new HistoryItem("LOGIN_CLINICIAN", String.format("Clinician %s %s logged in.",
                        clinician.getFirstName(), clinician.getLastName()));
                JSONConverter.updateHistory(save, "action_history.json");
            }
        } else {
            invalidStaffIdAlert();
        }
    }
}
