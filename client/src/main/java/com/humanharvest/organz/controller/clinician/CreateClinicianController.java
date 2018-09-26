package com.humanharvest.organz.controller.clinician;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.ClinicianManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.JSONConverter;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.validators.NotEmptyStringValidator;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;

/**
 * This controller provides the user with an interface allowing them to enter clinician details. This creates a
 * clinician login for them and takes them to the view clinician page.
 */
public class CreateClinicianController extends SubController {

    @FXML
    private TextField fname, lname, mname, staffId, workAddress;
    @FXML
    private PasswordField password;
    @FXML
    private Label fnameLabel, mnameLabel, lnameLabel, staffIdLabel, regionLabel, passwordLabel;
    @FXML
    private ChoiceBox<Region> region;
    @FXML
    private Button createButton, goBackButton;
    @FXML
    private Pane menuBarPane;

    private ClinicianManager clinicianManager;

    /**
     * Initialize the controller to display appropriate items.
     */
    @FXML
    private void initialize() {
        clinicianManager = State.getClinicianManager();
        region.setItems(FXCollections.observableArrayList(Region.values()));
    }

    /**
     * Override so we can set the page title.
     *
     * @param mainController The MainController
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.loadMenuBar(menuBarPane);
        mainController.setTitle("Create a new Clinician");
    }

    /**
     * Does not do anything as page doesn't render anything that could have changed
     */
    @Override
    public void refresh() {
        //Do not need to do anything as page doesn't render anything that could have changed
    }

    /**
     * Checks that all mandatory fields have had valid input correctly input. Invalid input results in the text beside
     * the instigating field turning red.
     *
     * @return if all mandatory fields have valid input. False otherwise
     */
    private boolean checkMandatoryFields() {
        boolean update = true;
        if (NotEmptyStringValidator.isInvalidString(fname.getText())) {   // First name
            fnameLabel.setTextFill(Color.RED);
            update = false;
        } else {
            fnameLabel.setTextFill(Color.BLACK);
        }

        if (NotEmptyStringValidator.isInvalidString(lname.getText())) {   // Last name
            lnameLabel.setTextFill(Color.RED);
            update = false;
        } else {
            lnameLabel.setTextFill(Color.BLACK);
        }

        try {
            int id = Integer.parseInt(staffId.getText()); // Staff ID
            if (id < -1) {
                staffIdLabel.setTextFill(Color.RED);
                update = false;

            } else if (clinicianManager
                    .doesStaffIdExist(Integer.parseInt(staffId.getText()))) { // If the staffId is in use
                PageNavigator.showAlert(AlertType.ERROR, "Staff Id in Use", "This staff Id is already in use.",
                        mainController.getStage());
                staffIdLabel.setTextFill(Color.RED);
                update = false;

            } else {
                staffIdLabel.setTextFill(Color.BLACK);
            }

        } catch (NumberFormatException ex) {
            staffIdLabel.setTextFill(Color.RED);
            update = false;
        }

        if (NotEmptyStringValidator.isInvalidString(password.getText())) { // Password
            passwordLabel.setTextFill(Color.RED);
            update = false;
        } else {
            passwordLabel.setTextFill(Color.BLACK);
        }
        return update;
    }

    /**
     * Creates a Clinician if all of the fields have valid input.
     */
    @FXML
    private void createUser() {
        if (checkMandatoryFields()) {

            if (clinicianManager.doesStaffIdExist(Integer.parseInt(staffId.getText()))) {
                staffIdLabel.setTextFill(Color.RED);
            } else {
                Region r;
                if (region.getValue() == null) {
                    r = Region.UNSPECIFIED;
                } else {
                    r = region.getValue();
                }
                // TODO
                Clinician clinician = new Clinician(fname.getText(), mname.getText(), lname.getText(),
                        workAddress.getText(), r.toString(),
                        Country.NZ,
                        Integer.parseInt(staffId.getText()),
                        password.getText());

                State.getClinicianManager().addClinician(clinician);
                State.setViewedClinician(clinician);

                HistoryItem save = new HistoryItem("CREATE CLINICIAN",
                        "Clinician " + fname.getText() + " " + lname.getText() + " with staff ID " + staffId.getText()
                                + " Created.");
                JSONConverter.updateHistory(save, "action_history.json");

                PageNavigator.loadPage(Page.VIEW_CLINICIAN, mainController);
            }
        }
    }

    /**
     * Takes the user back to the landing page.
     */
    @FXML
    private void goBack() {
        PageNavigator.loadPage(Page.LANDING, mainController);
    }
}
