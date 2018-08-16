package com.humanharvest.organz.controller.clinician;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.JSONConverter;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.views.clinician.ModifyClinicianObject;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.controlsfx.control.Notifications;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.EnumSet;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Presents an interface displaying all information of the currently logged in Clinician. Clinicians are able to edit
 * their details directly on this page.
 */
public class ViewClinicianController extends ViewBaseController {

    private static final Logger LOGGER = Logger.getLogger(ViewClinicianController.class.getName());

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withZone(ZoneId.systemDefault());

    private final Session session;
    private Clinician viewedClinician;
    private String updatedPassword;

    @FXML
    private Pane menuBarPane;
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
    private Label staffIdLabel;
    @FXML
    private Label title;
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
    private ChoiceBox<Region> regionCB;
    @FXML
    private TextField regionTF;
    @FXML
    private ChoiceBox<Country> country;
    @FXML
    private Button loadClinicianButton;

    public ViewClinicianController() {
        session = State.getSession();

        switch (session.getLoggedInUserType()) {
            case ADMINISTRATOR:
                if (State.getViewedClinician() != null) {
                    viewedClinician = State.getViewedClinician();
                    State.setViewedClinician(null);
                } else {
                    viewedClinician = State.getClinicianManager().getDefaultClinician();
                }
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
        regionCB.setItems(FXCollections.observableArrayList(Region.values()));
        inputsPane.setVisible(true);

    }

    /**
     * Sets the page title, loads the sidebar, and hides the "Load clincian" pane if the user is a clinician.
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Clinician profile: " + viewedClinician.getFullName());
        mainController.loadMenuBar(menuBarPane);

        getViewedClinicianData();
        updateCountries();
    }

    @Override
    public void refresh() {
        getViewedClinicianData();
        updateCountries();
    }


    /**
     * Checks the clinicians country, changes region input to a choicebox of NZ regions if the country is New Zealand,
     * and changes to a textfield input for any other country
     */
    private void checkClinicianCountry() {
        if (viewedClinician.getCountry() == Country.NZ ) {
            regionCB.setVisible(true);
            regionTF.setVisible(false);
        } else {
            regionCB.setVisible(false);
            regionTF.setVisible(true);
        }
    }

    /**
     * If the choicebox for the country is changed then this method will be called to decide if region should display
     * a textfield or choicebox
     */
    @FXML
    private void checkCountry() {
        if (country.getValue() != null && country.getValue() == Country.NZ) {
            regionCB.setVisible(true);
            regionTF.setVisible(false);

        } else {
            regionCB.setVisible(false);
            regionTF.setVisible(true);
        }
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
                    "The Staff ID must be an integer.", mainController.getStage());
            return;
        }
        try {
            Optional<Clinician> newClin = State.getClinicianManager().getClinicianByStaffId(idValue);
            viewedClinician = newClin.get();
        } catch (NotFoundException ex) {
            PageNavigator.showAlert(Alert.AlertType.ERROR, "Invalid Staff ID",
                    "This staff ID does not exist in the system.", mainController.getStage());
            return;
        }
        getViewedClinicianData();
    }

    /**
     * Loads all of the currently viewed Clinician's details.
     */
    private void getViewedClinicianData() {
        viewedClinician = State.getClinicianManager().getClinicianByStaffId(viewedClinician.getStaffId())
                .orElseThrow(IllegalStateException::new);
        mainController.setTitle("Clinician profile: " + viewedClinician.getFullName());
        password.setText(viewedClinician.getPassword());
        title.setText(viewedClinician.getFirstName());

        fname.setText(viewedClinician.getFirstName());
        mname.setText(viewedClinician.getMiddleName());
        lname.setText(viewedClinician.getLastName());
        workAddress.setText(viewedClinician.getWorkAddress());
        staffIdLabel.setText(Integer.toString(viewedClinician.getStaffId()));
        country.setValue(viewedClinician.getCountry());

        if (viewedClinician.getCountry() == Country.NZ) {
            regionCB.setValue(Region.fromString(viewedClinician.getRegion()));
        } else {
            regionTF.setText(viewedClinician.getRegion());
        }
        checkClinicianCountry();


        creationDate.setText(formatter.format(viewedClinician.getCreatedOn()));
        if (viewedClinician.getModifiedOn() == null) {
            lastModified.setText("Not yet modified.");
        } else {
            lastModified.setText(formatter.format(viewedClinician.getModifiedOn()));
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
                lastModified.setText(formatter.format(viewedClinician.getModifiedOn()));
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

    private void updateCountries() {
        EnumSet<Country> countries = EnumSet.noneOf(Country.class);
        countries.addAll(State.getConfigManager().getAllowedCountries());
        country.setItems(FXCollections.observableArrayList(countries));
        if (viewedClinician != null && viewedClinician.getCountry() != null) {
            country.setValue(viewedClinician.getCountry());
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
        addChangeIfDifferent(modifyClinicianObject, viewedClinician, "country", country.getValue());


        if (country.getValue() != null && country.getValue() == Country.NZ) {
            addChangeIfDifferent(modifyClinicianObject, viewedClinician, "region", regionCB.getValue().toString());
        } else {
            addChangeIfDifferent(modifyClinicianObject, viewedClinician, "region", regionTF.getText());
        }

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
                    + "the server, it may have been deleted", mainController.getStage());
            return false;
        } catch (ServerRestException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            PageNavigator.showAlert(AlertType.WARNING, "Server error", "Could not apply changes on the server, "
                    + "please try again later", mainController.getStage());
            return false;
        } catch (IfMatchFailedException e) {
            LOGGER.log(Level.INFO, "If-Match did not match");
            PageNavigator.showAlert(AlertType.WARNING, "Outdated Data",
                    "The clinician has been modified since you retrieved the data.\nIf you would still like to "
                            + "apply these changes please submit again, otherwise refresh the page to update the data.", mainController.getStage());
            return false;
        }
    }
}
