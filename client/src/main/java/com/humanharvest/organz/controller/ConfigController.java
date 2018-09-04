package com.humanharvest.organz.controller;

import java.util.EnumSet;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.Notifications;

import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;

import com.sun.org.apache.xpath.internal.operations.Or;

public class ConfigController extends SubController {

    @FXML
    CheckComboBox<Country> countries;

    @FXML
    HBox menuBarPane;

    @FXML
    CheckListView<Country> allowedCountries;

    @FXML
    ListView<Hospital> hospitalSelector;

    @FXML
    CheckListView<Organ> organSelector;

    public ConfigController() {
    }

    @FXML
    private void initialize() {

        hospitalSelector.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> newHospitalSelected());

        countries.getItems().setAll(Country.values());
        allowedCountries.getItems().setAll(Country.values());
        hospitalSelector.getItems().setAll(State.getConfigManager().getHospitals());
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Settings");
        mainController.loadMenuBar(menuBarPane);
        refresh();
    }

    /**
     * Fetches the current allowed countries and checks them in the combocheckboxes
     */
    @Override
    public void refresh() {
        Hospital selectedHospital = hospitalSelector.getSelectionModel().getSelectedItem();
        if (selectedHospital != null) {
            organSelector.getItems().clear();
            organSelector.getItems().setAll(Organ.values());
            for (Organ organ : selectedHospital.getTransplantPrograms()) {
                organSelector.getCheckModel().check(organ);
            }
        }

        allowedCountries.getCheckModel().clearChecks();
        for (Country country : State.getConfigManager().getAllowedCountries()) {
            allowedCountries.getCheckModel().check(country);
        }

        countries.getCheckModel().clearChecks();
        for (Country country : State.getConfigManager().getAllowedCountries()) {
            countries.getCheckModel().check(country);
        }
    }

    /**
     * Sets the countries selected as the allowed countries, and updates hospital transplant programs
     */
    @FXML
    private void apply() {
        // Update allowed countries
        EnumSet<Country> allowedCountries = EnumSet.noneOf(Country.class);
        allowedCountries.addAll(countries.getCheckModel().getCheckedItems());

        State.getConfigManager().setAllowedCountries(allowedCountries);
        Notifications.create().title("Updated Countries").text("Allowed countries have been updated").showInformation();

        // Update hospital transplant programs
        EnumSet<Organ> transplantProgram = EnumSet.noneOf(Organ.class);
        transplantProgram.addAll(organSelector.getCheckModel().getCheckedItems());

        Hospital selectedHospital = hospitalSelector.getSelectionModel().getSelectedItem();
        if (selectedHospital != null) {
            State.getConfigManager().setTransplantProgram(selectedHospital.getId(), transplantProgram);
            Notifications.create().title("Updated transplant program").text("Transplant program has been updated for "
                    + selectedHospital.getName());
        }
    }

    private void newHospitalSelected() {
        refresh();
    }

    /**
     * Sets all countries in the combobox as checked
     */
    @FXML
    private void selectAll() {
        EnumSet<Country> allowedCountries = EnumSet.allOf(Country.class);
        State.getConfigManager().setAllowedCountries(allowedCountries);

        refresh();
    }

    /**
     * Clears all countries so that none are selected
     */
    @FXML
    private void selectNone() {
        EnumSet<Country> allowedCountries = EnumSet.noneOf(Country.class);
        State.getConfigManager().setAllowedCountries(allowedCountries);

        refresh();
    }

    /**
     * Resets the countries selected
     */
    @FXML
    private void cancel() {
        refresh();
    }

}
