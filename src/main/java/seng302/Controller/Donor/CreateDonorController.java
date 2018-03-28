package seng302.Controller.Donor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.Donor.CreateDonorAction;
import seng302.Controller.SubController;
import seng302.Donor;
import seng302.HistoryItem;
import seng302.State.DonorManager;
import seng302.State.Session;
import seng302.State.State;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;

/**
 * Controller for the create donor page.
 */
public class CreateDonorController extends SubController {

    @FXML
    private DatePicker dobFld;
    @FXML
    private TextField firstNameFld, middleNamefld, lastNamefld;

    private DonorManager manager;
    private ActionInvoker invoker;

    /**
     * Initializes the UI for this page.
     * - Gets the DonorManager and ActionInvoker from the current state.
     */
    public CreateDonorController() {
        manager = State.getDonorManager();
        invoker = State.getInvoker();
    }

    /**
     * Creates a new donor based on the information supplied in the fields.
     * Shows appropriate alerts if the information is invalid, or if the donor already exists.
     * Shows an alert if successful, then redirects to the view page for the new donor.
     * @param event When the create button is clicked.
     */
    @FXML
    private void createDonor(ActionEvent event) {
        if (firstNameFld.getText().equals("") || lastNamefld.getText().equals("") || dobFld.getValue() == null) {
            PageNavigator.showAlert(AlertType.ERROR, "Required Field Empty",
                    "Please make sure that all the required fields are given.");
        }
        else {
            //Duplicate user warning alert
            if (manager.collisionExists(firstNameFld.getText(), lastNamefld.getText(), dobFld.getValue())) {
                System.out.println("Duplicate");
                ButtonType option = PageNavigator.showAlert(AlertType.CONFIRMATION,
                        "Duplicate Donor Warning",
                        "This donor is a duplicate of one that already exists. Would you still like to create it?")
                        .get();
                if (option != ButtonType.OK) {
                    // ... user chose CANCEL or closed the dialog
                    return;
                }
            }

            int uid = manager.getUid();
            Donor donor = new Donor(firstNameFld.getText(), middleNamefld.getText(), lastNamefld.getText(), dobFld.getValue(), uid);
            Action action = new CreateDonorAction(donor, manager);
            invoker.execute(action);
            HistoryItem save = new HistoryItem("CREATE DONOR",
                    "Donor " + firstNameFld.getText() + " " + lastNamefld.getText() + "was created with ID " + uid);
            JSONConverter.updateHistory(save, "action_history.json");

            PageNavigator.showAlert(AlertType.INFORMATION,
                    "Success",
                    String.format("Successfully created donor %s %s %s with ID %d.",
                            donor.getFirstName(), donor.getMiddleName(), donor.getLastName(), uid));

            State.login(Session.UserType.DONOR, donor);
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
