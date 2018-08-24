package com.humanharvest.organz.controller.clinician;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.JSONConverter;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.regex.Pattern;

/**
 * Controller to handle the login of staff.
 * It allows them to log into a valid staff account (clinician or administrator) that exists on the
 * system, e.g. the default clinician.
 */
public class StaffLoginController extends SubController {
    private static final Pattern IS_NUMBER = Pattern.compile("[0-9]+");

    @FXML
    private TextField staffId;
    @FXML
    private PasswordField password;

    /**
     * Override so we can set the page title.
     *
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
     *
     * @return true if the staffID is a positive integer. False otherwise.
     */
    private boolean isValidStaffIdInput() {
        String idString = staffId.getText();
        return idString != null && !idString.isEmpty();
    }

    /**
     * Alert to display that an invalid StaffId has been entered.
     */
    private void invalidStaffIdAlert() {
        PageNavigator.showAlert(AlertType.ERROR, "Invalid Staff ID",
                "Staff ID is invalid", mainController.getStage());
    }

    /**
     * Finds if there is a clinician with the staff id and password input and logs them in
     * Gives an alert if the password does not match the staff id
     */
    private void signInClinician() {
        int id = Integer.parseInt(staffId.getText());
        Clinician clinician;

        try {
            clinician = State.getAuthenticationManager().loginClinician(id, password.getText());
        } catch (AuthenticationException e) {
            PageNavigator.showAlert(AlertType.ERROR, "Invalid login", e.getLocalizedMessage(), mainController.getStage());
            return;
        }

        PageNavigator.loadPage(Page.VIEW_CLINICIAN, mainController);

        HistoryItem save = new HistoryItem("LOGIN_STAFF", String.format("Clinician %s %s logged in.",
                clinician.getFirstName(), clinician.getLastName()));
        JSONConverter.updateHistory(save, "action_history.json");
    }

    /**
     * Finds if there is an administrator with the username and password input and logs them in
     * Gives an alert if the password does not match the username
     */
    private void signInAdministrator() {
        Administrator administrator;
        try {
            administrator = State.getAuthenticationManager().loginAdministrator(staffId.getText(), password.getText());
        } catch (AuthenticationException e) {
            PageNavigator.showAlert(AlertType.ERROR, "Invalid login", e.getLocalizedMessage(), mainController.getStage());
            return;
        }

        PageNavigator.loadPage(Page.SEARCH, mainController);
        HistoryItem save = new HistoryItem("LOGIN_STAFF", String.format("Administrator %s logged in.",
                administrator.getUsername()));
        JSONConverter.updateHistory(save, "action_history.json");
    }

    /**
     * Checks if the staff id is valid and checks that the username and password is correct
     * The user cannot be logged in until valid parameters are supplied.
     */
    @FXML
    private void signIn() {
        if (isValidStaffIdInput()) {
            if (IS_NUMBER.matcher(staffId.getText()).matches()) {
                signInClinician();
            } else {
                signInAdministrator();
            }
        } else {
            invalidStaffIdAlert();
        }
    }
}
