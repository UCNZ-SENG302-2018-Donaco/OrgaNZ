package seng302.Controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import seng302.AppUI;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;

public class CreateUserController {
    @FXML
    private DatePicker dobFld;
    @FXML
    private TextField firstNameFld, middleNamefld, lastNamefld;
    @FXML
    private Label successLbl;

    private DonorManager manager;

    private boolean force;

    @FXML
    private void initialize() {
        manager = AppUI.getManager();
    }

    /**
     * Creates a user from the button being clicked.
     */
    @FXML
    private void createUser() {

        //doesn't work just yet...
        if (!force && manager.collisionExists(firstNameFld.getText(), middleNamefld.getText(), dobFld.getValue())) {
            System.out.println("Duplicate user found, use --force to create anyway");
            successLbl.setText("Duplicate user found, would you still like to create?");
            return;
        }

        int uid = manager.getUid();
        Donor donor = new Donor(firstNameFld.getText(), middleNamefld.getText(), lastNamefld.getText(), dobFld.getValue(), uid);
        manager.addDonor(donor);
        successLbl.setText("Successfully created Donor " + firstNameFld.getText() + " " + middleNamefld.getText() + " " + lastNamefld.getText() + " with ID " + uid);
    }

    @FXML
    private void goToViewDonor(ActionEvent event) {
        PageNavigator.loadPage(Page.VIEW_DONOR.getPath());
    }

    @FXML
    private void goBack(ActionEvent event) {
        PageNavigator.loadPage(Page.LANDING.getPath());
    }
}
