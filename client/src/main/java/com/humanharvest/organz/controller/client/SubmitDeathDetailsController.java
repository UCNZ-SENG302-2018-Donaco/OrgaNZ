package com.humanharvest.organz.controller.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.views.client.ModifyClientObject;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.controlsfx.control.Notifications;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.humanharvest.organz.controller.clinician.ViewBaseController.addChangeIfDifferent;

public class SubmitDeathDetailsController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(SubmitDeathDetailsController.class.getName());

    private Client client;

    @FXML
    private DatePicker deathDatePicker;
    @FXML
    private TextField deathTimeField;
    @FXML
    private ChoiceBox<Country> deathCountry;
    @FXML
    private ChoiceBox<Region> deathRegionCB;
    @FXML
    private TextField deathRegionTF;
    @FXML
    private TextField deathCity;

    @FXML
    private void initialize() {
        deathCountry.valueProperty().addListener(change -> enableAppropriateRegionInput());
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Details of Death");
        refresh();
    }

    @Override
    public void refresh() {
        super.refresh();

        // Get allowed countries & regions
        deathCountry.setItems(FXCollections.observableArrayList(State.getConfigManager().getAllowedCountries()));
        deathRegionCB.setItems(FXCollections.observableArrayList(Region.values()));

        // Populate fields with default values
        client = windowContext.getViewClient();
        deathDatePicker.setValue(LocalDate.now());
        deathCountry.setValue(client.getCountry());
        if (client.getCountry() == Country.NZ) {
            deathRegionCB.setValue(Region.fromString(client.getRegion()));
        } else {
            deathRegionTF.setText(client.getRegion());
        }
        deathCity.setText(client.getCurrentAddress());
    }

    /**
     * Checks the clients country, changes region input to a choicebox of NZ regions if the country is
     * New Zealand, and changes to a textfield input for any other country
     */
    private void enableAppropriateRegionInput() {
        if (deathCountry.getValue() == Country.NZ) {
            deathRegionCB.setManaged(true);
            deathRegionCB.setVisible(true);
            deathRegionTF.setManaged(false);
            deathRegionTF.setVisible(false);
        } else {
            deathRegionCB.setManaged(false);
            deathRegionCB.setVisible(false);
            deathRegionTF.setManaged(true);
            deathRegionTF.setVisible(true);
        }
    }

    @FXML
    private void submit() {
        ModifyClientObject modifyClientObject = new ModifyClientObject();

        // Validate all fields
        try {
            addChangeIfDifferent(modifyClientObject, client, "dateOfDeath", deathDatePicker.getValue());
            addChangeIfDifferent(modifyClientObject, client, "timeOfDeath",
                    LocalTime.parse(deathTimeField.getText()));
        } catch (DateTimeParseException e) {
            PageNavigator.showAlert(AlertType.WARNING, "Incorrect time format",
                    "Please enter the time of death in this format: 'HH:mm:ss'");
            return;
        } catch (NullPointerException e) {
            PageNavigator.showAlert(AlertType.WARNING, "Required data missing",
                    "Date of death and time of death are required.");
            return;
        }
        if (deathDatePicker.getValue().isAfter(LocalDate.now()) ||
                deathDatePicker.getValue().isBefore(client.getDateOfBirth())) {
            PageNavigator.showAlert(AlertType.WARNING, "Incorrect Date",
                    "Date of death cannot be in the future, or before the client's birth.");
            return;
        }

        if (deathCountry.getValue() == null) {
            PageNavigator.showAlert(AlertType.WARNING, "Required data missing",
                    "Country of death is required.");
            return;
        } else {
            addChangeIfDifferent(modifyClientObject, client, "countryOfDeath", deathCountry.getValue());
        }
        if (deathCountry.getValue() == Country.NZ) {
            if (deathRegionCB.getValue() == null) {
                PageNavigator.showAlert(AlertType.WARNING, "Required data missing",
                        "Region of death is required.");
                return;
            } else {
                addChangeIfDifferent(modifyClientObject, client, "regionOfDeath",
                        deathRegionCB.getValue().toString());
            }
        } else {
            if (deathRegionTF.getText().isEmpty()) {
                PageNavigator.showAlert(AlertType.WARNING, "Required data missing",
                        "Region of death is required.");
                return;
            } else {
                addChangeIfDifferent(modifyClientObject, client, "regionOfDeath", deathRegionTF.getText());
            }
        }
        if (deathCity.getText().isEmpty()) {
            PageNavigator.showAlert(AlertType.WARNING, "Required data missing",
                    "City of death is required.");
            return;
        } else {
            addChangeIfDifferent(modifyClientObject, client, "cityOfDeath", deathCity.getText());
        }

        // All valid, all death details registered as changes
        // Check that user really wants to mark as dead
        ButtonType buttonOpt = PageNavigator.showAlert(AlertType.CONFIRMATION,
                "Are you sure you want to mark this client as dead?",
                "This will cancel all waiting transplant requests for this client.")
                .orElse(ButtonType.CANCEL);

        if (buttonOpt == ButtonType.OK && makeRequest(modifyClientObject)) {
            Notifications.create()
                    .title("Marked Client as Dead")
                    .text("All organ transplant requests have been cancelled, "
                            + "and their details of death has been stored.")
                    .showConfirm();
            mainController.getStage().close();
            PageNavigator.refreshAllWindows();
        }
    }

    @FXML
    private void cancel() {
        mainController.getStage().close();
    }

    private boolean makeRequest(ModifyClientObject modifyClientObject) {
        try {
            State.getClientResolver().modifyClientDetails(client, modifyClientObject);
            return true;
        } catch (NotFoundException e) {
            LOGGER.log(Level.WARNING, "Client not found");
            PageNavigator.showAlert(
                    AlertType.WARNING,
                    "Client not found",
                    "The client could not be found on the server, it may have been deleted");
            return false;
        } catch (ServerRestException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            PageNavigator.showAlert(
                    AlertType.WARNING,
                    "Server error",
                    "Could not apply changes on the server, please try again later");
            return false;
        } catch (IfMatchFailedException e) {
            LOGGER.log(Level.INFO, "If-Match did not match");
            PageNavigator.showAlert(
                    AlertType.WARNING,
                    "Outdated Data",
                    "The client has been modified since you retrieved the data.\n"
                            + "If you would still like to apply these changes please submit again, "
                            + "otherwise refresh the page to update the data.");
            return false;
        }
    }
}
