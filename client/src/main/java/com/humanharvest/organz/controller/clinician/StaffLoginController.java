package com.humanharvest.organz.controller.clinician;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.JSONConverter;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;

/**
 * Controller to handle the login of staff.
 * It allows them to log into a valid staff account (clinician or administrator) that exists on the
 * system, e.g. the default clinician.
 */
public class StaffLoginController extends SubController {

    private static final Pattern IS_NUMBER = Pattern.compile("[0-9]+");
    private static final Logger LOGGER = Logger.getLogger(StaffLoginController.class.getName());

    @FXML
    private TextField staffId;
    @FXML
    private PasswordField password;

    /**
     * Checks that the staff ID is an integer and is positive.
     *
     * @return true if the staffID is a positive integer. False otherwise.
     */
    private static boolean isValidStaffIdInput(String username) {
        return username != null && !username.isEmpty();
    }

    /**
     * Alert to display that an invalid StaffId has been entered.
     */
    private static void invalidStaffIdAlert(MainController mainController) {
        PageNavigator.showAlert(AlertType.ERROR, "Invalid Staff ID",
                "Staff ID is invalid", mainController.getStage());
    }

    /**
     * Finds if there is a clinician with the staff id and password input and logs them in
     * Gives an alert if the password does not match the staff id
     */
    private static void signInClinician(String username, String password, MainController mainController) {
        int id = Integer.parseInt(username);
        Clinician clinician;

        try {
            clinician = State.getAuthenticationManager().loginClinician(id, password);
        } catch (AuthenticationException | ServerRestException e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            PageNavigator.showAlert(AlertType.ERROR, "Invalid login", e.getLocalizedMessage(),
                    mainController.getStage());
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
    private static void signInAdministrator(String username, String password, MainController mainController) {
        Administrator administrator;
        try {
            administrator = State.getAuthenticationManager().loginAdministrator(username, password);
        } catch (AuthenticationException | ServerRestException e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            PageNavigator.showAlert(AlertType.ERROR, "Invalid login", e.getLocalizedMessage(),
                    mainController.getStage());
            return;
        }

        PageNavigator.loadPage(Page.SEARCH, mainController);
        HistoryItem save = new HistoryItem("LOGIN_STAFF", String.format("Administrator %s logged in.",
                administrator.getUsername()));
        JSONConverter.updateHistory(save, "action_history.json");
    }

    public static void handleSignIn(String username, String password, MainController mainController) {
        if (isValidStaffIdInput(username)) {
            if (IS_NUMBER.matcher(username).matches()) {
                signInClinician(username, password, mainController);
            } else {
                signInAdministrator(username, password, mainController);
            }
        } else {
            invalidStaffIdAlert(mainController);
        }
    }

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
     * Does not do anything as page doesn't render anything that could have changed
     */
    @Override
    public void refresh() {
        //Do not need to do anything as page doesn't render anything that could have changed
    }

    /**
     * Navigates a user back to the Landing page.
     */
    @FXML
    private void goBack() {
        PageNavigator.loadPage(Page.LANDING, mainController);
    }

    /**
     * Checks if the staff id is valid and checks that the username and password is correct
     * The user cannot be logged in until valid parameters are supplied.
     */
    @FXML
    private void signIn() {
        handleSignIn(staffId.getText(), password.getText(), mainController);
    }
}
