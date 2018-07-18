package com.humanharvest.organz.controller.client;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.JSONConverter;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;

import java.util.Objects;

/**
 * Controller for the login page.
 */
public class ClientLoginController extends SubController {

    @FXML
    private ListView<Client> clientList;
    @FXML
    private Button signInButton, goBackButton;

    /**
     * Override so we can set the page title.
     * @param mainController The MainController
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Client login");
    }

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
                    String originalName = item.getFirstName() + " " + item.getLastName();
                    if (Objects.equals(originalName, item.getPreferredName())) {
                        setText(String.format("ID %d: %s %s", item.getUid(), item.getFirstName(), item.getLastName()));
                    } else {
                        setText(String.format("ID %d: %s", item.getUid(), item.getPreferredName()));
                    }
                }
            }
        });
        clientList.setItems(FXCollections.observableArrayList(clientManager.getClients()));
    }

    /**
     * Attempts to login with the selected client.
     * If successful, redirects to the view client page for that client.
     */
    @FXML
    private void signIn() {
        Client selectedClient = clientList.getSelectionModel().getSelectedItem();

        if (selectedClient != null) {
            try {
                selectedClient = State.getAuthenticationManager().loginClient(selectedClient.getUid());
            } catch (AuthenticationException e) {
                PageNavigator.showAlert(AlertType.ERROR, "Invalid login", e.getLocalizedMessage());
                return;
            }

            HistoryItem loginHistory = new HistoryItem("LOGIN_CLIENT", String.format("Client %s %s (%d) logged in.",
                    selectedClient.getFirstName(), selectedClient.getLastName(), selectedClient.getUid()));
            JSONConverter.updateHistory(loginHistory, "action_history.json");

            State.login(selectedClient);
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
