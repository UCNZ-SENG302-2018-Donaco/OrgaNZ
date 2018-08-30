package com.humanharvest.organz.controller.client;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Objects;
import java.util.logging.Logger;

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

import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.controller.AlertHelper;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.resolvers.client.ClientResolver;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.views.client.CreateIllnessView;
import com.humanharvest.organz.views.client.ModifyIllnessObject;

/**
 * Controller for the medical history page, which shows a list of all current and past illnesses for the client.
 */
public class ClientMedicalHistoryController extends SubController {

    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("d MMM yyyy");
    private static final Logger LOGGER = Logger.getLogger(ClientMedicalHistoryController.class.getName());

    private final Session session;
    private final ClientResolver resolver;
    private Client client;

    @FXML
    private Pane sidebarPane;
    @FXML
    private Pane menuBarPane;
    @FXML
    private HBox newIllnessPane;
    @FXML
    private HBox illnessButtonsPane;

    @FXML
    private TextField illnessNameField;
    @FXML
    private DatePicker dateDiagnosedPicker;
    @FXML
    private CheckBox chronicBox;
    @FXML
    private Text errorMessage;

    @FXML
    private TableView<IllnessRecord> pastIllnessView;
    @FXML
    private TableView<IllnessRecord> currentIllnessView;
    @FXML
    private TableColumn<IllnessRecord, String> illnessPastCol;
    @FXML
    private TableColumn<IllnessRecord, String> illnessCurrCol;
    @FXML
    private TableColumn<IllnessRecord, LocalDate> diagnosisDatePastCol;
    @FXML
    private TableColumn<IllnessRecord, LocalDate> diagnosisDateCurrCol;
    @FXML
    private TableColumn<IllnessRecord, LocalDate> curedDatePastCol;
    @FXML
    private TableColumn<IllnessRecord, Boolean> chronicCurrCol;
    @FXML
    private Button toggleCuredButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button toggleChronicButton;

    private TableView<IllnessRecord> selectedTableView;

    /**
     * Gets the current session and resolver from the global state.
     */
    public ClientMedicalHistoryController() {
        session = State.getSession();
        resolver = State.getClientResolver();
    }

    /**
     * Formats a table cell that holds a {@link LocalDate} value to display that value in the date time format.
     *
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
     *
     * @return The cell with the chronic formatter set.
     */
    private static TableCell<IllnessRecord, Boolean> formatChronicCell() {
        return new TableCell<IllnessRecord, Boolean>() {
            @Override
            protected void updateItem(Boolean isChronic, boolean empty) {
                super.updateItem(isChronic, empty);
                if (isChronic == null || empty || !isChronic) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText("CHRONIC");
                    setStyle("-fx-text-fill: red;");
                }
            }
        };
    }

    /**
     * Creates a sort policy where records for chronic illnesses are always sorted first, then sorts by the table's
     * current comparator. If no table comparator is active, then the default sorting is by diagnosis date descending.
     *
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
                    // negative because sorting DESC
                    return -Integer.signum(r1.getDiagnosisDate().compareTo(r2.getDiagnosisDate()));
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
                observable -> {
                    selectedTableView = pastIllnessView;
                    currentIllnessView.getSelectionModel().clearSelection();
                    enableAppropriateButtons();
                });

        currentIllnessView.getSelectionModel().selectedItemProperty().addListener(
                observable -> {
                    selectedTableView = currentIllnessView;
                    pastIllnessView.getSelectionModel().clearSelection();
                    enableAppropriateButtons();
                });

        currentIllnessView.setSortPolicy(ClientMedicalHistoryController::getChronicFirstSortPolicy);
        pastIllnessView.setSortPolicy(ClientMedicalHistoryController::getChronicFirstSortPolicy);

        dateDiagnosedPicker.setValue(LocalDate.now());
    }

    /**
     * Sets up the page using the MainController given.
     * - Loads the sidebar.
     * - Checks if the session login type is a client or a clinician, and sets the viewed client appropriately.
     * - Checks if the logged in user is a client, and if so, makes the page non-editable.
     * - Refreshes the illness tables to set initial state based on the viewed client.
     *
     * @param mainController The MainController for the window this page is loaded on.
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);

        if (session.getLoggedInUserType() == UserType.CLIENT) {
            client = session.getLoggedInClient();
            mainController.loadSidebar(sidebarPane);
            newIllnessPane.setVisible(false);
            newIllnessPane.setManaged(false);
            illnessButtonsPane.setVisible(false);
            illnessButtonsPane.setManaged(false);
        } else if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
            mainController.loadMenuBar(menuBarPane);
        }

        refresh();
        enableAppropriateButtons();
    }

    /**
     * Refreshes the past/current illness tables from the client's properties.
     */
    @Override
    public void refresh() {

        // Reload the client's medical history
        try {
            client.setIllnessHistory(resolver.getIllnessRecords(client));
        } catch (NotFoundException e) {
            AlertHelper.showNotFoundAlert(LOGGER, e, mainController);
            return;
        } catch (ServerRestException e) {
            AlertHelper.showRestAlert(LOGGER, e, mainController);
            return;
        }

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

        if (session.getLoggedInUserType() == UserType.CLIENT) {
            mainController.setTitle("Medication History:  " + client.getPreferredNameFormatted());
        } else if (windowContext.isClinViewClientWindow()) {
            mainController.setTitle("Medication History:  " + client.getFullName());

        }

        errorMessage.setText(null);
    }

    private void enableAppropriateButtons() {
        if (windowContext.isClinViewClientWindow()) {
            IllnessRecord selectedRecord = getSelectedRecord();
            if (selectedRecord == null) {
                toggleCuredButton.setDisable(true);
                toggleChronicButton.setDisable(true);
                deleteButton.setDisable(true);
                toggleCuredButton.setText("Mark as Cured");
                toggleChronicButton.setText("Mark as Chronic");

            } else if (Objects.equals(selectedTableView, currentIllnessView)) {
                toggleCuredButton.setDisable(false);
                toggleChronicButton.setDisable(false);
                deleteButton.setDisable(false);
                toggleCuredButton.setText("Mark as Cured");
                if (selectedRecord.isChronic()) {
                    toggleChronicButton.setText("Mark as not Chronic");
                } else {
                    toggleChronicButton.setText("Mark as Chronic");
                }

            } else if (Objects.equals(selectedTableView, pastIllnessView)) {
                toggleCuredButton.setDisable(false);
                toggleChronicButton.setDisable(false);
                deleteButton.setDisable(false);
                toggleCuredButton.setText("Mark as not Cured");
                toggleChronicButton.setText("Mark as Chronic");
            }
        }
    }

    /**
     * Gets the currently selected record in the currently selected table.
     *
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
     * Moves the currently selected illness record.
     * If it is chronic, it doesn't move it.
     * If it is in past illnesses, then it is moved to current illnesses and its cured date is set to null.
     * If it is in current illnesses, then it is moved to past illnesses and its cured date to the current date.
     */
    @FXML
    private void toggleCured() {

        IllnessRecord record = getSelectedRecord();
        ModifyIllnessObject modifyIllnessObject = new ModifyIllnessObject();
        if (record != null) {
            if (record.isChronic()) {
                PageNavigator.showAlert(AlertType.ERROR,
                        "Can't move a chronic illness to past illnesses.",
                        "An illness can't be cured if it is chronic. If the illness has been cured, first mark it as"
                                + " not chronic.", mainController.getStage());
            } else if (Objects.equals(selectedTableView, currentIllnessView)) {
                modifyIllnessObject.setCuredDate(LocalDate.now());
                State.getClientResolver().modifyIllnessRecord(client, record, modifyIllnessObject);

                PageNavigator.refreshAllWindows();
            } else if (Objects.equals(selectedTableView, pastIllnessView)) {
                modifyIllnessObject.setCuredDate(null);
                State.getClientResolver().modifyIllnessRecord(client, record, modifyIllnessObject);
                PageNavigator.refreshAllWindows();
            }
        }
    }

    /**
     * Deletes the currently selected illness record.
     */
    @FXML
    private void deleteIllness() {
        IllnessRecord record = getSelectedRecord();
        if (record != null) {
            try {
                State.getClientResolver().deleteIllnessRecord(client, record);
            } catch (NotFoundException e) {
                AlertHelper.showNotFoundAlert(LOGGER, e, mainController);
            } catch (ServerRestException e) {
                AlertHelper.showRestAlert(LOGGER, e, mainController);
                return;
            } catch (IfMatchFailedException e) {
                AlertHelper.showIfMatchAlert(LOGGER, e, mainController);
                return;
            }
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
        ModifyIllnessObject modifyIllnessObject = new ModifyIllnessObject();
        if (record != null) {

            if (record.isChronic()) {
                // Current, chronic illness -> Current illness
                modifyIllnessObject.setChronic(false);
            } else {
                if (record.getCuredDate() != null) {
                    // Past illness -> Current, chronic illness
                    modifyIllnessObject.setCuredDate(null);
                }
                // Illness -> chronic illness
                modifyIllnessObject.setChronic(true);
            }
            try {
                State.getClientResolver().modifyIllnessRecord(client, record, modifyIllnessObject);

            } catch (NotFoundException e) {
                AlertHelper.showNotFoundAlert(LOGGER, e, mainController);
            } catch (ServerRestException e) {
                AlertHelper.showRestAlert(LOGGER, e, mainController);
                return;
            } catch (IfMatchFailedException e) {
                AlertHelper.showIfMatchAlert(LOGGER, e, mainController);
                return;
            }
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
        boolean inFuture = dateDiagnosed.isAfter(LocalDate.now());

        if (illnessName == null || illnessName.isEmpty()) {
            errorMessage.setText("Illness name must not be blank.");
        } else if (beforeBirth) {
            errorMessage.setText("Diagnosis date cannot be before person is born.");
        } else if (inFuture) {
            errorMessage.setText("Diagnosis date cannot be in the future.");
        } else {
            CreateIllnessView view = new CreateIllnessView(illnessName, dateDiagnosed, isChronic);

            try {
                State.getClientResolver().addIllnessRecord(client, view);
            } catch (NotFoundException e) {
                AlertHelper.showNotFoundAlert(LOGGER, e, mainController);
            } catch (ServerRestException e) {
                AlertHelper.showRestAlert(LOGGER, e, mainController);
                return;
            } catch (IfMatchFailedException e) {
                AlertHelper.showIfMatchAlert(LOGGER, e, mainController);
                return;
            }

            illnessNameField.setText(null);
            errorMessage.setText(null);
            dateDiagnosedPicker.setValue(LocalDate.now());
            chronicBox.setSelected(false);
            PageNavigator.refreshAllWindows();
        }
    }
}
