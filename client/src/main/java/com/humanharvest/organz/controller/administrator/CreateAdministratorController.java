package com.humanharvest.organz.controller.administrator;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.controlsfx.control.Notifications;

import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.AdministratorManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.validators.NotEmptyStringValidator;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.views.administrator.CreateAdministratorView;

public class CreateAdministratorController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(CreateAdministratorController.class.getName());

    @FXML
    private Label usernameLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button createButton;

    @FXML
    private Pane menuBarPane;

    private AdministratorManager administratorManager;

    /**
     * Initialize the controller.
     */
    @FXML
    private void initialize() {
        administratorManager = State.getAdministratorManager();
    }

    /**
     * Override so we can set the page title.
     *
     * @param mainController The MainController
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.loadMenuBar(menuBarPane);
        mainController.setTitle("Create New Administrator");
    }

    /**
     * Does not do anything as page doesn't render anything that could have changed
     */
    @Override
    public void refresh() {
        //Do not need to do anything as page doesn't render anything that could have changed
    }

    /**
     * Checks that both fields are valid.
     * Sets any invalid fields' labels to red if they are invalid.
     * Shows an alert if there is invalid input (apart from empty input).
     * Invalid input:
     * * Username is numeric
     * * Username is already taken
     *
     * @return true if all fields are valid
     */
    private boolean fieldsAreValid() {
        boolean valid = true;

        // Username
        if (NotEmptyStringValidator.isInvalidString(usernameTextField.getText())) {
            usernameLabel.setTextFill(Color.RED);
            valid = false;
        } else {
            try {
                Integer.parseInt(usernameTextField.getText());
                // Username is numeric. This is not allowed - tell the user why, in an alert dialog.
                usernameLabel.setTextFill(Color.RED);
                valid = false;
                PageNavigator.showAlert(AlertType.ERROR, "Invalid username",
                        "Username must not be an integer, so as not to clash with clincians' staff IDs.",
                        mainController.getStage());
            } catch (NumberFormatException ex) {
                // Non-numeric username - check if it already exists
                if (administratorManager.doesUsernameExist(usernameTextField.getText())) {
                    usernameLabel.setTextFill(Color.RED);
                    valid = false;
                    PageNavigator.showAlert(AlertType.ERROR, "Invalid username",
                            "Username is already in use.", mainController.getStage());
                } else {
                    usernameLabel.setTextFill(Color.BLACK);
                }
            }
        }

        // Password
        if (NotEmptyStringValidator.isInvalidString(passwordField.getText())) {
            passwordLabel.setTextFill(Color.RED);
            valid = false;
        } else {
            passwordLabel.setTextFill(Color.BLACK);
        }

        return valid;
    }

    @FXML
    void createUser() {
        if (fieldsAreValid()) {
            String username = usernameTextField.getText();
            String password = passwordField.getText();

            CreateAdministratorView administratorView = new CreateAdministratorView(username, password);

            try {
                State.getAdministratorResolver().createAdministrator(administratorView);
                State.getAuthenticationManager().loginAdministrator(username, password);
                Notifications.create()
                        .title("Created Administrator")
                        .text(String.format("Administrator was created with username:%s", username))
                        .showInformation();

                PageNavigator.loadPage(Page.SEARCH, mainController);
            } catch (ServerRestException e) {
                LOGGER.log(Level.INFO, e.getMessage(), e);
                PageNavigator.showAlert(AlertType.ERROR,
                        "Error",
                        "An Administrator with this username exists. Please pick another", mainController.getStage());
            }
        }
    }

    @FXML
    void goBack() {
        PageNavigator.loadPage(Page.LANDING, mainController);
    }

}
