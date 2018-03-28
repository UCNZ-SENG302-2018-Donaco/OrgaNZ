package seng302.Controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import seng302.HistoryItem;
import seng302.Session;
import seng302.State;
import seng302.Utilities.JSONConverter;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for the history page.
 */
public class HistoryController extends SubController {
    private final DateTimeFormatter datetimeformat = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");
    private Session session;

    @FXML
    private TableColumn<HistoryItem, String> timeCol, typeCol, commandCol;
    @FXML
    private TableView<HistoryItem> historyTable;
    @FXML
    private Pane sidebarPane;

    public HistoryController() {
        session = State.getSession();
    }

    /**
     * Initializes the UI for this page.
     * - Loads the sidebar.
     * - Sets up cell factories to generate the values for the history table.
     * - Loads history data from file and populates the table with it.
     */
    @FXML
    private void initialize() {

        timeCol.setCellValueFactory(
                data -> new ReadOnlyStringWrapper(
                        data.getValue().getTimestamp().format(datetimeformat))
        );
        typeCol.setCellValueFactory(
                data -> new ReadOnlyStringWrapper(data.getValue().getType())
        );
        commandCol.setCellValueFactory(
                data -> new ReadOnlyStringWrapper(data.getValue().getDetails())
        );

        try {
            List<HistoryItem> history = JSONConverter.loadJSONtoHistory(new File("action_history.json"));
            historyTable.setItems(FXCollections.observableArrayList(history));
        } catch (IOException exc) {
            System.out.println(exc.getMessage());
        }

    }

    private void getSidebar() {
        if (session.getLoggedInUserType() == Session.UserType.DONOR || windowContext.isClinViewDonorWindow()) {
            mainController.loadDonorSidebar(sidebarPane);
        } else if (session.getLoggedInUserType() == Session.UserType.CLINICIAN) {
            mainController.loadClinicianSidebar(sidebarPane);
        }
    }

    @Override
    public void setMainController(MainController mainController) {
        super.setMainController(mainController);
        getSidebar();
    }
}
