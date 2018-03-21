package seng302.Controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.CreateDonorAction;
import seng302.HistoryItem;
import seng302.State;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;

public class CreateDonorController {
    @FXML
    private DatePicker dobFld;
    @FXML
    private TextField firstNameFld, middleNamefld, lastNamefld;

    private DonorManager manager;
    private ActionInvoker invoker;

    @FXML
    private void initialize() {
        manager = State.getManager();
        invoker = State.getInvoker();
    }

    /**
     * Creates a user from the button being clicked.
     */
    @FXML
    private void createDonor() {

        if (firstNameFld.getText() == "" | lastNamefld.getText() == "" | dobFld.getValue() == null) {
            PageNavigator.showAlert(AlertType.ERROR, "Text field missing",
                    "Please make sure that all the required fields are filled in properly")
                    .get();
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

            State.setPageParam("currentUserId", uid);
            State.setPageParam("currentUserType", "donor");
            PageNavigator.loadPage(Page.VIEW_DONOR.getPath());
        }
    }


    /**
     * Goes back to the landing page when button is clicked
     *
     * @param event button clicked and goes back.
     */
    @FXML
    private void goBack(ActionEvent event) {
        PageNavigator.loadPage(Page.LANDING.getPath());
    }
}
