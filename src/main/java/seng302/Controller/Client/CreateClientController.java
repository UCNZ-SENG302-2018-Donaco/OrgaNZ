package seng302.Controller.Client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.Client.CreateClientAction;
import seng302.Client;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.HistoryItem;
import seng302.State.ClientManager;
import seng302.State.State;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;

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

    private ClientManager manager;
    private ActionInvoker invoker;

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
    }

    /**
     * Creates a new client based on the information supplied in the fields.
     * Shows appropriate alerts if the information is invalid, or if the client already exists.
     * Shows an alert if successful, then redirects to the view page for the new client.
     */
    @FXML
    private void createClient() {
        if (firstNameFld.getText().equals("") || lastNamefld.getText().equals("") || dobFld.getValue() == null) {
            PageNavigator.showAlert(AlertType.ERROR, "Required Field Empty",
                    "Please make sure that all the required fields are given.");
        } else {
            //Duplicate user warning alert
            if (manager.collisionExists(firstNameFld.getText(), lastNamefld.getText(), dobFld.getValue())) {
                ButtonType option = PageNavigator.showAlert(AlertType.CONFIRMATION,
                        "Duplicate Client Warning",
                        "This client is a duplicate of one that already exists. Would you still like to create it?")
                        .get();
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
            HistoryItem save = new HistoryItem("CREATE CLIENT",
                    "Client " + firstNameFld.getText() + " " + lastNamefld.getText() + "was created with ID " + uid);
            JSONConverter.updateHistory(save, "action_history.json");

            State.login(client);
            PageNavigator.loadPage(Page.VIEW_CLIENT, mainController);
        }
    }

    /**
     * Redirects the UI back to the landing page.
     */
    @FXML
    private void goBack() {
        PageNavigator.loadPage(Page.LANDING, mainController);
    }
}
