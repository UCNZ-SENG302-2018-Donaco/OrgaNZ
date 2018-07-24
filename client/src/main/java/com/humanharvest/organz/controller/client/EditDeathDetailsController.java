package com.humanharvest.organz.controller.client;

import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.PageNavigator;

public class EditDeathDetailsController extends SubController{

    private Session session;
    private ActionInvoker invoker;
    private ClientManager manager;

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

    }

    public void applyChanges(){

    }





}
