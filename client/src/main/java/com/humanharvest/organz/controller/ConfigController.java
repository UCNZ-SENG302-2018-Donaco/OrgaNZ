package com.humanharvest.organz.controller;

import java.awt.MenuBar;
import java.util.EnumSet;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.Notifications;
import sun.applet.Main;

public class ConfigController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(ConfigController.class.getName());
    private final Session session;

    @FXML
    CheckComboBox<Country> countries;

    @FXML
    HBox menuBarPane;

    public ConfigController() {
        session = State.getSession();
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

    @Override
    public void refresh() {
        System.out.println(State.getConfigManager().getAllowedCountries());
        countries.getCheckModel().clearChecks();
        for (Country country: State.getConfigManager().getAllowedCountries()) {
            countries.getCheckModel().check(country);
        }
    }

    @FXML
    private void apply() {
        EnumSet<Country> allowedCountries = EnumSet.noneOf(Country.class);
        allowedCountries.addAll(countries.getCheckModel().getCheckedItems());

        State.getConfigManager().setAllowedCountries(allowedCountries);
        Notifications.create().title("Updated Countries").text("Allowed countries have been updated").showInformation();
    }

    @FXML
    private void selectAll() {
        EnumSet<Country> allowedCountries = EnumSet.allOf(Country.class);
        State.getConfigManager().setAllowedCountries(allowedCountries);

        refresh();
    }

    @FXML
    private void selectNone() {
        EnumSet<Country> allowedCountries = EnumSet.noneOf(Country.class);
        State.getConfigManager().setAllowedCountries(allowedCountries);

        refresh();
    }

    @FXML
    private void cancel() {
        refresh();
    }

}
