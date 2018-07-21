package com.humanharvest.organz.controller.administrator;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.actions.administrator.CreateAdministratorAction;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.AdministratorManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;

public class CreateAdministratorController extends SubController {

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
     * @param mainController The MainController
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.loadMenuBar(menuBarPane);
        mainController.setTitle("Create a new administrator");
    }

    /**
     * Checks that both fields are valid.
     * Sets any invalid fields' labels to red if they are invalid.
     * Shows an alert if there is invalid input (apart from empty input).
     * Invalid input:
     * * Username is numeric
     * * Username is already taken
     * @return true if all fields are valid
     */
    private boolean fieldsAreValid() {
        boolean valid = true;

        // Username
        if (usernameTextField.getText().equals("")) {
            usernameLabel.setTextFill(Color.RED);
            valid = false;
        } else {
            try {
                Integer.parseInt(usernameTextField.getText());
                // Username is numeric. This is not allowed - tell the user why, in an alert dialog.
                usernameLabel.setTextFill(Color.RED);
                valid = false;
                PageNavigator.showAlert(AlertType.ERROR, "Invalid username",
                        "Username must not be an integer, so as not to clash with clincians' staff IDs.");
            } catch (NumberFormatException ex) {
                // Non-numeric username - check if it already exists
                if (administratorManager.doesUsernameExist(usernameTextField.getText())) {
                    usernameLabel.setTextFill(Color.RED);
                    valid = false;
                    PageNavigator.showAlert(AlertType.ERROR, "Invalid username",
                            "Username is already in use.");
                } else {
                    usernameLabel.setTextFill(Color.BLACK);
                }
            }
        }

        // Password
        if (passwordField.getText().equals("")) {
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
            Administrator administrator = new Administrator(usernameTextField.getText(), passwordField.getText());

            CreateAdministratorAction action = new CreateAdministratorAction(administrator, administratorManager);
            State.getInvoker().execute(action);

            PageNavigator.loadPage(Page.SEARCH, mainController);
        }
    }

    @FXML
    void goBack() {
        PageNavigator.loadPage(Page.LANDING, mainController);
    }

}
