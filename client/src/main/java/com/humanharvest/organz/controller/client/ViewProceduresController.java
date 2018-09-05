package com.humanharvest.organz.controller.client;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
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
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.Notifications;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.TransplantRecord;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.controller.components.DatePickerCell;
import com.humanharvest.organz.controller.components.OrganCheckComboBoxCell;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.exceptions.BadRequestException;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.views.client.ModifyProcedureObject;

/**
 * Controller for the medical history page, which shows a list of all pending and past procedures for the client.
 */
public class ViewProceduresController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(ViewProceduresController.class.getName());

    private Session session;
    private Client client;

    @FXML
    private Pane sidebarPane;
    @FXML
    private Pane newProcedurePane, procedureButtonsPane, menuBarPane;

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
    @FXML
    private Button completeTransplantButton;

    private TableView<ProcedureRecord> selectedTableView = null;

    /**
     * Gets the current session from the global state.
     */
    public ViewProceduresController() {
        session = State.getSession();
    }

    /**
     * Sets the table's sort policy - default ordering is by date DESC, but if a column is selected for sorting then
     * that takes precedence.
     *
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
     *
     * @param event The cell edit event.
     */
    private void editSummaryCell(CellEditEvent<ProcedureRecord, String> event) {
        String summary = event.getNewValue();
        if (summary == null || summary.equals("")) {
            PageNavigator.showAlert(AlertType.ERROR,
                    "Invalid summary",
                    "New procedure summary must not be blank.", mainController.getStage());
        } else {
            ModifyProcedureObject modification = new ModifyProcedureObject();
            modification.setSummary(event.getNewValue());
            System.out.println(event.getNewValue());
            ProcedureRecord record = event.getRowValue();
            sendModification(record, modification);
        }
        PageNavigator.refreshAllWindows();
    }

    /**
     * Handles the edit event when a procedure description cell is edited.
     *
     * @param event The cell edit event.
     */
    private void editDescriptionCell(CellEditEvent<ProcedureRecord, String> event) {
        ModifyProcedureObject modification = new ModifyProcedureObject();
        modification.setDescription(event.getNewValue());
        ProcedureRecord record = event.getRowValue();
        sendModification(record, modification);
        PageNavigator.refreshAllWindows();
    }

    /**
     * Handles the edit event when a procedure date cell is edited.
     *
     * @param event The cell edit event.
     */
    private void editDateCell(CellEditEvent<ProcedureRecord, LocalDate> event) {
        LocalDate newDate = event.getNewValue();
        if (newDate == null) {
            PageNavigator.showAlert(AlertType.ERROR,
                    "Invalid date",
                    "New procedure date must not be blank.", mainController.getStage());
        } else if (newDate.isBefore(client.getDateOfBirth())) {
            PageNavigator.showAlert(AlertType.ERROR,
                    "Invalid date",
                    "New procedure date must be after the client's date of birth.", mainController.getStage());
        } else {
            ModifyProcedureObject modification = new ModifyProcedureObject();
            modification.setDate(event.getNewValue());
            ProcedureRecord record = event.getRowValue();
            sendModification(record, modification);
        }
        PageNavigator.refreshAllWindows();
    }

    /**
     * Handles the edit event when an affected organs cell is edited.
     *
     * @param event The cell edit event.
     */
    private void editAffectedOrgansCell(CellEditEvent<ProcedureRecord, Set<Organ>> event) {
        ModifyProcedureObject modification = new ModifyProcedureObject();
        modification.setAffectedOrgans(event.getNewValue());
        ProcedureRecord record = event.getRowValue();
        sendModification(record, modification);
        PageNavigator.refreshAllWindows();
    }

    private void sendModification(ProcedureRecord procedureRecord, ModifyProcedureObject modification) {
        try {
            State.getClientResolver().modifyProcedureRecord(client, procedureRecord, modification);
            PageNavigator.refreshAllWindows();
        } catch (ServerRestException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            PageNavigator.showAlert(AlertType.ERROR,
                    "Server Error",
                    "An error occurred when trying to send data to the server.\nPlease try again later.",
                    mainController.getStage());
        } catch (BadRequestException e) {
            LOGGER.log(Level.INFO, "No changes were made to the procedure.", e);
        } catch (IfMatchFailedException e) {
            LOGGER.log(Level.INFO, "If-Match did not match", e);
            Notifications.create()
                    .title("Outdated Data")
                    .text("The client has been modified since you retrieved the data. If you would still like to "
                            + "apply these changes please submit again, otherwise refresh the page to update the data.")
                    .showWarning();
        }
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
                observable -> enableAppropriateButtons());
        pastProcedureView.getSelectionModel().selectedItemProperty().addListener(
                observable -> enableAppropriateButtons());
        pendingProcedureView.setOnMouseClicked(
                observable -> {
                    selectedTableView = pendingProcedureView;
                    pastProcedureView.getSelectionModel().clearSelection();
                });
        pastProcedureView.setOnMouseClicked(
                observable -> {
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
     *
     * @param mainController The MainController for the window this page is loaded on.
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);

        if (session.getLoggedInUserType() == UserType.CLIENT) {
            client = session.getLoggedInClient();
            mainController.loadSidebar(sidebarPane);
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
            deleteButton.setDisable(true);
            completeTransplantButton.setDisable(true);
        } else if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
            mainController.loadMenuBar(menuBarPane);
        }

        mainController.setTitle("Procedures: " + client.getPreferredNameFormatted());
        refresh();
        enableAppropriateButtons();
    }

    /**
     * Refreshes the past/pending procedure tables from the client's properties.
     */
    @Override
    public void refresh() {
        try {
            client.setProcedures(State.getClientResolver().getProcedureRecords(client));
        } catch (NotFoundException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            Notifications.create()
                    .title("Client not found")
                    .text("The client could not be found on the server, it may have been deleted")
                    .showWarning();
            return;
        } catch (ServerRestException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            Notifications.create()
                    .title("Server error")
                    .text("Could not apply changes on the server, please try again later")
                    .showError();
            return;
        }

        List<ProcedureRecord> allProcedures = client.getProcedures();

        SortedList<ProcedureRecord> sortedPastProcedures = new SortedList<>(FXCollections.observableArrayList(
                allProcedures.stream()
                        .filter(record -> record.getDate().isBefore(LocalDate.now()))
                        .collect(Collectors.toList())));

        SortedList<ProcedureRecord> sortedPendingProcedures = new SortedList<>(FXCollections.observableArrayList(
                allProcedures.stream()
                        .filter(record -> !record.getDate().isBefore(LocalDate.now()))
                        .collect(Collectors.toList())));

        sortedPendingProcedures.comparatorProperty().bind(pendingProcedureView.comparatorProperty());
        sortedPastProcedures.comparatorProperty().bind(pastProcedureView.comparatorProperty());

        pendingProcedureView.getItems().setAll(sortedPendingProcedures);
        pastProcedureView.getItems().setAll(sortedPastProcedures);

        pendingProcedureView.sort();
        pastProcedureView.sort();

        if (session.getLoggedInUserType() == UserType.CLIENT) {
            mainController.setTitle("View Procedures:  " + client.getPreferredNameFormatted());
        } else if (windowContext.isClinViewClientWindow()) {
            mainController.setTitle("View Procedures:  " + client.getFullName());

        }

        errorMessage.setText(null);
    }

    /**
     * Enables and disables all buttons relevant to the selected procedure record appropriately.
     */
    private void enableAppropriateButtons() {
        if (!windowContext.isClinViewClientWindow()) {
            return;
        }
        if (pastProcedureView.getSelectionModel().getSelectedItem() == null &&
                pendingProcedureView.getSelectionModel().getSelectedItem() == null) {
            deleteButton.setDisable(true);
            completeTransplantButton.setDisable(true);
        } else {
            deleteButton.setDisable(false);
            setResolveTransplantButton();
        }
    }

    /**
     * Enable or disable the resolve transplant button based on the currently selected item
     * Will only be enabled if the currently selected item is:
     * In the past
     * A TransplantRecord
     * Has not yet been completed
     */
    private void setResolveTransplantButton() {
        ProcedureRecord record = pastProcedureView.getSelectionModel().getSelectedItem();
        if (record instanceof TransplantRecord) {
            TransplantRecord tRecord = (TransplantRecord) record;
            // Disable the button if the record is completed, else enable it
            completeTransplantButton.setDisable(tRecord.isCompleted());
        } else {
            completeTransplantButton.setDisable(true);
        }
    }

    /**
     * Gets the currently selected record in the currently selected table.
     *
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
            try {
                State.getClientResolver().deleteProcedureRecord(client, record);
            } catch (ServerRestException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                PageNavigator.showAlert(AlertType.ERROR,
                        "Server Error",
                        "An error occurred when trying to send data to the server.\nPlease try again later.",
                        mainController.getStage());
                return;
            }

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
                record.addAffectedOrgan(organ);
            }

            try {
                State.getClientResolver().addProcedureRecord(client, record);

                summaryField.setText(null);
                descriptionField.setText(null);
                dateField.setValue(null);
                errorMessage.setText(null);
                PageNavigator.refreshAllWindows();

            } catch (ServerRestException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                PageNavigator.showAlert(AlertType.ERROR,
                        "Server Error",
                        "An error occurred when trying to send data to the server.\nPlease try again later.",
                        mainController.getStage());
            }
        }
    }

    @FXML
    private void completeTransplant() {
        TransplantRecord record = (TransplantRecord) pastProcedureView.getSelectionModel().getSelectedItem();
        State.getClientResolver().completeTransplantRecord(record);
        refresh();
    }
}
