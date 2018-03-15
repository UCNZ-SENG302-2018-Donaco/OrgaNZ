package seng302.Controller;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import seng302.App;
import seng302.AppUI;
import seng302.Donor;
import seng302.DonorManager;

public class CreateUserController {

    @FXML
    private Button createUserBtn;
    @FXML
    private DatePicker dobFld;
    @FXML
    private TextField firstNameFld;
    @FXML
    private TextField middleNamefld;
    @FXML
    private TextField lastNamefld;
    @FXML
    private Label successLbl;

    private DonorManager manager;

    @FXML
    private void initialize() {
        manager = AppUI.getManager();
    }

    private boolean force;

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
}
