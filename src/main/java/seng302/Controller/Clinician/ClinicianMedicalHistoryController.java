package seng302.Controller.Clinician;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
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
import seng302.Actions.Client.AddIllnessRecord;
import seng302.Actions.Client.DeleteIllnessRecord;
import seng302.Actions.Client.ModifyIllnessRecordAction;
import seng302.Client;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.IllnessRecord;
import seng302.State.Session;
import seng302.State.Session.UserType;
import seng302.State.State;
import seng302.Utilities.View.PageNavigator;

public class ClinicianMedicalHistoryController extends SubController {

    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("d MMM yyyy");

    private Session session;
    private ActionInvoker invoker;
    private Client client;

    @FXML
    private Pane sidebarPane;
    @FXML
    private HBox newIllnessPane;

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

    private static Boolean getChronicFirstSortPolicy(TableView<IllnessRecord> table) {
        Comparator<IllnessRecord> comparator = (r1, r2) -> {
            if (r1.isChronic() == r2.isChronic()) {
                Comparator<IllnessRecord> tableComparator = table.getComparator();
                if (tableComparator != null) {
                    return table.getComparator().compare(r1, r2);
                } else {
                    return 0;
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

    public ClinicianMedicalHistoryController() {
        session = State.getSession();
        invoker = State.getInvoker();
    }

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

        pastIllnessView.getSelectionModel().selectedItemProperty().addListener(
                (observable) -> {
                    selectedTableView = pastIllnessView;
                    currentIllnessView.getSelectionModel().clearSelection();
                });

        currentIllnessView.getSelectionModel().selectedItemProperty().addListener(
                (observable) -> {
                    selectedTableView = currentIllnessView;
                    pastIllnessView.getSelectionModel().clearSelection();
                });

        currentIllnessView.setSortPolicy(ClinicianMedicalHistoryController::getChronicFirstSortPolicy);

        dateDiagnosedPicker.setValue(LocalDate.now());
    }

    /**
     * Sets up the page using the MainController given.
     * - Loads the sidebar.
     * - Checks if the session login type is a client or a clinician, and sets the viewed client appropriately.
     * - Refreshes the medication list views to set initial state based on the viewed client.
     * - Checks if the logged in user is a client, and if so, makes the page non-editable.
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
            moveToHistoryButton.setDisable(true);
            moveToCurrentButton.setDisable(true);
            deleteButton.setDisable(true);
            toggleChronicButton.setDisable(true);
        } else if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
        }

        refresh();
    }

    /**
     * Refreshes the past/current illness list views from the client's properties.
     */
    @Override
    public void refresh() {
        SortedList<IllnessRecord> sortedCurrentIllnesses = new SortedList<>(FXCollections.observableArrayList(
                client.getCurrentIllnesses()));
        SortedList<IllnessRecord> sortedPastIllnesses = new SortedList<>(FXCollections.observableArrayList(
                client.getPastIllnesses()));

        sortedCurrentIllnesses.comparatorProperty().bind(currentIllnessView.comparatorProperty());
        sortedPastIllnesses.comparatorProperty().bind(pastIllnessView.comparatorProperty());

        currentIllnessView.getItems().clear();
        pastIllnessView.getItems().clear();

        for (IllnessRecord record : sortedCurrentIllnesses) {
            if (record.isChronic()) {
                currentIllnessView.getItems().add(record);
            }
        }
        for (IllnessRecord record : sortedCurrentIllnesses) {
            if (!record.isChronic()) {
                currentIllnessView.getItems().add(record);
            }
        }
        for (IllnessRecord record : sortedPastIllnesses) {
            if (record.isChronic()) {
                pastIllnessView.getItems().add(record);
            }
        }
        for (IllnessRecord record : sortedPastIllnesses) {
            if (!record.isChronic()) {
                pastIllnessView.getItems().add(record);
            }
        }

        errorMessage.setText(null);
    }

    private IllnessRecord getSelectedRecord() {
        if (selectedTableView != null) {
            return selectedTableView.getSelectionModel().getSelectedItem();
        } else {
            return null;
        }
    }

    /**
     * Moves Selected Illness to past Illnesses List Provided it is not chronic.
     */
    @FXML
    private void moveIllnessToHistory() {
        IllnessRecord record = getSelectedRecord();
        if (record != null) {
            if (record.isChronic()) {
                errorMessage.setText("Can't move chronic illness to Past Illnesses.");
            } else {
                ModifyIllnessRecordAction action = new ModifyIllnessRecordAction(record);
                action.changeCuredDate(LocalDate.now());

                invoker.execute(action);
                PageNavigator.refreshAllWindows();
            }
        }
    }

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

    @FXML
    private void deleteIllness() {
        IllnessRecord record = getSelectedRecord();
        if (record != null) {
            DeleteIllnessRecord action = new DeleteIllnessRecord(client, record);

            invoker.execute(action);
            PageNavigator.refreshAllWindows();
        }
    }

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
            AddIllnessRecord action = new AddIllnessRecord(client, record);
            invoker.execute(action);

            illnessNameField.setText(null);
            errorMessage.setText(null);
            dateDiagnosedPicker.setValue(LocalDate.now());
            PageNavigator.refreshAllWindows();
        }
    }
}
