package com.humanharvest.organz.controller;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.utilities.JSONConverter;

/**
 * Controller for the history page.
 */
public class HistoryController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(HistoryController.class.getName());

    private final DateTimeFormatter datetimeformat = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");

    @FXML
    private TableColumn<HistoryItem, String> timeCol, typeCol, commandCol;
    @FXML
    private TableView<HistoryItem> historyTable;
    @FXML
    private Pane menuBarPane;

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
            LOGGER.severe("IO Exception while loading history table");
            LOGGER.severe(exc.getMessage());
        }

    }


    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Action history");
        mainController.loadMenuBar(menuBarPane);

    }
}