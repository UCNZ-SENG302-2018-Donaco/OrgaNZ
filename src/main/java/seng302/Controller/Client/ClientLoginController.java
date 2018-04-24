package seng302.Controller.Client;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import seng302.Client;
import seng302.Controller.SubController;
import seng302.HistoryItem;
import seng302.State.ClientManager;
import seng302.State.Session;
import seng302.State.State;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;

/**
 * Controller for the login page.
 */
public class ClientLoginController extends SubController {

    @FXML
    private ListView<Client> clientList;

    /**
     * Initializes the UI for this page.
     * - Gets the client manager from the current state.
     * - Sets up the cell factory to show users with their id and name.
     * - Adds all clients currently in the client manager to the client list.
     */
    @FXML
    private void initialize() {
        ClientManager clientManager = State.getClientManager();

        clientList.setCellFactory(cell -> new ListCell<Client>() {
            @Override
            public void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("ID %d: %s %s", item.getUid(), item.getFirstName(), item.getLastName()));
                }
            }
        });
        clientList.setItems(FXCollections.observableArrayList(clientManager.getClients()));
    }

    /**
     * Attempts to login with the selected client.
     * If successful, redirects to the view client page for that client.
     * @param event When the sign in button is clicked.
     */
    @FXML
    private void signIn(ActionEvent event) {
        Client selectedClient = clientList.getSelectionModel().getSelectedItem();

        if (selectedClient != null) {
            HistoryItem loginHistory = new HistoryItem("LOGIN_CLIENT", String.format("Client %s %s (%d) logged in.",
                    selectedClient.getFirstName(), selectedClient.getLastName(), selectedClient.getUid()));
            JSONConverter.updateHistory(loginHistory, "action_history.json");

            State.login(Session.UserType.CLIENT, selectedClient);
            PageNavigator.loadPage(Page.VIEW_CLIENT, mainController);
        }
    }

    /**
     * Redirects the UI back to the landing page.
     * @param event When the back button is clicked.
     */
    @FXML
    private void goBack(ActionEvent event) {
        PageNavigator.loadPage(Page.LANDING, mainController);
    }
}
