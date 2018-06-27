package com.humanharvest.organz.controller.client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.client.CreateClientAction;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.ui.validation.NotNullValidator;
import com.humanharvest.organz.ui.validation.StringValidator;
import com.humanharvest.organz.ui.validation.UIValidation;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext;

/**
 * Controller for the create client page.
 */
public class CreateClientController extends SubController {

    @FXML
    private DatePicker dobFld;
    @FXML
    private TextField firstNameFld, middleNamefld, lastNamefld;
    @FXML
    private Button createButton, goBackButton;
    @FXML
    private Pane menuBarPane;

    private final ClientManager manager;
    private final ActionInvoker invoker;
    private UIValidation validation;

    /**
     * Initializes the UI for this page.
     * - Gets the ClientManager and ActionInvoker from the current state.
     */
    public CreateClientController() {
        manager = State.getClientManager();
        invoker = State.getInvoker();
    }

    /**
     * Override so we can set the page title.
     * @param mainController The MainController
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Create a new Client");

        if (State.getSession() != null) { //they're a clinician or admin
            mainController.loadMenuBar(menuBarPane);
            goBackButton.setVisible(false);
        }

        validation = new UIValidation()
                .add(firstNameFld, new StringValidator())
                .add(lastNamefld, new StringValidator())
                .add(dobFld, new NotNullValidator())
                .validate();
    }

    /**
     * Creates a new client based on the information supplied in the fields.
     * Shows appropriate alerts if the information is invalid, or if the client already exists.
     * Shows an alert if successful, then redirects to the view page for the new client.
     */
    @FXML
    private void createClient() {
        if (validation.isInvalid()) {
            PageNavigator.showAlert(AlertType.ERROR, "Required Field Empty",
                    "Please make sure that all the required fields are given.");
        } else {
            //Duplicate user warning alert
            if (manager.collisionExists(firstNameFld.getText(), lastNamefld.getText(), dobFld.getValue())) {
                ButtonType option = PageNavigator.showAlert(AlertType.CONFIRMATION,
                        "Duplicate Client Warning",
                        "This client is a duplicate of one that already exists. Would you still like to create it?")
                        .orElse(ButtonType.CANCEL);
                if (option != ButtonType.OK) {
                    // ... user chose CANCEL or closed the dialog
                    return;
                }
            }

            int uid = manager.nextUid();
            Client client = new Client(firstNameFld.getText(), middleNamefld.getText(), lastNamefld.getText(),
                    dobFld.getValue(), uid);
            Action action = new CreateClientAction(client, manager);
            invoker.execute(action);

            if (State.getSession() == null) { // Someone creating a client
                State.login(client);
                PageNavigator.loadPage(Page.VIEW_CLIENT, mainController);

            } else { // Clinician or admin are creating a user.

                MainController newMain = PageNavigator.openNewWindow();
                if (newMain != null) {
                    newMain.setWindowContext(new WindowContext.WindowContextBuilder()
                            .setAsClinViewClientWindow()
                            .viewClient(client)
                            .build());
                    PageNavigator.loadPage(Page.VIEW_CLIENT, newMain);
                }
            }
        }
    }

    /**
     * Redirects the UI back to the previous page.
     */
    @FXML
    private void goBack() {
        if (State.getSession() == null) { // Someone creating a user
            PageNavigator.loadPage(Page.LANDING, mainController);

        } else if (State.getSession().getLoggedInUserType() == UserType.CLINICIAN) {
            PageNavigator.loadPage(Page.VIEW_CLINICIAN, mainController);
        }
    }
}
