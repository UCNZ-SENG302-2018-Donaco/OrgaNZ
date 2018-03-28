package seng302.Controller.Donor;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import seng302.Controller.SubController;
import seng302.Donor;
import seng302.State.DonorManager;
import seng302.HistoryItem;
import seng302.State.Session;
import seng302.State.State;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;

/**
 * Controller for the login page.
 */
public class DonorLoginController extends SubController {

    @FXML
    private ListView<Donor> donorList;

    /**
     * Initializes the UI for this page.
     * - Gets the donor manager from the current state.
     * - Sets up the cell factory to show users with their id and name.
     * - Adds all donors currently in the donor manager to the donor list.
     */
    @FXML
    private void initialize() {
        DonorManager donorManager = State.getDonorManager();

        donorList.setCellFactory(cell -> new ListCell<Donor>() {
            @Override
            public void updateItem(Donor item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("ID %d: %s %s", item.getUid(), item.getFirstName(), item.getLastName()));
                }
            }
        });
        donorList.setItems(FXCollections.observableArrayList(donorManager.getDonors()));
    }

    /**
     * Attempts to login with the selected donor.
     * If successful, redirects to the view donor page for that donor.
     * @param event When the sign in button is clicked.
     */
    @FXML
    private void signIn(ActionEvent event) {
        Donor selectedDonor = donorList.getSelectionModel().getSelectedItem();

        if (selectedDonor != null) {
            HistoryItem loginHistory = new HistoryItem("LOGIN_DONOR", String.format("Donor %s %s (%d) logged in.",
                    selectedDonor.getFirstName(), selectedDonor.getLastName(), selectedDonor.getUid()));
            JSONConverter.updateHistory(loginHistory, "action_history.json");

            State.login(Session.UserType.DONOR, selectedDonor);
            PageNavigator.loadPage(Page.VIEW_DONOR, mainController);
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
