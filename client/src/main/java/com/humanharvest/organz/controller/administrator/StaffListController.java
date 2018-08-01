package com.humanharvest.organz.controller.administrator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.AdministratorManager;
import com.humanharvest.organz.state.ClinicianManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.JSONConverter;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;

public class StaffListController extends SubController {

    private ClinicianManager clinicianManager;
    private AdministratorManager adminManager;

    @FXML
    private HBox menuBarPane;

    @FXML
    private ListView<String> staffList;

    private final String defaultAdminUsername;
    private final String defaultClinicianId;

    public StaffListController() {
        this.adminManager = State.getAdministratorManager();
        this.clinicianManager = State.getClinicianManager();

        this.defaultAdminUsername = adminManager.getDefaultAdministrator().getUsername();
        this.defaultClinicianId = Integer.toString(clinicianManager.getDefaultClinician().getStaffId());
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Staff list");
        mainController.loadMenuBar(menuBarPane);
    }

    @Override
    public void refresh() {
        super.refresh();
        staffList.setItems(getStaffIds());
    }

    private void loadUser(String user) {
        try {
            int staffId = Integer.parseInt(user);
            Optional<Clinician> clinician = State.getClinicianManager().getClinicianByStaffId(staffId);
            State.setViewedClinician(clinician.get());
        } catch (NumberFormatException ex) { // The user is an admin
            System.out.println("admin");

        }


        PageNavigator.loadPage(Page.VIEW_CLINICIAN, mainController);
    }


    @FXML
    private void initialize() {
        staffList.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>();

            MenuItem deleteItem = new MenuItem();
            deleteItem.textProperty().setValue("Delete");
            deleteItem.setOnAction(event -> delete(cell.getItem()));
            ContextMenu contextMenu = new ContextMenu(deleteItem);

            cell.textProperty().bind(cell.itemProperty());

            cell.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                    System.out.println(cell.textProperty().getValue());
                    loadUser(cell.textProperty().getValue());
                    }
                });

            // Listener to disable deleting of defaults

            cell.textProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue == null) {
                    // The new value is null (the cell is now empty)
                    cell.setContextMenu(null);
                    cell.setStyle("");

                } else if (newValue.equals(defaultAdminUsername)
                        || newValue.equals(defaultClinicianId)
                        || newValue.equals(State.getSession().getLoggedInAdministrator().getUsername())) {
                    // It is either the default admin username or default clinician id, or the currently
                    // logged-in admin
                    cell.setContextMenu(null);
                    cell.setStyle("-fx-background-color: grey");

                } else {
                    // Normal cell, with a non-default staff member
                    cell.setContextMenu(contextMenu);
                    cell.setStyle("");
                }
            });

            return cell;
        });

        refresh();
    }

    /**
     * Deletes the staff member (clinician or administrator) represented by "id".
     * @param id the staff member to delete
     */
    private void delete(String id) {
        String actionHistoryFilename = "action_history.json";

        if (id.matches("[0-9]+")) {
            Clinician clinician = clinicianManager.getClinicianByStaffId(Integer.parseInt(id))
                    .orElseThrow(IllegalArgumentException::new);

            State.getClinicianManager().removeClinician(clinician);

            HistoryItem deleteClinician = new HistoryItem("DELETE", "Clinician " + id + " deleted");
            JSONConverter.updateHistory(deleteClinician, actionHistoryFilename);
        } else {
            Administrator administrator = adminManager.getAdministratorByUsername(id)
                    .orElseThrow(IllegalArgumentException::new);

            State.getAdministratorManager().removeAdministrator(administrator);

            HistoryItem deleteAdministrator = new HistoryItem("DELETE", "Administrator " + id + " deleted");
            JSONConverter.updateHistory(deleteAdministrator, actionHistoryFilename);
        }
        PageNavigator.refreshAllWindows();
    }

    /**
     * Returns a list of all staff IDs (clinician IDs and administrator usernames).
     * @return a list of all staff IDs
     */
    private ObservableList<String> getStaffIds() {
        List<String> staffIds = new ArrayList<>();

        List<Clinician> clinicians = clinicianManager.getClinicians();

        for (Clinician clinician : clinicians) {
            staffIds.add(Integer.toString(clinician.getStaffId()));
        }

        return FXCollections.observableArrayList(staffIds);
    }
}
