package com.humanharvest.organz.controller.clinician;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.JSONConverter;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.views.clinician.ModifyClinicianObject;
import org.controlsfx.control.Notifications;

/**
 * Presents an interface displaying all information of the currently logged in Clinician. Clinicians are able to edit
 * their details directly on this page.
 */
public class ViewClinicianController extends ViewBaseController {

    private static final Logger LOGGER = Logger.getLogger(ViewClinicianController.class.getName());

    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy\nh:mm:ss a");

    private final Session session;
    private Clinician viewedClinician;
    private String updatedPassword;

    @FXML
    private Pane menuBarPane;
    @FXML
    private Pane loadClinicianPane;
    @FXML
    private Pane inputsPane;
    @FXML
    private Label creationDate;
    @FXML
    private Label lastModified;
    @FXML
    private Label fnameLabel;
    @FXML
    private Label lnameLabel;
    @FXML
    private TextField fname;
    @FXML
    private TextField lname;
    @FXML
    private TextField mname;
    @FXML
    private TextField workAddress;
    @FXML
    private TextField loadStaffIdTextField;
    @FXML
    private PasswordField password;
    @FXML
    private ChoiceBox<Region> region;
    @FXML
    private Button loadClinicianButton;

    public ViewClinicianController() {
        session = State.getSession();

        switch (session.getLoggedInUserType()) {
            case ADMINISTRATOR:
                viewedClinician = State.getClinicianManager().getDefaultClinician();
                break;
            case CLINICIAN:
                viewedClinician = session.getLoggedInClinician();
                break;
            default:
                throw new IllegalStateException("Should not get to this page without being logged in.");
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
        loadClinicianButton.setDisable(true); //TODO discuss whether we even need this?
        loadStaffIdTextField.setDisable(true);
    }

    /**
     * Sets the page title, loads the sidebar, and hides the "Load clincian" pane if the user is a clinician.
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Clinician profile: " + viewedClinician.getFullName());
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
        int idValue;
        try {
            idValue = Integer.parseInt(loadStaffIdTextField.getText());
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid staff ID", e);
            PageNavigator.showAlert(Alert.AlertType.ERROR, "Invalid Staff ID",
                    "The Staff ID must be an integer.");
            return;
        }
        Optional<Clinician> newClin = State.getClinicianManager().getClinicianByStaffId(idValue);

        if (newClin.isPresent()) {
            viewedClinician = newClin.get();
        } else {
            PageNavigator.showAlert(Alert.AlertType.ERROR, "Invalid Staff ID",
                    "This staff ID does not exist in the system.");
            return;
        }

        loadClinicianData();
    }

    /**
     * Loads all of the currently logged in Clinician's details, except for their password.
     */
    private void loadClinicianData() {
        viewedClinician = State.getClinicianManager().getClinicianByStaffId(viewedClinician.getStaffId())
                .orElseThrow(IllegalStateException::new);
        loadStaffIdTextField.setText(String.valueOf(viewedClinician.getStaffId()));
        fname.setText(viewedClinician.getFirstName());
        mname.setText(viewedClinician.getMiddleName());
        lname.setText(viewedClinician.getLastName());
        workAddress.setText(viewedClinician.getWorkAddress());
        region.setValue(viewedClinician.getRegion());

        creationDate.setText(viewedClinician.getCreatedOn().format(dateTimeFormat));
        if (viewedClinician.getModifiedOn() == null) {
            lastModified.setText("Not yet modified.");
        } else {
            lastModified.setText(viewedClinician.getModifiedOn().format(dateTimeFormat));
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
            if (updateChanges() && viewedClinician.getModifiedOn() != null) {
                lastModified.setText(viewedClinician.getModifiedOn().format(dateTimeFormat));
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
        if (fname.getText().isEmpty()) {
            fnameLabel.setTextFill(Color.RED);
            update = false;
        } else {
            fnameLabel.setTextFill(Color.BLACK);
        }
        if (lname.getText().isEmpty()) {
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
        if (password.getText().isEmpty()) {
            return viewedClinician.getPassword();
        } else {
            return password.getText();
        }
    }

    /**
     * Records the changes updated as a ModifyClinicianAction to trace the change in record.
     * @return If there were any changes made
     */
    private boolean updateChanges() {
        ModifyClinicianObject modifyClinicianObject = new ModifyClinicianObject();

        addChangeIfDifferent(modifyClinicianObject, viewedClinician, "firstName", fname.getText());
        addChangeIfDifferent(modifyClinicianObject, viewedClinician, "lastName", lname.getText());
        addChangeIfDifferent(modifyClinicianObject, viewedClinician, "middleName", mname.getText());
        addChangeIfDifferent(modifyClinicianObject, viewedClinician, "workAddress", workAddress.getText());
        addChangeIfDifferent(modifyClinicianObject, viewedClinician, "password", updatedPassword);
        addChangeIfDifferent(modifyClinicianObject, viewedClinician, "region", region.getValue());

        try {
            viewedClinician = State.getClinicianResolver().modifyClinician(viewedClinician, modifyClinicianObject);
            String actionText = modifyClinicianObject.toString();

            Notifications.create()
                    .title("Updated Clinician")
                    .text(actionText)
                    .showInformation();

            HistoryItem save = new HistoryItem("UPDATE CLINICIAN",
                    "The Clinician's information was updated. New details are: " + actionText);
            JSONConverter.updateHistory(save, "action_history.json");

            PageNavigator.refreshAllWindows();
            return true;

        } catch (NotFoundException e) {
            LOGGER.log(Level.WARNING, "Client not found");
            PageNavigator.showAlert(AlertType.WARNING, "Clinician not found", "The clinician could not be found on "
                    + "the server, it may have been deleted");
            return false;
        } catch (ServerRestException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            PageNavigator.showAlert(AlertType.WARNING, "Server error", "Could not apply changes on the server, "
                    + "please try again later");
            return false;
        } catch (IfMatchFailedException e) {
            LOGGER.log(Level.INFO, "If-Match did not match");
            PageNavigator.showAlert(AlertType.WARNING, "Outdated Data",
                    "The clinician has been modified since you retrieved the data.\nIf you would still like to "
                            + "apply these changes please submit again, otherwise refresh the page to update the data.");
            return false;
        }
    }
}
