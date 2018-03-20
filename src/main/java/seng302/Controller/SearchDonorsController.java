package seng302.Controller;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import seng302.Actions.ActionInvoker;
import seng302.Donor;
import seng302.DonorManager;
import seng302.State;
import seng302.Utilities.Gender;
import seng302.Utilities.Region;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.EventListener;

public class SearchDonorsController {

    private DonorManager manager;
    private ArrayList<Donor> donors;

    @FXML
    private TextField searchBox;

    @FXML
    private TableView<Donor> tableView;

    @FXML
    private TableColumn<Donor, Integer> idCol;
    @FXML
    private TableColumn<Donor, String> fnameCol;
    @FXML
    private TableColumn<Donor, String> mnameCol;
    @FXML
    private TableColumn<Donor, String> lnameCol;
    @FXML
    private TableColumn<Donor, Integer> ageCol;
    @FXML
    private TableColumn<Donor, Gender> genderCol;
    @FXML
    private TableColumn<Donor, Region> regionCol;

    private ObservableList<Donor> observableDonorList = FXCollections.observableArrayList();



    @FXML
    private void initialize() {
        manager = State.getDonorManager();
        donors = manager.getDonors();
        observableDonorList.setAll(donors);

        idCol.setCellValueFactory(new PropertyValueFactory<>("uid"));
        fnameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        mnameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        lnameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        regionCol.setCellValueFactory(new PropertyValueFactory<>("region"));

        tableView.setItems(observableDonorList);

        tableView.getColumns().setAll(idCol, fnameCol, mnameCol, lnameCol, ageCol, genderCol, regionCol);

        searchBox.textProperty().addListener(((o) -> refresh()));
    }

    @FXML
    private void refresh() {
        String searchText = searchBox.getText();
        ArrayList<Donor> donorsWithString = new ArrayList<>();
        for (Donor donor : donors) {
            if (donor.nameContains(searchText)) {
                donorsWithString.add(donor);
            }
        }
        observableDonorList.setAll(donorsWithString);
    }


}
