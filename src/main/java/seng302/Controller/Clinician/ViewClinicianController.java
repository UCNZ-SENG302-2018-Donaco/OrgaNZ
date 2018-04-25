package seng302.Controller.Clinician;

import java.time.format.DateTimeFormatter;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import seng302.Actions.ActionInvoker;
import seng302.Actions.Clinician.ModifyClinicianAction;
import seng302.Clinician;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.HistoryItem;
import seng302.State.Session;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.View.PageNavigator;

import org.controlsfx.control.Notifications;

/**
 * Presents an interface displaying all information of the currently logged in Clinician. Clinicians are able to edit
 * their details directly on this page.
 */
public class ViewClinicianController extends SubController {

    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy\nh:mm:ss a");

    private Session session;
    private ActionInvoker invoker;
    private Clinician currentClinician;
    private String updatedPassword;

    @FXML
    private Pane sidebarPane, idPane, inputsPane;
    @FXML
    private Label creationDate, lastModified, fnameLabel, lnameLabel, passwordLabel;
    @FXML
    private TextField staffID, fname, lname, mname, workAddress;
    @FXML
    private PasswordField password;
    @FXML
    private ChoiceBox<Region> region;

    public ViewClinicianController() {
        invoker = State.getInvoker();
        session = State.getSession();

        currentClinician = session.getLoggedInClinician();
    }

    /**
     * Initialize the page.
     */
    @FXML
    private void initialize() {
        region.setItems(FXCollections.observableArrayList(Region.values()));
        staffID.setDisable(true);
        inputsPane.setVisible(true);

        loadClinicianData();
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Clinician profile: " + currentClinician.getFullName());
        mainController.loadSidebar(sidebarPane);
    }

    @Override
    public void refresh() {
        loadClinicianData();
    }

    /**
     * Loads all of the currently logged in Clinician's details, except for their password.
     */
    private void loadClinicianData() {
        fname.setText(currentClinician.getFirstName());
        mname.setText(currentClinician.getMiddleName());
        lname.setText(currentClinician.getLastName());
        workAddress.setText(currentClinician.getWorkAddress());
        staffID.setText(String.valueOf(currentClinician.getStaffId()));
        region.setValue(currentClinician.getRegion());

        creationDate.setText(currentClinician.getCreated_on().format(dateTimeFormat));
        if (currentClinician.getModified_on() == null) {
            lastModified.setText("Not yet modified.");
        } else {
            lastModified.setText(currentClinician.getModified_on().format(dateTimeFormat));
        }
    }

    /**
     * Saves the changes a user makes to the viewed donor if all their inputs are valid. Otherwise the invalid fields
     * text turns red.
     */
    @FXML
    private void saveChanges() {
        if (checkMandatoryFields()) {
            updatedPassword = checkPassword();
            updateChanges();
            lastModified.setText(currentClinician.getModified_on().format(dateTimeFormat));
        }
    }

    /**
     * Checks that all mandatory fields have valid arguments inside. Otherwise display red text on the invalidly entered
     * labels.
     * @return true if all mandatory fields have valid input.
     */
    private boolean checkMandatoryFields() {
        boolean update = true;
        if (fname.getText().equals("")) {
            fnameLabel.setTextFill(Color.RED);
            update = false;
        } else {
            fnameLabel.setTextFill(Color.BLACK);
        }

        if (lname.getText().equals("")) {
            lnameLabel.setTextFill(Color.RED);
            update = false;
        } else {
            lnameLabel.setTextFill(Color.BLACK);
        }
        if (lname.getText().equals("")) {
            lnameLabel.setTextFill(Color.RED);
            update = false;
        } else {
            lnameLabel.setTextFill(Color.BLACK);
        }
        return update;
    }


    /**
     * Checks if the password has been update. If the PasswordField is left blank, the old password remains current.
     * Otherwise the current password is updated to the newly entered value in the field.
     * @return the users password.
     */
    private String checkPassword() {
        if (password.getText().equals("")) {
            return currentClinician.getPassword();
        } else {
            return password.getText();
        }
    }

    /**
     * Records the changes updated as a ModifyDonorAction to trace the change in record.
     */
    private void updateChanges() {
        try {
            ModifyClinicianAction action = new ModifyClinicianAction(currentClinician);

            action.addChange("setFirstName", currentClinician.getFirstName(), fname.getText());
            action.addChange("setLastName", currentClinician.getLastName(), lname.getText());
            action.addChange("setMiddleName", currentClinician.getMiddleName(), mname.getText());
            action.addChange("setWorkAddress", currentClinician.getWorkAddress(), workAddress.getText());
            action.addChange("setPassword", currentClinician.getPassword(), updatedPassword);
            action.addChange("setRegion", currentClinician.getRegion(), region.getValue());

            invoker.execute(action);
            PageNavigator.refreshAllWindows();

            HistoryItem save = new HistoryItem("UPDATE CLINICIAN",
                    "The Clinician's information was updated. New details are: " + currentClinician.getUpdateLog());
            JSONConverter.updateHistory(save, "action_history.json");

            Notifications.create().title("Updated").text("Successfully updated clinician").showInformation();
        } catch (NoSuchFieldException | NoSuchMethodException exc) {
            exc.printStackTrace();
        }
    }

}
