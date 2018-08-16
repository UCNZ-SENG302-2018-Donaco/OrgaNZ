package com.humanharvest.organz.controller;

import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.Notifications;

import java.util.EnumSet;

;

public class ConfigController extends SubController {

    @FXML
    CheckComboBox<Country> countries;

    @FXML
    HBox menuBarPane;

    public ConfigController() {
    }

    @FXML
    private void initialize() {
        countries.getItems().setAll(Country.values());
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
        countries.getCheckModel().clearChecks();
        for (Country country: State.getConfigManager().getAllowedCountries()) {
            countries.getCheckModel().check(country);
        }
    }

    /**
     * Sets the countries selected as the allowed countries
     */
    @FXML
    private void apply() {
        EnumSet<Country> allowedCountries = EnumSet.noneOf(Country.class);
        allowedCountries.addAll(countries.getCheckModel().getCheckedItems());

        State.getConfigManager().setAllowedCountries(allowedCountries);
        Notifications.create().title("Updated Countries").text("Allowed countries have been updated").showInformation();
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
