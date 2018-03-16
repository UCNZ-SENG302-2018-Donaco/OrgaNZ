package seng302.Controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import seng302.State;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;

import java.util.Optional;

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
        manager = State.getManager();
    }

    /**
     * Creates a user from the button being clicked.
     */
    @FXML
    private void createUser() {

        //doesn't work just yet...
        if (!force && manager.collisionExists(firstNameFld.getText(), middleNamefld.getText(), dobFld.getValue())) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Duplicate warning");
            alert.setHeaderText("Duplicate User warning");
            alert.setContentText("Duplicate user found, would you still like to create?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                // ... user chose OK
                int uid = manager.getUid();
                Donor donor = new Donor(firstNameFld.getText(), middleNamefld.getText(), lastNamefld.getText(), dobFld.getValue(), uid);
                manager.addDonor(donor);
                Alert alert0 = new Alert(Alert.AlertType.INFORMATION);
                alert0.setTitle("Success");
                alert0.setHeaderText("User Created");
                alert0.setContentText("Successfully created Donor " + firstNameFld.getText() + " " + middleNamefld.getText() + " " + lastNamefld.getText() + " with ID " + uid);
                alert0.showAndWait();
                PageNavigator.loadPage(Page.VIEW_DONOR.getPath());

            } else {
                // ... user chose CANCEL or closed the dialog
                return;
            }
        }

        int uid = manager.getUid();
        Donor donor = new Donor(firstNameFld.getText(), middleNamefld.getText(), lastNamefld.getText(), dobFld.getValue(), uid);
        manager.addDonor(donor);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("User Created");
        alert.setContentText("Successfully created Donor " + firstNameFld.getText() + " " + middleNamefld.getText() + " " + lastNamefld.getText() + " with ID " + uid);
        alert.showAndWait();
        PageNavigator.loadPage(Page.VIEW_DONOR.getPath());
    }


    @FXML
    private void goBack(ActionEvent event) {
        PageNavigator.loadPage(Page.LANDING.getPath());
    }
}
