package seng302.Controller;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

import seng302.HistoryItem;
import seng302.Utilities.JSONConverter;

/**
 * Controller for the history page.
 */
public class HistoryController extends SubController {

    private final DateTimeFormatter datetimeformat = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");

    @FXML
    private TableColumn<HistoryItem, String> timeCol, typeCol, commandCol;
    @FXML
    private TableView<HistoryItem> historyTable;
    @FXML
    private Pane sidebarPane;

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
        mainController.loadSidebar(sidebarPane);
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Action history");
        getSidebar();
    }
}
