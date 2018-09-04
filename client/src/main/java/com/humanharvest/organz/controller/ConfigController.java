package com.humanharvest.organz.controller;

import java.util.EnumSet;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.Notifications;

import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;

public class ConfigController extends SubController {

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
        hospitalSelector.getSelectionModel().select(0);  // Select the first hospital by default

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
            Optional<Hospital> hospital = State.getConfigManager().getHospitalById(selectedHospital.getId());
            if (hospital.isPresent()) {
                organSelector.getItems().clear();
                organSelector.getItems().setAll(Organ.values());

                for (Organ organ : hospital.get().getTransplantPrograms()) {
                    organSelector.getCheckModel().check(organ);
                }
            }
        }

        System.out.println(State.getConfigManager().getAllowedCountries().size());
        allowedCountries.getCheckModel().clearChecks();
        for (Country country : State.getConfigManager().getAllowedCountries()) {
            allowedCountries.getCheckModel().check(country);
        }
    }

    /**
     * Sets the countries selected as the allowed countries, and updates hospital transplant programs
     */
    @FXML
    private void apply() {
        // Update allowed countries
        EnumSet<Country> allowedCountriesSet = EnumSet.noneOf(Country.class);
        allowedCountriesSet.addAll(allowedCountries.getCheckModel().getCheckedItems());

        State.getConfigManager().setAllowedCountries(allowedCountriesSet);
        Notifications.create().title("Updated Countries").text("Allowed countries have been updated").showInformation();

        // Update hospital transplant programs
        EnumSet<Organ> transplantProgram = EnumSet.noneOf(Organ.class);
        transplantProgram.addAll(organSelector.getCheckModel().getCheckedItems());

        Hospital selectedHospital = hospitalSelector.getSelectionModel().getSelectedItem();
        if (selectedHospital != null) {
            State.getConfigResolver().setTransplantProgramsForHospital(selectedHospital, transplantProgram);
            Notifications.create().title("Updated transplant program").text("Transplant program has been updated for "
                    + selectedHospital.getName()).showInformation();
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
