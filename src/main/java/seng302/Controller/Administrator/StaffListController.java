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

import seng302.Administrator;
import seng302.Clinician;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.State.State;

import org.apache.commons.lang3.StringUtils;

public class StaffListController extends SubController {

    @FXML
    private HBox sidebarPane;

    @FXML
    private ListView<String> staffList;

    @FXML
    private ContextMenu contextMenu;


    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Staff list");
        mainController.loadSidebar(sidebarPane);
    }


    @FXML
    private void initialize() {
        List<String> staffIds = getStaffIds();
        ObservableList<String> observableStaffIds = FXCollections.observableArrayList(staffIds);

        staffList.setItems(observableStaffIds);

        staffList.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteItem = new MenuItem();
            deleteItem.textProperty().setValue("Delete");
            deleteItem.setOnAction(event -> delete(cell.getItem()));

            contextMenu.getItems().add(deleteItem);

            cell.textProperty().bind(cell.itemProperty());

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });

            return cell;

        });
    }

    /**
     * Deletes the staff member (clinician or administrator) represented by "id".
     * @param id the staff member to delete
     */
    private void delete(String id) {
        if (StringUtils.isNumeric(id)) {
            System.out.println("Deleting clinician: " + id);
        } else {
            System.out.println("Deleting admin: " + id);
        }
    }

    /**
     * Returns a list of all staff IDs (clinician IDs and administrator usernames).
     * @return a list of all staff IDs
     */
    private List<String> getStaffIds() {
        List<String> staffIds = new ArrayList<>();

        List<Clinician> clinicians = State.getClinicianManager().getClinicians();
        List<Administrator> administrators = State.getAdministratorManager().getAdministrators();

        for (Clinician clinician : clinicians) {
            staffIds.add(Integer.toString(clinician.getStaffId()));
        }

        for (Administrator administrator : administrators) {
            staffIds.add(administrator.getUsername());
        }

        return staffIds;
    }

}