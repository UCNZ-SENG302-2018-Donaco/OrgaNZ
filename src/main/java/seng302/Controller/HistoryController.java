package seng302.Controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import seng302.HistoryItem;
import seng302.Utilities.JSONConverter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoryController {
    private final DateTimeFormatter datetimeformat = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");

    @FXML
    private TableColumn<HistoryItem, String> timeCol, typeCol, commandCol;
    @FXML
    private TableView<HistoryItem> historyTable;
    @FXML
    private Pane sidebarPane;

    @FXML
    private void initialize() {
        SidebarController.loadSidebar(sidebarPane);

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
}
