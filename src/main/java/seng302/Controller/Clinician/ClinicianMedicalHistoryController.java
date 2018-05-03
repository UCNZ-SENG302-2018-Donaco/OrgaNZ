package seng302.Controller.Clinician;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import seng302.Actions.ActionInvoker;
import seng302.Actions.Client.AddIllnessRecordAction;
import seng302.Actions.Client.DeleteIllnessRecordAction;
import seng302.Actions.Client.ModifyIllnessRecordAction;
import seng302.Client;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.IllnessRecord;
import seng302.State.Session;
import seng302.State.Session.UserType;
import seng302.State.State;
import seng302.Utilities.View.PageNavigator;

/**
 * Controller for the medical history page, which shows a list of all current and past illnesses for the client.
 */
public class ClinicianMedicalHistoryController extends SubController {

    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("d MMM yyyy");

    private Session session;
    private ActionInvoker invoker;
    private Client client;

    @FXML
    private Pane sidebarPane;
    @FXML
    private HBox newIllnessPane, illnessButtonsPane;

    @FXML
    private TextField illnessNameField;
    @FXML
    private DatePicker dateDiagnosedPicker;
    @FXML
    private CheckBox chronicBox;
    @FXML
    private Text errorMessage;

    @FXML
    private TableView<IllnessRecord> pastIllnessView, currentIllnessView;
    @FXML
    private TableColumn<IllnessRecord, String> illnessPastCol, illnessCurrCol;
    @FXML
    private TableColumn<IllnessRecord, LocalDate> diagnosisDatePastCol, diagnosisDateCurrCol, curedDatePastCol;
    @FXML
    private TableColumn<IllnessRecord, Boolean> chronicCurrCol;
    @FXML
    private Button moveToHistoryButton, moveToCurrentButton, deleteButton, toggleChronicButton;

    private TableView<IllnessRecord> selectedTableView = null;

    /**
     * Formats a table cell that holds a {@link LocalDate} value to display that value in the date time format.
     * @return The cell with the date time formatter set.
     */
    private static TableCell<IllnessRecord, LocalDate> formatDateTimeCell() {
        return new TableCell<IllnessRecord, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.format(dateTimeFormat));
                }
            }
        };
    }

    /**
     * Formats a table cell that holds a {@link Boolean} to display "CHRONIC" in red text if the value is true, or
     * nothing otherwise.
     * @return The cell with the chronic formatter set.
     */
    private static TableCell<IllnessRecord, Boolean> formatChronicCell() {
        return new TableCell<IllnessRecord, Boolean>() {
            @Override
            protected void updateItem(Boolean isChronic, boolean empty) {
                super.updateItem(isChronic, empty);
                if (isChronic == null || empty) {
                    setText(null);
                    setStyle(null);
                } else if (isChronic) {
                    setText("CHRONIC");
                    setStyle("-fx-text-fill: red;");
                } else {
                    setText(null);
                    setStyle(null);
                }
            }
        };
    }

    /**
     * Creates a sort policy where records for chronic illnesses are always sorted first, then sorts by the table's
     * current comparator. If no table comparator is active, then the default sorting is by diagnosis date descending.
     * @param table The tableview to get the current comparator from.
     * @return The sort policy.
     */
    private static Boolean getChronicFirstSortPolicy(TableView<IllnessRecord> table) {
        Comparator<IllnessRecord> comparator = (r1, r2) -> {
            if (r1.isChronic() == r2.isChronic()) {
                Comparator<IllnessRecord> tableComparator = table.getComparator();
                if (tableComparator != null) {
                    return table.getComparator().compare(r1, r2);
                } else {
                    return -r1.getDiagnosisDate().compareTo(r2.getDiagnosisDate());  // negative because sorting DESC
                }
            } else if (r1.isChronic()) {
                return -1;
            } else {
                return 1;
            }
        };
        FXCollections.sort(table.getItems(), comparator);
        return true;
    }

    /**
     * Gets the current session and action invoker from the global state.
     */
    public ClinicianMedicalHistoryController() {
        session = State.getSession();
        invoker = State.getInvoker();
    }

    /**
     * Initializes the page, setting cell value/represntation factories for all the columns, setting up selection
     * listeners, setting the sort policy for each table and setting the initial value for the date diagnosed picker.
     */
    @FXML
    public void initialize() {
        illnessCurrCol.setCellValueFactory(new PropertyValueFactory<>("illnessName"));
        diagnosisDateCurrCol.setCellValueFactory(new PropertyValueFactory<>("diagnosisDate"));
        chronicCurrCol.setCellValueFactory(new PropertyValueFactory<>("chronic"));

        illnessPastCol.setCellValueFactory(new PropertyValueFactory<>("illnessName"));
        diagnosisDatePastCol.setCellValueFactory(new PropertyValueFactory<>("diagnosisDate"));
        curedDatePastCol.setCellValueFactory(new PropertyValueFactory<>("curedDate"));

        // Format all the datetime cells
        diagnosisDateCurrCol.setCellFactory(cell -> formatDateTimeCell());
        diagnosisDatePastCol.setCellFactory(cell -> formatDateTimeCell());
        curedDatePastCol.setCellFactory(cell -> formatDateTimeCell());

        // Format chronic cells
        chronicCurrCol.setCellFactory(cell -> formatChronicCell());

        // Set listeners so that the other table's selection is cleared when an item in each table is selected.
        pastIllnessView.getSelectionModel().selectedItemProperty().addListener(
                (observable) -> {
                    selectedTableView = pastIllnessView;
                    currentIllnessView.getSelectionModel().clearSelection();
                    enableAppropriateButtons();
                });

        currentIllnessView.getSelectionModel().selectedItemProperty().addListener(
                (observable) -> {
                    selectedTableView = currentIllnessView;
                    pastIllnessView.getSelectionModel().clearSelection();
                    enableAppropriateButtons();
                });

        currentIllnessView.setSortPolicy(ClinicianMedicalHistoryController::getChronicFirstSortPolicy);
        pastIllnessView.setSortPolicy(ClinicianMedicalHistoryController::getChronicFirstSortPolicy);

        dateDiagnosedPicker.setValue(LocalDate.now());
    }

    /**
     * Sets up the page using the MainController given.
     * - Loads the sidebar.
     * - Checks if the session login type is a client or a clinician, and sets the viewed client appropriately.
     * - Checks if the logged in user is a client, and if so, makes the page non-editable.
     * - Refreshes the illness tables to set initial state based on the viewed client.
     * @param mainController The MainController for the window this page is loaded on.
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.loadSidebar(sidebarPane);

        if (session.getLoggedInUserType() == UserType.CLIENT) {
            client = session.getLoggedInClient();

            newIllnessPane.setVisible(false);
            newIllnessPane.setManaged(false);
            illnessButtonsPane.setVisible(false);
            illnessButtonsPane.setManaged(false);
        } else if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
        }

        refresh();
        enableAppropriateButtons();
    }

    /**
     * Refreshes the past/current illness tables from the client's properties.
     */
    @Override
    public void refresh() {
        SortedList<IllnessRecord> sortedCurrentIllnesses = new SortedList<>(FXCollections.observableArrayList(
                client.getCurrentIllnesses()));
        SortedList<IllnessRecord> sortedPastIllnesses = new SortedList<>(FXCollections.observableArrayList(
                client.getPastIllnesses()));

        sortedCurrentIllnesses.comparatorProperty().bind(currentIllnessView.comparatorProperty());
        sortedPastIllnesses.comparatorProperty().bind(pastIllnessView.comparatorProperty());

        currentIllnessView.getItems().setAll(sortedCurrentIllnesses);
        pastIllnessView.getItems().setAll(sortedPastIllnesses);

        currentIllnessView.sort();
        pastIllnessView.sort();

        errorMessage.setText(null);
    }

    private void enableAppropriateButtons() {
        if (windowContext.isClinViewClientWindow()) {
            if (getSelectedRecord() == null) {
                moveToCurrentButton.setDisable(true);
                moveToHistoryButton.setDisable(true);
                toggleChronicButton.setDisable(true);
                deleteButton.setDisable(true);
            } else if (selectedTableView == currentIllnessView) {
                moveToCurrentButton.setDisable(true);
                moveToHistoryButton.setDisable(false);
                toggleChronicButton.setDisable(false);
                deleteButton.setDisable(false);
            } else if (selectedTableView == pastIllnessView) {
                moveToCurrentButton.setDisable(false);
                moveToHistoryButton.setDisable(true);
                toggleChronicButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        }
    }

    /**
     * Gets the currently selected record in the currently selected table.
     * @return The selected illness record.
     */
    private IllnessRecord getSelectedRecord() {
        if (selectedTableView != null) {
            return selectedTableView.getSelectionModel().getSelectedItem();
        } else {
            return null;
        }
    }

    /**
     * Moves the currently selected illness record to history, setting its cured date to the current date.
     */
    @FXML
    private void moveIllnessToHistory() {
        IllnessRecord record = getSelectedRecord();
        if (record != null) {
            if (record.isChronic()) {
                PageNavigator.showAlert(AlertType.ERROR,
                        "Can't move a chronic illness to past illnesses.",
                        "An illness can't be cured if it is chronic.");
            } else {
                ModifyIllnessRecordAction action = new ModifyIllnessRecordAction(record);
                action.changeCuredDate(LocalDate.now());

                invoker.execute(action);
                PageNavigator.refreshAllWindows();
            }
        }
    }

    /**
     * Moves the currently selected illness record to history, setting its cured date to null.
     */
    @FXML
    private void moveIllnessToCurrent() {
        IllnessRecord record = getSelectedRecord();
        if (record != null) {
            ModifyIllnessRecordAction action = new ModifyIllnessRecordAction(record);
            action.changeCuredDate(null);
            invoker.execute(action);
            PageNavigator.refreshAllWindows();
        }
    }

    /**
     * Deletes the currently selected illness record.
     */
    @FXML
    private void deleteIllness() {
        IllnessRecord record = getSelectedRecord();
        if (record != null) {
            DeleteIllnessRecordAction action = new DeleteIllnessRecordAction(client, record);

            invoker.execute(action);
            PageNavigator.refreshAllWindows();
        }
    }

    /**
     * Toggles the currently selected illness record's chronic property. If the illness is chronic, it will be set to
     * non-chronic. If the illness is not chronic, it will be set as chronic AND if it was a "cured" record, then its
     * cured date will be set to null (to show that it wasn't cured after all).
     */
    @FXML
    private void toggleChronic() {
        IllnessRecord record = getSelectedRecord();
        if (record != null) {
            ModifyIllnessRecordAction action = new ModifyIllnessRecordAction(record);
            if (record.isChronic()) {
                action.changeChronicStatus(false);
            } else {
                if (record.getCuredDate() != null) {
                    action.changeCuredDate(null);
                }
                action.changeChronicStatus(true);
            }
            invoker.execute(action);
            PageNavigator.refreshAllWindows();
        }
    }

    /**
     * Adds a new illness record based on the information in the add new illness record inputs.
     */
    @FXML
    private void addIllness() {
        String illnessName = illnessNameField.getText();
        LocalDate dateDiagnosed = dateDiagnosedPicker.getValue();
        boolean isChronic = chronicBox.isSelected();

        boolean beforeBirth = dateDiagnosed.isBefore(client.getDateOfBirth());
        boolean inFuture = dateDiagnosed.isAfter(LocalDate.now().plus(1, ChronoUnit.DAYS));

        if (illnessName == null || illnessName.equals("")) {
            errorMessage.setText("Illness name must not be blank.");
        } else if (beforeBirth) {
            errorMessage.setText("Diagnosis date cannot be before person is born.");
        } else if (inFuture) {
            errorMessage.setText("Diagnosis date cannot be in the future.");
        } else {
            IllnessRecord record = new IllnessRecord(illnessName, dateDiagnosed, null, isChronic);
            AddIllnessRecordAction action = new AddIllnessRecordAction(client, record);
            invoker.execute(action);

            illnessNameField.setText(null);
            errorMessage.setText(null);
            dateDiagnosedPicker.setValue(LocalDate.now());
            chronicBox.setSelected(false);
            PageNavigator.refreshAllWindows();
        }
    }
}
