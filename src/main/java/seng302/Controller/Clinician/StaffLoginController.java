package seng302.Controller.Clinician;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import seng302.Administrator;
import seng302.Clinician;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.HistoryItem;
import seng302.State.AdministratorManager;
import seng302.State.ClinicianManager;
import seng302.State.State;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;

/**
 * Controller to handle the login of staff.
 * It allows them to log into a valid staff account (clinician or administrator) that exists on the
 * system, e.g. the default clinician.
 */
public class StaffLoginController extends SubController {

    @FXML
    private TextField staffId;
    @FXML
    private PasswordField password;

    private ClinicianManager clinicianManager;
    private AdministratorManager administratorManager;

    public StaffLoginController() {
        clinicianManager = State.getClinicianManager();
        administratorManager = State.getAdministratorManager();
    }

    /**
     * Override so we can set the page title.
     * @param mainController The MainController
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Staff login");
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
    private boolean validStaffIdInput() {

        String idString = staffId.getText();
        return !(idString == null);
    }

    /**
     * Alert to display that the StaffId doesn't exist.
     */
    private void staffIdDoesntExistAlert() {
        PageNavigator.showAlert(Alert.AlertType.ERROR, "Invalid login",
                "This staff ID does not exist in the system.");
    }

    /**
     * Alert to display the password and StaffId doesn't match.
     */
    private void staffIdPasswordMismatchAlert() {
        PageNavigator.showAlert(Alert.AlertType.ERROR, "Invalid login",
                "The password is incorrect.");
    }

    /**
     * Alert to display that an invalid StaffId has been entered.
     */
    private void invalidStaffIdAlert() {
        PageNavigator.showAlert(Alert.AlertType.ERROR, "Invalid Staff ID",
                "Staff ID is invalid");
    }

    /**
     * Finds if there is a clinician with the staff id and password input and logs them in
     * Gives an alert if the password does not match the staff id
     */
    private void signInClinician() {
        int id = Integer.parseInt(staffId.getText());
        Clinician clinician = clinicianManager.getClinicianByStaffId(id);

        if (clinician == null) {
            staffIdDoesntExistAlert();

        } else if (!clinician.getPassword().equals(password.getText())) {
            staffIdPasswordMismatchAlert();

        } else {
            State.login(clinician);
            PageNavigator.loadPage(Page.VIEW_CLINICIAN, mainController);

            HistoryItem save = new HistoryItem("LOGIN_STAFF", String.format("Clinician %s %s logged in.",
                    clinician.getFirstName(), clinician.getLastName()));
            JSONConverter.updateHistory(save, "action_history.json");
        }
    }

    /**
     * Finds if there is an administrator with the username and password input and logs them in
     * Gives an alert if the password does not match the username
     */
    private void signInAdministrator() {
        Administrator administrator = administratorManager.getAdministratorByUsername(staffId.getText());

        if (administrator == null) {
            staffIdDoesntExistAlert();

        } else if (!administrator.getPassword().equals(password.getText())) {
            staffIdPasswordMismatchAlert();

        } else {
            State.login(administrator);
            PageNavigator.loadPage(Page.SEARCH, mainController);
            HistoryItem save = new HistoryItem("LOGIN_STAFF", String.format("Administrator %s logged in.",
                    administrator.getUsername()));
            JSONConverter.updateHistory(save, "action_history.json");
        }
    }

    /**
     * Checks if the staff id is valid and checks that the username and password is correct
     * The user cannot be logged in until valid parameters are supplied.
     */
    @FXML
    private void signIn() {

        if(validStaffIdInput()) {
            if (staffId.getText().matches("[0-9]+")) {
                signInClinician();
            } else {
                signInAdministrator();
            }
        } else {
            invalidStaffIdAlert();
        }
    }
}
