package com.humanharvest.organz.controller.client;

import static com.humanharvest.organz.controller.clinician.ViewBaseController.addChangeIfDifferent;

import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.utilities.JSONConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Region;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.views.client.ModifyClientObject;

import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.converter.DateTimeStringConverter;

import org.controlsfx.control.Notifications;

public class EditDeathDetailsController extends SubController{

    private Session session;
    private ClientManager manager;
    private Client client;

    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML
    private TextField deathTimeField;

    @FXML
    private DatePicker deathDatePicker;

    @FXML
    private TextField deathCity;

    @FXML
    private Button applyButton;

    @FXML
    private ChoiceBox<Country> deathCountry;

    @FXML
    private ChoiceBox<Region> deathRegionCB;
    @FXML
    private TextField deathRegionTF;

    public EditDeathDetailsController(){
        session = State.getSession();
        manager = State.getClientManager();

    }


    @FXML
    public void initialize(){
        deathCountry.setItems(FXCollections.observableArrayList(Country.values()));
        deathRegionCB.setItems(FXCollections.observableArrayList(Region.values()));
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);

        mainController.setTitle("Details of Death");

        if (session.getLoggedInUserType() == UserType.CLIENT) {
            client = session.getLoggedInClient();
            deathTimeField.setDisable(true);
            deathDatePicker.setDisable(true);
            deathCountry.setDisable(true);
            deathRegionTF.setEditable(false);
            deathRegionCB.setDisable(true);
            deathCity.setEditable(false);
            if (client.isDead()) {
                if (client.getTimeOfDeath() != null) {
                    deathTimeField.setText(timeFormat.format(client.getTimeOfDeath()));
                }
                if (client.getCurrentAddress() != null) {
                    deathCountry.setValue(client.getCountryOfDeath());
                }
                if (client.getRegionOfDeath() != null && client.getCountry() == Country.NZ) {
                   deathRegionCB.setValue(Region.fromString(client.getRegionOfDeath()));
                   deathRegionTF.setVisible(false);
                }
                else if (client.getRegionOfDeath() != null && client.getCountry() != Country.NZ) {
                    deathRegionTF.setText(client.getRegionOfDeath());
                    deathRegionCB.setVisible(false);
                }
                if (client.getDateOfDeath() != null) {
                    deathDatePicker.setValue(client.getDateOfDeath());
                }
                if (client.getCityOfDeath() != null) {
                    deathCity.setText(client.getCurrentAddress());
                }

            }

        }

        deathCountry.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(Country.NZ)) {
                deathRegionCB.setVisible(true);
                deathRegionTF.setVisible(false);
                deathRegionTF.setEditable(false);
            } else {
                deathRegionTF.setVisible(true);
                deathRegionTF.setEditable(true);
                deathRegionCB.setVisible(false);
            }
        });

        if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
            if (client.isDead()) {
                if (client.getTimeOfDeath() != null) {
                    deathTimeField.setText(timeFormat.format(client.getTimeOfDeath()));
                }
                if (client.getCountryOfDeath() != null) {
                    deathCountry.setValue(client.getCountryOfDeath());
                }
                if (client.getRegionOfDeath() !=  null) {
                    if (client.getCountryOfDeath() == Country.NZ) {
                        deathRegionCB.setValue(Region.fromString(client.getRegionOfDeath()));
                    }
                    else {
                        deathRegionTF.setText(client.getRegionOfDeath());
                    }
                }
                if (client.getDateOfDeath() != null) {
                    deathDatePicker.setValue(client.getDateOfDeath());
                }
                if (client.getCityOfDeath() != null) {
                    deathCity.setText(client.getCityOfDeath());
                }
            }

            else {
                deathCountry.setValue(client.getCountry());
                deathCity.setText(client.getCurrentAddress());
                if (client.getCountry() == Country.NZ) {
                    deathRegionCB.setVisible(true);
                    deathRegionCB.setValue(deathRegionCB.getValue());
                    deathRegionTF.setVisible(false);
                } else if (client.getCountry() != Country.NZ){
                    deathRegionCB.setVisible(false);
                    deathRegionTF.setVisible(true);
                    deathRegionTF.setText(client.getRegion());
                }
            }

        }

    }

    /**
     * Triggered when the value of the country choicebox is changed
     */
    @FXML
    private void countryChanged() {
        checkCountry();
    }

    /**
     * Checks the clients country, changes region input to a choicebox of NZ regions if the country is New Zealand,
     * and changes to a textfield input for any other country
     */
    private void checkCountry() {
        if (client.getCountryOfDeath() == Country.NZ ) {
            deathRegionCB.setVisible(true);
            deathRegionTF.setVisible(false);
        } else {
            deathRegionCB.setVisible(false);
            deathRegionTF.setVisible(true);
        }
    }


    public void applyChanges () {
        ModifyClientObject modifyClientObject = new ModifyClientObject();

        if (session.getLoggedInUserType() == UserType.CLIENT) {
            PageNavigator.showAlert(AlertType.ERROR, "Invalid Access", "Clients cannot edit death details");
        } else {
            addChangeIfDifferent(modifyClientObject, client, "dateOfDeath", deathDatePicker.getValue());
            try {
                addChangeIfDifferent(modifyClientObject, client, "timeOfDeath",
                        LocalTime.parse(deathTimeField.getText()));
            } catch (DateTimeParseException e) {
                PageNavigator.showAlert(AlertType.WARNING, "Incorrect time format", "Please enter the time of death"
                        + " in 'HH:mm:ss'");
            }
            addChangeIfDifferent(modifyClientObject, client, "cityOfDeath", deathCity.getText());
            addChangeIfDifferent(modifyClientObject, client, "countryOfDeath", deathCountry.getValue());

            if (deathCountry.getValue() == Country.NZ) {
                addChangeIfDifferent(modifyClientObject, client, "regionOfDeath", deathRegionCB.getValue().toString());
            }
            else {
                addChangeIfDifferent(modifyClientObject, client,"regionOfDeath", deathRegionTF.getText());
            }
            if (modifyClientObject.getModifiedFields().isEmpty()) {
                Notifications.create()
                        .title("No changes were made.")
                        .text("No changes were made to the client.")
                        .showWarning();
            }

            else {
                client = State.getClientResolver().modifyClientDetails(client, modifyClientObject);
                String actionText = modifyClientObject.toString();
                Notifications.create().title("Updated Death Details").text(actionText).showInformation();
                checkCountry();
                Stage stage = (Stage) applyButton.getScene().getWindow();
                stage.close();
                PageNavigator.refreshAllWindows();

                HistoryItem save = new HistoryItem("UPDATE CLIENT INFO",
                        String.format("Updated client %s with values: %s", client.getFullName(), actionText));
                JSONConverter.updateHistory(save, "action_history.json");
                }

            if (client.getDateOfDeath() == null) {
                PageNavigator.showAlert(AlertType.WARNING, "Date of death must be set", "You must set the date of "
                        + "death to apply changes ");
            }

            }

        }

    }
