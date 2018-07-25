package com.humanharvest.organz.controller.client;

import static com.humanharvest.organz.controller.clinician.ViewBaseController.addChangeIfDifferent;

import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.utilities.JSONConverter;
import java.time.format.DateTimeFormatter;

import javax.management.Notification;

import com.humanharvest.organz.utilities.view.Page;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.client.ModifyClientAction;
import com.humanharvest.organz.actions.client.ModifyClientOrgansAction;
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
    private ActionInvoker invoker;
    private ClientManager manager;
    private Client client;

    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:MM:SS");

    @FXML
    private TextField deathTimeField;

    @FXML
    private DatePicker deathDatePicker;

    @FXML
    private TextField deathCountry;

    @FXML
    private TextField deathRegion;

    @FXML
    private TextField deathCity;

    @FXML
    private Button applyButton;

    @FXML
    private Button cancelButton;

    public EditDeathDetailsController(){
        session = State.getSession();
        invoker = State.getInvoker();
        manager = State.getClientManager();

    }


    @FXML
    public void initialize(){
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);

        if (session.getLoggedInUserType() == UserType.CLIENT) {
            client = session.getLoggedInClient();
            deathTimeField.setDisable(true);
            deathDatePicker.setDisable(true);
            deathCountry.setEditable(false);
            deathRegion.setEditable(false);
            deathCity.setEditable(false);
            if (client.isDead()) {
                if (client.getTimeOfDeath() != null) {
                    deathTimeField.setText(client.getTimeOfDeath().toString());
                }
                if (client.getCurrentAddress() != null) {
                    deathCountry.setText(client.getCurrentAddress());
                }
                if (client.getRegion() != null) {
                    deathRegion.setText(client.getRegion().toString());
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
                    deathCountry.setText(client.getCountryOfDeath());
                }
                if (client.getRegionOfDeath() != null) {
                    deathRegion.setText(client.getRegionOfDeath());
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

        public void applyChanges () {
            if (session.getLoggedInUserType() == UserType.CLIENT) {
                PageNavigator.showAlert(AlertType.ERROR, "Invalid Access", "Clients cannot edit death details");
            } else {
                ModifyClientObject modifyClientObject = new ModifyClientObject();
                addChangeIfDifferent(modifyClientObject, client, "dateOfDeath", deathDatePicker.getValue());
                addChangeIfDifferent(modifyClientObject, client, "timeOfDeath", deathTimeField.getText());
                addChangeIfDifferent(modifyClientObject, client, "regionOfDeath", deathRegion.getText());
                addChangeIfDifferent(modifyClientObject, client, "cityOfDeath", deathCity.getText());
                addChangeIfDifferent(modifyClientObject, client, "countryOfDeath", deathCountry.getText());

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



                    HistoryItem save = new HistoryItem("UPDATE CLIENT INFO",
                        String.format("Updated client %s with values: %s", client.getFullName(), actionText));
                    JSONConverter.updateHistory(save, "action_history.json");
                }

            }

        }

    }
