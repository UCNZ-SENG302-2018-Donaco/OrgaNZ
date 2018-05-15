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

import org.apache.commons.lang3.StringUtils;

public class StaffListController extends SubController {

    @FXML
    private HBox sidebarPane;

    @FXML
    private ListView<String> staffList;

    @FXML
    private ContextMenu contextMenu;


    private String defaultAdministratorUsername = State.getAdministratorManager().getDefaultAdministrator()
            .getUsername();
    private String defaultClinicianId = Integer.toString(State.getClinicianManager().getDefaultClinician().getStaffId());


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
        staffList.setItems(getStaffIds());

        staffList.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteItem = new MenuItem();
            deleteItem.textProperty().setValue("Delete");
            deleteItem.setOnAction(event -> delete(cell.getItem()));

            contextMenu.getItems().add(deleteItem);

            cell.textProperty().bind(cell.itemProperty());

            // Listener to disable deleting of defaults
            cell.textProperty().addListener((obs, oldValue, newValue) -> {


                if (newValue == null) {
                    // IThe new value is null (the cell is now empty)
                    cell.setContextMenu(null);
                    cell.setStyle("");
                }
                else if (newValue.equals(defaultAdministratorUsername) || newValue.equals(defaultClinicianId)) {
                    // It is either the default admin username or default clinician id
                    cell.setContextMenu(null);
                    cell.setStyle("-fx-background-color: grey");
                } else {
                    // Normal cell, with a non-default staff member
                    cell.setContextMenu(contextMenu);
                    cell.setStyle("");
                }
            });
/*
            // Listener to remove context menu from empty cells
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
*/
            return cell;

        });
    }

    /**
     * Deletes the staff member (clinician or administrator) represented by "id".
     * @param id the staff member to delete
     */
    private void delete(String id) {
        ActionInvoker invoker = new ActionInvoker();
        String action_history_filename = "action_history.json";

        if (StringUtils.isNumeric(id)) {
            ClinicianManager manager = State.getClinicianManager();
            Clinician clinician = manager.getClinicianByStaffId(Integer.parseInt(id));

            Action action = new DeleteClinicianAction(clinician, manager);
            invoker.execute(action);

            HistoryItem deleteClinician = new HistoryItem("DELETE", "Clinician " + id + " deleted");
            JSONConverter.updateHistory(deleteClinician, action_history_filename);
        } else {
            AdministratorManager manager = State.getAdministratorManager();
            Administrator administrator = manager.getAdministratorByUsername(id);

            Action action = new DeleteAdministratorAction(administrator, manager);
            invoker.execute(action);

            HistoryItem deleteAdministrator = new HistoryItem("DELETE", "Administrator " + id + " deleted");
            JSONConverter.updateHistory(deleteAdministrator, action_history_filename);
        }
        refresh();
    }

    /**
     * Returns a list of all staff IDs (clinician IDs and administrator usernames).
     * @return a list of all staff IDs
     */
    private ObservableList<String> getStaffIds() {
        List<String> staffIds = new ArrayList<>();

        List<Clinician> clinicians = State.getClinicianManager().getClinicians();
        List<Administrator> administrators = State.getAdministratorManager().getAdministrators();

        for (Clinician clinician : clinicians) {
            staffIds.add(Integer.toString(clinician.getStaffId()));
        }

        for (Administrator administrator : administrators) {
            staffIds.add(administrator.getUsername());
        }

        return FXCollections.observableArrayList(staffIds);
    }

}