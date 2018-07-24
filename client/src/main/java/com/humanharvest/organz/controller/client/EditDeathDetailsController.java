package com.humanharvest.organz.controller.client;

import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.PageNavigator;

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
    public void setup(MainController mainController){
        super.setup(mainController);

        if(session.getLoggedInUserType() == UserType.CLIENT){
            client = session.getLoggedInClient();
            deathTimeField.setEditable(false);
            System.out.println(client.isDead());
            if(client.isDead()){
                if (client.getTimeOfDeath() != null){
                    deathTimeField.setText(client.getTimeOfDeath().toString());
                }
                deathDatePicker.setValue(client.getDateOfDeath());
                deathCountry.setText(client.getCurrentAddress());
                deathRegion.setText(client.getRegion().toString());
                deathCity.setText(client.getCurrentAddress());

            }




        }
        //System.out.println(windowContext.isClinViewClientWindow());
        if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
        }

    }

    public void applyChanges(){
        if(session.getLoggedInUserType() == UserType.CLIENT){
            PageNavigator.showAlert(AlertType.ERROR, "Invalid Access","Clients cannot edit death details");
        } else {
            client.setDateOfDeath(deathDatePicker.getValue());
            //client.setTimeOfDeath(timeFormat.format(deathTimeField.getText()));
            //client.setRegion(deathRegion.getText());

        }

    }





}
