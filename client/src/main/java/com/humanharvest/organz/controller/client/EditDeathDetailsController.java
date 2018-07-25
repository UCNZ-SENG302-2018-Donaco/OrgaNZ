package com.humanharvest.organz.controller.client;

import static com.humanharvest.organz.controller.clinician.ViewBaseController.addChangeIfDifferent;

import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.utilities.JSONConverter;
import java.time.format.DateTimeFormatter;


import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Region;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;;
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
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.views.client.ModifyClientObject;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

public class EditDeathDetailsController extends SubController{

    private Session session;
    private ClientManager manager;
    private Client client;

    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:MM:SS");

    @FXML
    private TextField deathTimeField;

    @FXML
    private DatePicker deathDatePicker;



    @FXML
    private TextField deathCity;

    @FXML
    private Button applyButton;

    @FXML
    private ChoiceBox deathCountry;

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
                    deathTimeField.setText(client.getTimeOfDeath().toString());
                }
                if (client.getCurrentAddress() != null) {
                    deathCountry.setValue(client.getCountryOfDeath());
                }
                if (client.getRegion() != null && client.getCountry() == Country.NZ) {
                    deathRegionCB.setValue(deathRegionCB.getValue());
                }
                else if (client.getRegion() != null && client.getCountry() != Country.NZ) {
                    deathRegionTF.setText(client.getRegionOfDeath());
                }
                if (client.getDateOfDeath() != null) {
                    deathDatePicker.setValue(client.getDateOfDeath());
                }
                if (client.getCityOfDeath() != null) {
                    deathCity.setText(client.getCurrentAddress());
                }

            }

        }
        if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
            if (client.isDead()) {
                if (client.getTimeOfDeath() != null) {
                    deathTimeField.setText(client.getTimeOfDeath());
                }
                if (client.getCountryOfDeath() != null) {
                    deathCountry.setValue(client.getCountryOfDeath());
                }
                if (client.getRegionOfDeath() != null && client.getCountry() == Country.NZ) {
                    deathRegionCB.setValue(deathRegionCB.getValue());
                }
                else if (client.getRegionOfDeath() != null && client.getCountry() != Country.NZ) {
                    deathRegionTF.setText(client.getRegionOfDeath());
                }
                if (client.getDateOfDeath() != null) {
                    deathDatePicker.setValue(client.getDateOfDeath());
                }
                if (client.getCityOfDeath() != null) {
                    deathCity.setText(client.getCityOfDeath());
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
        if (client.getCountry() == Country.NZ ) {
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
                addChangeIfDifferent(modifyClientObject, client, "timeOfDeath", deathTimeField.getText());
                addChangeIfDifferent(modifyClientObject, client, "regionOfDeath", deathRegionTF.getText());
                addChangeIfDifferent(modifyClientObject, client, "regionOfDeath", deathRegionCB.getValue());
                addChangeIfDifferent(modifyClientObject, client, "cityOfDeath", deathCity.getText());
                addChangeIfDifferent(modifyClientObject, client, "countryOfDeath", deathCountry.getSelectionModel()
                        .toString());

                if (client.getCountry() == Country.NZ) {
                    addChangeIfDifferent(modifyClientObject, client, "region", deathRegionCB.getValue());
                } else {
                    addChangeIfDifferent(modifyClientObject, client,"region", deathRegionTF.getText());

                }

                if (modifyClientObject.getModifiedFields().isEmpty()) {
                    Notifications.create()
                            .title("No changes were made.")
                            .text("No changes were made to the client.")
                            .showWarning();
                } else {

                    client = State.getClientResolver().modifyClientDetails(client, modifyClientObject);
                    String actionText = modifyClientObject.toString();
                    System.out.println(modifyClientObject.getRegionOfDeath());
                    System.out.println(modifyClientObject.getCountryOfDeath());
                    System.out.println(client.getCountryOfDeath());
                    Notifications.create().title("Updated Death Details").text(actionText).showInformation();

                    Stage stage = (Stage) applyButton.getScene().getWindow();
                    stage.close();

                    PageNavigator.refreshAllWindows();

                    HistoryItem save = new HistoryItem("UPDATE CLIENT INFO",
                        String.format("Updated client %s with values: %s", client.getFullName(), actionText));
                    JSONConverter.updateHistory(save, "action_history.json");
                }

            }

        }

    }
