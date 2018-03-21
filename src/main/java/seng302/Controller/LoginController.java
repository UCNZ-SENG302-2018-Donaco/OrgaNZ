package seng302.Controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import seng302.Donor;
import seng302.DonorManager;
import seng302.HistoryItem;
import seng302.State;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;

/**
 * Controller for the login page.
 */
public class LoginController {
    @FXML
    private ListView<Donor> donorList;
    private DonorManager donorManager;

    /**
     * Initializes the UI for this page.
     * - Gets the donor manager from the current state.
     * - Sets up the cell factory to show users with their id and name.
     * - Adds all donors currently in the donor manager to the donor list.
     */
    @FXML
    private void initialize() {
        donorManager = State.getDonorManager();

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
            HistoryItem loginHistory = new HistoryItem("LOGIN", String.format("Donor %s %s (%d) logged in.",
                    selectedDonor.getFirstName(), selectedDonor.getLastName(), selectedDonor.getUid()));
            JSONConverter.updateHistory(loginHistory, "action_history.json");

            State.setPageParam("currentUserId", selectedDonor.getUid());
            State.setPageParam("currentUserType", "donor");
            PageNavigator.loadPage(Page.VIEW_DONOR.getPath());
        }
    }

    /**
     * Redirects the UI back to the landing page.
     * @param event When the back button is clicked.
     */
    @FXML
    private void goBack(ActionEvent event) {
        PageNavigator.loadPage(Page.LANDING.getPath());
    }
}
