package seng302.Controller.Client;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import seng302.Actions.ActionInvoker;
import seng302.Actions.Client.AddProcedureRecordAction;
import seng302.Actions.Client.DeleteProcedureRecordAction;
import seng302.Actions.Client.ModifyProcedureRecordAction;
import seng302.Client;
import seng302.Controller.Components.DatePickerCell;
import seng302.Controller.Components.OrganCheckComboBoxCell;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.ProcedureRecord;
import seng302.State.Session;
import seng302.State.Session.UserType;
import seng302.State.State;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.View.PageNavigator;

import org.controlsfx.control.CheckComboBox;

/**
 * Controller for the medical history page, which shows a list of all pending and past procedures for the client.
 */
public class ViewProceduresController extends SubController {

    private Session session;
    private ActionInvoker invoker;
    private Client client;

    @FXML
    private Pane sidebarPane;
    @FXML
    private Pane newProcedurePane, procedureButtonsPane;

    @FXML
    private TextField summaryField;
    @FXML
    private DatePicker dateField;
    @FXML
    private CheckComboBox<Organ> affectedOrgansField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private Text errorMessage;

    @FXML
    private TableView<ProcedureRecord> pastProcedureView, pendingProcedureView;
    @FXML
    private TableColumn<ProcedureRecord, String> summaryPastCol, summaryPendCol, descriptionPastCol, descriptionPendCol;
    @FXML
    private TableColumn<ProcedureRecord, LocalDate> datePastCol, datePendCol;
    @FXML
    private TableColumn<ProcedureRecord, Set<Organ>> affectedPastCol, affectedPendCol;
    @FXML
    private Button deleteButton;

    private TableView<ProcedureRecord> selectedTableView = null;

    /**
     * Sets the table's sort policy - default ordering is by date DESC, but if a column is selected for sorting then
     * that takes precedence.
     * @param table The table to set the sort policy for.
     * @return True.
     */
    private static Boolean getTableSortPolicy(TableView<ProcedureRecord> table) {
        Comparator<ProcedureRecord> comparator = (r1, r2) -> {
            Comparator<ProcedureRecord> tableComparator = table.getComparator();
            if (tableComparator != null) {
                return table.getComparator().compare(r1, r2);
            } else if (r1.getDate().isAfter(r2.getDate())) {
                return -1;
            } else {
                return 1;
            }
        };
        FXCollections.sort(table.getItems(), comparator);
        return true;
    }

    /**
     * Handles the edit event when a procedure summary cell is edited.
     * @param event The cell edit event.
     */
    private void editSummaryCell(CellEditEvent<ProcedureRecord,String> event) {
        ModifyProcedureRecordAction action = new ModifyProcedureRecordAction(event.getRowValue());
        action.changeSummary(event.getNewValue());

        try {
            invoker.execute(action);
            PageNavigator.refreshAllWindows();
        } catch (IllegalStateException ignored) {
        }
    }

    /**
     * Handles the edit event when a procedure description cell is edited.
     * @param event The cell edit event.
     */
    private void editDescriptionCell(CellEditEvent<ProcedureRecord,String> event) {
        ModifyProcedureRecordAction action = new ModifyProcedureRecordAction(event.getRowValue());
        action.changeDescription(event.getNewValue());

        try {
            invoker.execute(action);
            PageNavigator.refreshAllWindows();
        } catch (IllegalStateException ignored) {
        }
    }

    /**
     * Handles the edit event when a procedure date cell is edited.
     * @param event The cell edit event.
     */
    private void editDateCell(CellEditEvent<ProcedureRecord, LocalDate> event) {
        ModifyProcedureRecordAction action = new ModifyProcedureRecordAction(event.getRowValue());
        action.changeDate(event.getNewValue());

        try {
            invoker.execute(action);
            PageNavigator.refreshAllWindows();
        } catch (IllegalStateException ignored) {
        }
    }

    /**
     * Handles the edit event when an affected organs cell is edited.
     * @param event The cell edit event.
     */
    private void editAffectedOrgansCell(CellEditEvent<ProcedureRecord, Set<Organ>> event) {
        ModifyProcedureRecordAction action = new ModifyProcedureRecordAction(event.getRowValue());
        action.changeAffectedOrgans(event.getNewValue());

        try {
            invoker.execute(action);
            PageNavigator.refreshAllWindows();
        } catch (IllegalStateException ignored) {
        }
    }

    /**
     * Gets the current session and action invoker from the global state.
     */
    public ViewProceduresController() {
        session = State.getSession();
        invoker = State.getInvoker();
    }

    /**
     * Initializes the page, setting cell value/represntation factories for all the columns, setting up selection
     * listeners, setting the sort policy for each table and setting the initial value for the date diagnosed picker.
     */
    @FXML
    public void initialize() {
        // Setup the value factories (these get the actual model values underlying the cells)
        summaryPastCol.setCellValueFactory(new PropertyValueFactory<>("summary"));
        summaryPendCol.setCellValueFactory(new PropertyValueFactory<>("summary"));
        datePastCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        datePendCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        affectedPastCol.setCellValueFactory(new PropertyValueFactory<>("affectedOrgans"));
        affectedPendCol.setCellValueFactory(new PropertyValueFactory<>("affectedOrgans"));
        descriptionPastCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionPendCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Setup the cell factories (these generate the editable cells)
        summaryPastCol.setCellFactory(TextFieldTableCell.forTableColumn());
        summaryPendCol.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionPastCol.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionPendCol.setCellFactory(TextFieldTableCell.forTableColumn());
        datePastCol.setCellFactory(DatePickerCell::new);
        datePendCol.setCellFactory(DatePickerCell::new);
        affectedPastCol.setCellFactory(OrganCheckComboBoxCell::new);
        affectedPendCol.setCellFactory(OrganCheckComboBoxCell::new);

        // Setup the edit commit handlers (these deal with making the changes using Actions)
        summaryPastCol.setOnEditCommit(this::editSummaryCell);
        summaryPendCol.setOnEditCommit(this::editSummaryCell);
        descriptionPastCol.setOnEditCommit(this::editDescriptionCell);
        descriptionPendCol.setOnEditCommit(this::editDescriptionCell);
        datePendCol.setOnEditCommit(this::editDateCell);
        datePastCol.setOnEditCommit(this::editDateCell);
        affectedPendCol.setOnEditCommit(this::editAffectedOrgansCell);
        affectedPastCol.setOnEditCommit(this::editAffectedOrgansCell);

        // Add listeners to clear the other table when anything is selected in each table (and enable/disable buttons).
        pendingProcedureView.getSelectionModel().selectedItemProperty().addListener(
                (observable) -> enableAppropriateButtons());
        pastProcedureView.getSelectionModel().selectedItemProperty().addListener(
                (observable) -> enableAppropriateButtons());
        pendingProcedureView.setOnMouseClicked(
                (observable) -> {
                    selectedTableView = pendingProcedureView;
                    pastProcedureView.getSelectionModel().clearSelection();
                });
        pastProcedureView.setOnMouseClicked(
                (observable) -> {
                    selectedTableView = pastProcedureView;
                    pendingProcedureView.getSelectionModel().clearSelection();
                });

        // Set the sort policies for both tables
        pendingProcedureView.setSortPolicy(ViewProceduresController::getTableSortPolicy);
        pastProcedureView.setSortPolicy(ViewProceduresController::getTableSortPolicy);

        // Setup the "new procedure" affected organs input with all organ values
        affectedOrgansField.getItems().setAll(Organ.values());
    }

    /**
     * Sets up the page using the MainController given.
     * - Loads the sidebar.
     * - Checks if the session login type is a client or a clinician, and sets the viewed client appropriately.
     * - Checks if the logged in user is a client, and if so, makes the page non-editable.
     * - Refreshes the procedure tables to set initial state based on the viewed client.
     * @param mainController The MainController for the window this page is loaded on.
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.loadSidebar(sidebarPane);

        if (session.getLoggedInUserType() == UserType.CLIENT) {
            client = session.getLoggedInClient();

            newProcedurePane.setVisible(false);
            newProcedurePane.setManaged(false);
            procedureButtonsPane.setVisible(false);
            procedureButtonsPane.setManaged(false);
            pendingProcedureView.setEditable(false);
            pastProcedureView.setEditable(false);
            datePendCol.setEditable(false);
            datePastCol.setEditable(false);
            affectedPendCol.setEditable(false);
            affectedPastCol.setEditable(false);
        } else if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
        }

        mainController.setTitle("View procedures: " + client.getFullName());
        refresh();
        enableAppropriateButtons();
    }

    /**
     * Refreshes the past/pending procedure tables from the client's properties.
     */
    @Override
    public void refresh() {
        SortedList<ProcedureRecord> sortedPendingProcedures = new SortedList<>(FXCollections.observableArrayList(
                client.getPendingProcedures()));
        SortedList<ProcedureRecord> sortedPastProcedures = new SortedList<>(FXCollections.observableArrayList(
                client.getPastProcedures()));

        sortedPendingProcedures.comparatorProperty().bind(pendingProcedureView.comparatorProperty());
        sortedPastProcedures.comparatorProperty().bind(pastProcedureView.comparatorProperty());

        pendingProcedureView.getItems().setAll(sortedPendingProcedures);
        pastProcedureView.getItems().setAll(sortedPastProcedures);

        pendingProcedureView.sort();
        pastProcedureView.sort();

        errorMessage.setText(null);
    }

    /**
     * Enables and disables all buttons relevant to the selected procedure record appropriately.
     */
    private void enableAppropriateButtons() {
        if (windowContext.isClinViewClientWindow()) {
            if (pastProcedureView.getSelectionModel().getSelectedItem() == null &&
                    pendingProcedureView.getSelectionModel().getSelectedItem() == null) {
                deleteButton.setDisable(true);
            } else {
                deleteButton.setDisable(false);
            }
        }
    }

    /**
     * Gets the currently selected record in the currently selected table.
     * @return The selected procedure record.
     */
    private ProcedureRecord getSelectedRecord() {
        if (selectedTableView != null) {
            return selectedTableView.getSelectionModel().getSelectedItem();
        } else {
            return null;
        }
    }

    /**
     * Deletes the currently selected procedure record.
     */
    @FXML
    private void deleteProcedure() {
        ProcedureRecord record = getSelectedRecord();
        if (record != null) {
            DeleteProcedureRecordAction action = new DeleteProcedureRecordAction(client, record);

            invoker.execute(action);
            PageNavigator.refreshAllWindows();
        }
    }

    /**
     * Adds a new procedure record based on the information in the add new procedure record inputs.
     */
    @FXML
    private void addProcedure() {
        String summary = summaryField.getText();
        LocalDate date = dateField.getValue();

        if (summary == null || summary.equals("")) {
            errorMessage.setText("Procedure summary must not be blank.");
        } else if (date == null || date.isBefore(client.getDateOfBirth())) {
            errorMessage.setText("Procedure date cannot be before client was born.");
        } else {
            ProcedureRecord record = new ProcedureRecord(summary, descriptionField.getText(), date);
            for (Organ organ : affectedOrgansField.getCheckModel().getCheckedItems()) {
                record.getAffectedOrgans().add(organ);
            }
            AddProcedureRecordAction action = new AddProcedureRecordAction(client, record);
            invoker.execute(action);

            summaryField.setText(null);
            descriptionField.setText(null);
            dateField.setValue(null);
            errorMessage.setText(null);

            PageNavigator.refreshAllWindows();
        }
    }
}
