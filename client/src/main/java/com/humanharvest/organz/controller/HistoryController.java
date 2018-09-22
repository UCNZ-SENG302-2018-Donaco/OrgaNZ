package com.humanharvest.organz.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;

/**
 * Controller for the history page.
 */
public class HistoryController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(HistoryController.class.getName());
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");

    private Session session;

    @FXML
    private TableColumn<HistoryItem, LocalDateTime> timestampCol;
    @FXML
    private TableColumn<HistoryItem, String> typeCol, detailsCol;
    @FXML
    private TableView<HistoryItem> historyTable;
    @FXML
    private Pane sidebarPane, menuBarPane;

    public HistoryController() {
        this.session = State.getSession();
    }

    /**
     * Formats a table cell that holds a {@link LocalDateTime} value to display that value in the date time format.
     *
     * @return The cell with the date time formatter set.
     */
    private static TableCell<HistoryItem, LocalDateTime> formatDateTimeCell() {
        return new TableCell<HistoryItem, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(dateTimeFormat));
                }
            }
        };
    }

    /**
     * Initializes the UI for this page.
     * - Loads the sidebar.
     * - Sets up cell factories to generate the values for the history table.
     * - Loads history data from file and populates the table with it.
     */
    @FXML
    private void initialize() {
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        timestampCol.setCellFactory(cell -> formatDateTimeCell());
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        detailsCol.setCellValueFactory(new PropertyValueFactory<>("details"));
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);

        if (session.getLoggedInUserType() == UserType.CLIENT || windowContext.isClinViewClientWindow()) {
            mainController.setTitle("Client History");
            mainController.loadSidebar(sidebarPane);
        } else {
            mainController.setTitle("System History");
            mainController.loadTouchActionsBar(menuBarPane);
        }

        refresh();
    }

    @Override
    public void refresh() {
        List<HistoryItem> historyItems;
        if (session.getLoggedInUserType() == UserType.CLIENT) {
            historyItems = State.getClientResolver().getHistory(session.getLoggedInClient());
        } else if (windowContext.isClinViewClientWindow()) {
            historyItems = State.getClientResolver().getHistory(windowContext.getViewClient());
        } else if (session.getLoggedInUserType() == UserType.CLINICIAN) {
            historyItems = State.getClinicianResolver().getHistory(session.getLoggedInClinician());
        } else {

            historyItems = State.getAdministratorResolver().getHistory();
        }

        historyTable.setItems(FXCollections.observableArrayList(historyItems));

        FXCollections.sort(historyTable.getItems(), (h1, h2) -> h2.getTimestamp().compareTo(h1.getTimestamp()));
    }
}
