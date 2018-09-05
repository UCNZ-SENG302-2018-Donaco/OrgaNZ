package com.humanharvest.organz.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
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

        // Hospital selector
        hospitalSelector.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> newHospitalSelected());
        hospitalSelector.getSelectionModel().select(0);  // Select the first hospital by default

        hospitalSelector.getItems().setAll(State.getConfigManager().getHospitals());

        // Country selector
        Set<Country> selectedCountries = State.getConfigManager().getAllowedCountries();
        List<Country> allCountries = Arrays.asList(Country.values());
        SortedList<Country> countryList = getCountryListSortedByIfInCollection(allCountries, selectedCountries);

        allowedCountries.getItems().setAll(countryList);

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

        // Generate notification
        Notifications.create().title("Updated Countries").text("Allowed countries have been updated").showInformation();

        // Sort the list, so that the updated allowed countries are at the top
        ObservableList<Country> selectedCountries = allowedCountries.getCheckModel().getCheckedItems();
        List<Country> allCountries = Arrays.asList(Country.values());
        SortedList<Country> countryList = getCountryListSortedByIfInCollection(allCountries, selectedCountries);
        // Reset the list's items
        allowedCountries.setItems(countryList);
        // Re-check every country was that checked before
        for (Country country : selectedCountries) {
            allowedCountries.getCheckModel().check(country);
        }

        // Update hospital transplant programs
        // todo update all hospital's transplant programs that have changed, not just the currently selected one
        EnumSet<Organ> transplantProgram = EnumSet.noneOf(Organ.class);
        transplantProgram.addAll(organSelector.getCheckModel().getCheckedItems());

        Hospital selectedHospital = hospitalSelector.getSelectionModel().getSelectedItem();
        if (selectedHospital != null) {
            State.getConfigResolver().setTransplantProgramsForHospital(selectedHospital, transplantProgram);
            Notifications.create().title("Updated transplant program").text("Transplant program has been updated for "
                    + selectedHospital.getName()).showInformation();
        }
    }

    /**
     * Given a list of countries and a collection of countries which the user has selected,
     * returns a list that is sorted so that all "selected" countries are before the non-selected countries,
     * but the order is otherwise determined by Country's default sort.
     *
     * @param countries the full list of countries
     * @param selectedCountries the subcollection of the list that the user has selected
     * @return the sorted list of countries
     */
    private SortedList<Country> getCountryListSortedByIfInCollection(List<Country> countries,
            Collection<Country> selectedCountries) {
        SortedList<Country> countryList =
                new SortedList<>(FXCollections.observableArrayList(countries));

        countryList.setComparator((o1, o2) -> {
            // Sort so that selected countries are at the top, but are otherwise sorted by default country sorting
            if ((selectedCountries.contains(o1) && selectedCountries.contains(o2))
                    || (!selectedCountries.contains(o1) && !selectedCountries.contains(o2))) {
                // either both selected or neither are selected
                return o1.compareTo(o2);
            } else {
                if (selectedCountries.contains(o1)) {
                    // just contains o1
                    return -1;
                } else {
                    // just contains o2
                    return 1;
                }
            }
        });

        return countryList;
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