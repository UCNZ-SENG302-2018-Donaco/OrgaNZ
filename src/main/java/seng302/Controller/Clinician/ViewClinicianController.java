package seng302.Controller.Clinician;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import seng302.Actions.ActionInvoker;
import seng302.Actions.Clinician.ModifyClinicianAction;
import seng302.Clinician;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.HistoryItem;
import seng302.State.Session;
import seng302.State.Session.UserType;
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
    private Pane menuBarPane, loadClinicianPane, inputsPane;
    @FXML
    private Label creationDate, lastModified, fnameLabel, lnameLabel, passwordLabel;
    @FXML
    private TextField fname, lname, mname, workAddress, loadStaffIdTextField;
    @FXML
    private PasswordField password;
    @FXML
    private ChoiceBox<Region> region;
    @FXML
    private Button saveChangesButton, loadClinicianButton;

    public ViewClinicianController() {
        invoker = State.getInvoker();
        session = State.getSession();

        if (session.getLoggedInUserType() == UserType.ADMINISTRATOR) {
            currentClinician = State.getClinicianManager().getDefaultClinician();
        } else {
            //should be logged in as clinician
            currentClinician = session.getLoggedInClinician();
        }
    }

    /**
     * Initialize the page.
     */
    @FXML
    private void initialize() {
        region.setItems(FXCollections.observableArrayList(Region.values()));
        inputsPane.setVisible(true);

        loadClinicianData();
    }

    /**
     * Sets the page title, loads the sidebar, and hides the "Load clincian" pane if the user is a clinician.
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Clinician profile: " + currentClinician.getFullName());
        mainController.loadMenuBar(menuBarPane);

        if (session.getLoggedInUserType() == Session.UserType.CLINICIAN) {
            loadClinicianPane.setVisible(false);
            loadClinicianPane.setManaged(false);
        }
    }

    @Override
    public void refresh() {
        loadClinicianData();
    }


    /**
     * Loads the clinician identified by the staff ID in loadStaffIdTextField.
     */
    @FXML
    void loadClinician() {
        int id_value;
        try {
            id_value = Integer.parseInt(loadStaffIdTextField.getText());
        } catch (Exception e) {
            e.printStackTrace();
            PageNavigator.showAlert(Alert.AlertType.ERROR, "Invalid Staff ID",
                    "The Staff ID must be an integer.");
            return;
        }
        Clinician newClin = State.getClinicianManager().getClinicianByStaffId(id_value);

        if (newClin == null) {
            PageNavigator.showAlert(Alert.AlertType.ERROR, "Invalid Staff ID",
                    "This staff ID does not exist in the system.");
            return;
        } else {
            currentClinician = newClin;
        }

        loadClinicianData();
    }

    /**
     * Loads all of the currently logged in Clinician's details, except for their password.
     */
    private void loadClinicianData() {
        loadStaffIdTextField.setText(String.valueOf(currentClinician.getStaffId()));
        fname.setText(currentClinician.getFirstName());
        mname.setText(currentClinician.getMiddleName());
        lname.setText(currentClinician.getLastName());
        workAddress.setText(currentClinician.getWorkAddress());
        region.setValue(currentClinician.getRegion());

        creationDate.setText(currentClinician.getCreatedOn().format(dateTimeFormat));
        if (currentClinician.getModifiedOn() == null) {
            lastModified.setText("Not yet modified.");
        } else {
            lastModified.setText(currentClinician.getModifiedOn().format(dateTimeFormat));
        }
    }

    /**
     * Saves the changes a user makes to the viewed clinician if all their inputs are valid. Otherwise the invalid
     * fields text turns red.
     */
    @FXML
    private void apply() {
        if (checkMandatoryFields()) {
            updatedPassword = checkPassword();
             if (updateChanges()) {
                 lastModified.setText(currentClinician.getModifiedOn().format(dateTimeFormat));
             }
        }
    }

    /**
     * Resets the page back to its default state.
     */
    @FXML
    private void cancel() {
        refresh();
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
        return update;
    }


    /**
     * Checks if the password has been updated. If the PasswordField is left blank, the old password remains current.
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

    private void addChangeIfDifferent(ModifyClinicianAction action, String field, Object oldValue, Object newValue) {
        try {
            if (!Objects.equals(oldValue, newValue)) {
                action.addChange(field, oldValue, newValue);
            }
        } catch (NoSuchFieldException | NoSuchMethodException exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Records the changes updated as a ModifyClinicianAction to trace the change in record.
     * @return If there were any changes made
     */
    private boolean updateChanges() {
        ModifyClinicianAction action = new ModifyClinicianAction(currentClinician);

        addChangeIfDifferent(action, "setFirstName", currentClinician.getFirstName(), fname.getText());
        addChangeIfDifferent(action, "setLastName", currentClinician.getLastName(), lname.getText());
        addChangeIfDifferent(action, "setMiddleName", currentClinician.getMiddleName(), mname.getText());
        addChangeIfDifferent(action, "setWorkAddress", currentClinician.getWorkAddress(), workAddress.getText());
        addChangeIfDifferent(action, "setPassword", currentClinician.getPassword(), updatedPassword);
        addChangeIfDifferent(action, "setRegion", currentClinician.getRegion(), region.getValue());

        try {
            String actionText = invoker.execute(action);

            Notifications.create()
                    .title("Updated Clinician")
                    .text(actionText)
                    .showInformation();

            HistoryItem save = new HistoryItem("UPDATE CLINICIAN",
                    "The Clinician's information was updated. New details are: " + actionText);
            JSONConverter.updateHistory(save, "action_history.json");

            PageNavigator.refreshAllWindows();
            return true;

        } catch (IllegalStateException exc) {
                Notifications.create()
                        .title("No changes were made.")
                        .text("No changes were made to the clinician.")
                        .showWarning();
                return false;
        }

    }
}
