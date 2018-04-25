package seng302.Controller.Donor;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import seng302.Actions.ActionInvoker;
import seng302.Actions.Donor.AddMedicationRecordAction;
import seng302.Actions.Donor.DeleteMedicationRecordAction;
import seng302.Actions.Donor.ModifyMedicationRecordAction;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.Donor;
import seng302.MedicationRecord;
import seng302.State.Session;
import seng302.State.State;
import seng302.Utilities.View.PageNavigator;
import seng302.Utilities.Web.MedActiveIngredientsHandler;
import seng302.Utilities.Web.MedAutoCompleteHandler;

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;

/**
 * Controller for the view/edit medications page.
 */
public class ViewMedicationsController extends SubController {

    private Session session;
    private ActionInvoker invoker;
    private Donor donor;
    private List<String> lastResponse;
    private MedAutoCompleteHandler autoCompleteHandler;
    private MedActiveIngredientsHandler activeIngredientsHandler;

    @FXML
    private Pane sidebarPane;

    @FXML
    private TextField newMedField;

    @FXML
    private HBox newMedicationPane;

    @FXML
    private Button moveToHistoryButton, moveToCurrentButton, deleteButton;

    @FXML
    private ListView<MedicationRecord> pastMedicationsView, currentMedicationsView;

    private ListView<MedicationRecord> selectedListView = null;

    public ViewMedicationsController() {
        session = State.getSession();
        invoker = State.getInvoker();
    }

    public void setActiveIngredientsHandler(MedActiveIngredientsHandler handler) {
        this.activeIngredientsHandler = handler;
    }

    /**
     * Initializes the UI for this page.
     * - Starts the WebAPIHandler for drug name autocompletion.
     * - Sets listeners for changing selection on two list views so that if an item is selected on one, the selection
     * is removed from the other.
     */
    @FXML
    private void initialize() {
        autoCompleteHandler = new MedAutoCompleteHandler();
        new AutoCompletionTextFieldBinding<String>(newMedField, param -> {
            String input = param.getUserText().trim();
            return getSuggestions(input);
        });

        activeIngredientsHandler = new MedActiveIngredientsHandler();

        pastMedicationsView.getSelectionModel().selectedItemProperty().addListener(
                (observable) -> {
                    selectedListView = pastMedicationsView;
                    currentMedicationsView.getSelectionModel().clearSelection();
                });

        currentMedicationsView.getSelectionModel().selectedItemProperty().addListener(
                (observable) -> {
                    selectedListView = currentMedicationsView;
                    pastMedicationsView.getSelectionModel().clearSelection();
                });
    }

    /**
     * Sets up the page using the MainController given.
     * - Loads the sidebar.
     * - Checks if the session login type is a donor or a clinician, and sets the viewed donor appropriately.
     * - Refreshes the medication list views to set initial state based on the viewed donor.
     * - Checks if the logged in user is a donor, and if so, makes the page non-editable.
     * @param mainController The MainController for the window this page is loaded on.
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.loadSidebar(sidebarPane);

        if (session.getLoggedInUserType() == Session.UserType.DONOR) {
            donor = session.getLoggedInDonor();

            newMedicationPane.setVisible(false);
            newMedicationPane.setManaged(false);
            moveToHistoryButton.setDisable(true);
            moveToCurrentButton.setDisable(true);
            deleteButton.setDisable(true);
        } else if (windowContext.isClinViewDonorWindow()) {
            donor = windowContext.getViewDonor();
        }

        refreshMedicationLists();
    }

    /**
     * Refreshes the past/current medication list views from the donor's properties.
     */
    private void refreshMedicationLists() {
        pastMedicationsView.setItems(FXCollections.observableArrayList(donor.getPastMedications()));
        currentMedicationsView.setItems(FXCollections.observableArrayList(donor.getCurrentMedications()));
    }

    /**
     * Moves the MedicationRecord selected in the current medications list to the past medications list. Also:
     * - Sets the date the donor stopped taking the medication to the current date.
     * - Removes the MedicationRecord from the current medications list.
     * - Refreshes both list views.
     * @param event When the '<' button is pressed.
     */
    @FXML
    private void moveMedicationToHistory(ActionEvent event) {
        MedicationRecord record = currentMedicationsView.getSelectionModel().getSelectedItem();
        if (record != null) {
            ModifyMedicationRecordAction action = new ModifyMedicationRecordAction(record);
            action.changeStopped(LocalDate.now());

            invoker.execute(action);
            refreshMedicationLists();
        }
    }

    /**
     * Moves the MedicationRecord selected in the past medications list to the current medications list. Also:
     * - Sets the date the donor started taking the medication to the current date.
     * - Sets the date the donor stopped taking the medication to null (hasn't stopped yet).
     * - Removes the MedicationRecord from the past medications list.
     * - Refreshes both list views.
     * @param event When the '>' button is pressed.
     */
    @FXML
    private void moveMedicationToCurrent(ActionEvent event) {
        MedicationRecord record = pastMedicationsView.getSelectionModel().getSelectedItem();
        if (record != null) {
            ModifyMedicationRecordAction action = new ModifyMedicationRecordAction(record);
            action.changeStopped(null);

            invoker.execute(action);
            refreshMedicationLists();
        }
    }

    /**
     * Checks whether the key pressed was ENTER, and if so, adds a new medication with the current value of the new
     * medication text field.
     * @param keyEvent When a key is pressed and focus is on the new medication text field.
     */
    @FXML
    public void newMedKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            addMedication(newMedField.getText());
        }
    }

    /**
     * Adds a new medication with the current value of the new medication text field.
     * @param event When the 'add medication' button is pressed.
     */
    @FXML
    private void addButtonPressed(ActionEvent event) {
        addMedication(newMedField.getText());
    }

    /**
     * Creates a new MedicationRecord for a medication with the given name, sets its 'started' date to the
     * current date, then adds it to the donor's current medications list.
     * @param newMedName The name of the medication to add a new instance of.
     */
    private void addMedication(String newMedName) {
        if (!newMedName.equals("")) {
            MedicationRecord record = new MedicationRecord(newMedName, LocalDate.now(), null);
            AddMedicationRecordAction action = new AddMedicationRecordAction(donor, record);

            invoker.execute(action);
            newMedField.setText("");
            refreshMedicationLists();
        }
    }

    private MedicationRecord getSelectedRecord() {
        if (selectedListView != null) {
            return selectedListView.getSelectionModel().getSelectedItem();
        } else {
            return null;
        }
    }

    /**
     * Deletes the currently selected MedicationRecord. Will determine which of the list views is currently
     * selected, then delete from the appropriate one. If neither list view is currently selected, this will have no
     * effect.
     * @param event When the 'delete' button is clicked.
     */
    @FXML
    private void deleteMedication(ActionEvent event) {
        MedicationRecord record = getSelectedRecord();
        if (record != null) {
            DeleteMedicationRecordAction action = new DeleteMedicationRecordAction(donor, record);

            invoker.execute(action);
            refreshMedicationLists();
        }
    }

    /**
     * Generates a pop-up with a list of active ingredients.
     * @param event When the 'View active ingredients' button is clicked.
     */
    @FXML
    private void viewActiveIngredients(ActionEvent event) {
        MedicationRecord medicationRecord = getSelectedRecord();

        if (medicationRecord != null) {
            String medicationName = medicationRecord.getMedicationName();
            // Generate initial alert popup
            String alertTitle = "Active ingredients in " + medicationName;
            Alert alert = PageNavigator.generateAlert(AlertType.INFORMATION, alertTitle, "Loading...");
            alert.show();

            Task<List<String>> task = new Task<List<String>>() {
                @Override
                public List<String> call() throws IOException {
                    return activeIngredientsHandler.getActiveIngredients(medicationName);
                }
            };

            task.setOnSucceeded(e -> {
                List<String> activeIngredients = task.getValue();
                // If there are no results, display an error, else display the results.
                // It is assumed that every valid drug has active ingredients, thus if an empty list is returned,
                //     then the drug name wasn't valid.
                if (activeIngredients.isEmpty()) {
                    alert.setAlertType(AlertType.ERROR);
                    alert.setContentText("No results found for " + medicationName);
                } else {
                    // Build list of active ingredients into a string, each ingredient on a new line
                    StringBuilder sb = new StringBuilder();
                    for (String ingredient : activeIngredients) {
                        sb.append(ingredient).append("\n");
                    }
                    alert.setContentText(sb.toString());
                }
                PageNavigator.resizeAlert(alert);
            });

            task.setOnFailed(e -> {
                alert.setAlertType(AlertType.ERROR);
                alert.setContentText("Error loading results. Please try again later.");

            });

            new Thread(task).start();
        }
    }

    /**
     * Gets a list of medication suggestions for the given input from the autocomplete WebAPIHandler.
     * @param input The string to search for suggested drug names that start with this.
     * @return The list of suggested medication names.
     */
    private List<String> getSuggestions(String input) {
        if (input.equals("")) {
            return null;
        } else {
            List<String> results = autoCompleteHandler.getSuggestions(input);
            if (input.equals(newMedField.getText())) {
                lastResponse = results;
                return results;
            } else {
                return lastResponse;
            }
        }
    }
}
