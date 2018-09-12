package com.humanharvest.organz.controller.administrator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.Notifications;

import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.view.PageNavigator;

public class ConfigController extends SubController {

    @FXML
    private HBox menuBarPane;
    @FXML
    private CheckListView<Country> allowedCountries;
    @FXML
    private ListView<Hospital> hospitalSelector;
    @FXML
    private CheckListView<Organ> organSelector;

    private Map<Hospital, Set<Organ>> modifiedHospitalPrograms = new HashMap<>();
    private ListChangeListener<? super Organ> programsChangeListener = change -> onTransplantProgramsChanged();

    public ConfigController() {
    }

    @FXML
    private void initialize() {
        // Hospital selector
        hospitalSelector.getItems().setAll(State.getConfigManager().getHospitals());
        hospitalSelector.getItems().sort(Comparator.comparing(Hospital::getName));
        hospitalSelector.getSelectionModel().selectedItemProperty().addListener(observable -> newHospitalSelected());
        hospitalSelector.getSelectionModel().select(0);  // Select the first hospital by default

        // Double-click to open hospital details
        hospitalSelector.setOnMouseClicked(click -> {
                    if (click.getClickCount() == 2) { // double-click
                        Hospital hospital = hospitalSelector.getSelectionModel().getSelectedItem();
                        generateHospitalAlert(hospital);
                    }
                }
        );

        // Country selector
        Set<Country> selectedCountries = State.getConfigManager().getAllowedCountries();
        List<Country> allCountries = Arrays.asList(Country.values());
        SortedList<Country> countryList = getCountryListSortedByIfInCollection(allCountries, selectedCountries);

        allowedCountries.getItems().setAll(countryList);

    }

    /**
     * Generates an information alert that displays details about a hospital.
     * Displays name, address, and co-ordinates.
     *
     * @param hospital the hospital to display information about
     */
    private void generateHospitalAlert(Hospital hospital) {
        // Note that 5 decimal places is used for lat-long to get accuracy of 1.1 metres.
        String information = String.format("Address: %s.%nLocation: (%.5f, %.5f).",
                hospital.getAddress(), hospital.getLatitude(), hospital.getLongitude());
        PageNavigator.showAlert(AlertType.INFORMATION, hospital.getName(), information, mainController.getStage());
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
        allowedCountries.getCheckModel().clearChecks();
        for (Country country : State.getConfigManager().getAllowedCountries()) {
            allowedCountries.getCheckModel().check(country);
        }
        modifiedHospitalPrograms.clear();
        newHospitalSelected();
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
        // Send requests for each hospital which has had its programs modified
        if (!modifiedHospitalPrograms.isEmpty()) {
            for (Hospital hospital : modifiedHospitalPrograms.keySet()) {
                State.getConfigResolver()
                        .setTransplantProgramsForHospital(hospital, modifiedHospitalPrograms.get(hospital));
            }
            Notifications.create()
                    .title("Updated Transplant Programs")
                    .text(String.format("Transplant programs have been updated for: \n%s.",
                            modifiedHospitalPrograms.keySet().stream()
                                    .map(Hospital::getName)
                                    .collect(Collectors.joining(", \n"))))
                    .showInformation();
        }
        refresh();
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
        Hospital selectedHospital = hospitalSelector.getSelectionModel().getSelectedItem();
        if (selectedHospital != null) {
            // Remove the listener so it doesn't pick up these changes
            organSelector.getCheckModel().getCheckedItems().removeListener(programsChangeListener);
            organSelector.getCheckModel().clearChecks();

            // Reset the transplant programs selector
            organSelector.getItems().clear();
            organSelector.getItems().setAll(Organ.values());

            if (modifiedHospitalPrograms.keySet().contains(selectedHospital)) {
                // If programs for this hospital have been modified, use its modified programs for checked values
                for (Organ organ : modifiedHospitalPrograms.get(selectedHospital)) {
                    organSelector.getCheckModel().check(organ);
                }
            } else {
                // Else, use current programs for checked values
                for (Organ organ : selectedHospital.getTransplantPrograms()) {
                    organSelector.getCheckModel().check(organ);
                }
            }

            // Re-add the listener so that it picks up the changes again
            organSelector.getCheckModel().getCheckedItems().addListener(programsChangeListener);
        }
    }

    private void onTransplantProgramsChanged() {
        // Determine the changed programs
        Set<Organ> newPrograms = EnumSet.noneOf(Organ.class);
        newPrograms.addAll(organSelector.getCheckModel().getCheckedItems());

        // Put the modified programs into an entry for that hospital
        modifiedHospitalPrograms.put(
                hospitalSelector.getSelectionModel().getSelectedItem(),
                newPrograms);
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
