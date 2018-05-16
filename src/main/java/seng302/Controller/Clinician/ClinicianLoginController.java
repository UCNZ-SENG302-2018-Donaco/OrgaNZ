package seng302.Controller.Clinician;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import seng302.Clinician;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.HistoryItem;
import seng302.HistoryManager;
import seng302.State.ClinicianManager;
import seng302.State.State;
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
    @FXML
    private Button signInButton, goBackButton;

    private ClinicianManager clinicianManager;
    private int id;

    public ClinicianLoginController() {
        clinicianManager = State.getClinicianManager();
    }

    /**
     * Override so we can set the page title.
     * @param mainController The MainController
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Clinician login");
    }

    /**
     * Navigates a user back to the Landing page.
     */
    @FXML
    private void goBack() {
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
     */
    @FXML
    private void signIn() {
        if (validStaffIDinput()) {
            Clinician clinician = clinicianManager.getClinicianByStaffId(id);
            if (clinician == null) {
                staffIdDoesntExistAlert();
            } else if (!clinician.getPassword().equals(password.getText())) {
                staffIdPasswordMismatchAlert();
            } else {
                State.login(clinician);
                PageNavigator.loadPage(Page.VIEW_CLINICIAN, mainController);

                HistoryItem historyItem = new HistoryItem("LOGIN_CLINICIAN", String.format("Clinician %s %s logged in.",
                        clinician.getFirstName(), clinician.getLastName()));
                HistoryManager.INSTANCE.updateHistory(historyItem);
            }
        } else {
            invalidStaffIdAlert();
        }
    }
}
