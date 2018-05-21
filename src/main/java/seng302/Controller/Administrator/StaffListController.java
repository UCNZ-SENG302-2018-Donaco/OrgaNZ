package seng302.Controller.Administrator;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;

import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.Administrator.DeleteAdministratorAction;
import seng302.Actions.Clinician.DeleteClinicianAction;
import seng302.Administrator;
import seng302.Clinician;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.HistoryItem;
import seng302.State.AdministratorManager;
import seng302.State.ClinicianManager;
import seng302.State.State;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.View.PageNavigator;

public class StaffListController extends SubController {

    private ClinicianManager clinicianManager;
    private AdministratorManager adminManager;
    private ActionInvoker invoker;

    @FXML
    private HBox sidebarPane;

    @FXML
    private ListView<String> staffList;

    private final String defaultAdminUsername;
    private final String defaultClinicianId;

    public StaffListController() {
        this.adminManager = State.getAdministratorManager();
        this.clinicianManager = State.getClinicianManager();
        this.invoker = State.getInvoker();

        this.defaultAdminUsername = adminManager.getDefaultAdministrator().getUsername();
        this.defaultClinicianId = Integer.toString(clinicianManager.getDefaultClinician().getStaffId());
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Staff list");
        mainController.loadSidebar(sidebarPane);
    }

    @Override
    public void refresh() {
        super.refresh();
        staffList.setItems(getStaffIds());
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
        String action_history_filename = "action_history.json";

        if (id.matches("[0-9]+")) {
            Clinician clinician = clinicianManager.getClinicianByStaffId(Integer.parseInt(id));

            Action action = new DeleteClinicianAction(clinician, clinicianManager);
            invoker.execute(action);

            HistoryItem deleteClinician = new HistoryItem("DELETE", "Clinician " + id + " deleted");
            JSONConverter.updateHistory(deleteClinician, action_history_filename);
        } else {
            Administrator administrator = adminManager.getAdministratorByUsername(id);

            Action action = new DeleteAdministratorAction(administrator, adminManager);
            invoker.execute(action);

            HistoryItem deleteAdministrator = new HistoryItem("DELETE", "Administrator " + id + " deleted");
            JSONConverter.updateHistory(deleteAdministrator, action_history_filename);
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
        List<Administrator> administrators = adminManager.getAdministrators();

        for (Clinician clinician : clinicians) {
            staffIds.add(Integer.toString(clinician.getStaffId()));
        }

        for (Administrator administrator : administrators) {
            staffIds.add(administrator.getUsername());
        }

        return FXCollections.observableArrayList(staffIds);
    }
}
