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
import seng302.State;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;

public class CreateUserController {
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
    private void createUser() {

        //Duplicate user warning alert
        if (manager.collisionExists(firstNameFld.getText() , lastNamefld.getText(), dobFld.getValue())) {
            System.out.println("Duplicate");
            ButtonType option = PageNavigator.showAlert(AlertType.CONFIRMATION,
                    "Duplicate User Warning",
                    "This user is a duplicate of one that already exists. Would you still like to create it?")
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

        PageNavigator.showAlert(AlertType.INFORMATION,
                "Success",
                String.format("Successfully created donor %s %s %s with ID %d.",
                        donor.getFirstName(), donor.getMiddleName(), donor.getLastName(), uid));

        State.setPageParam("currentUserId", uid);
        State.setPageParam("currentUserType", "donor");
        PageNavigator.loadPage(Page.VIEW_DONOR.getPath());
    }


    @FXML
    private void goBack(ActionEvent event) {
        PageNavigator.loadPage(Page.LANDING.getPath());
    }
}
