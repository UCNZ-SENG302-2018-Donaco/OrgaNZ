package com.humanharvest.organz.controller.client;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.client.AddMedicationRecordAction;
import com.humanharvest.organz.actions.client.DeleteMedicationRecordAction;
import com.humanharvest.organz.actions.client.ModifyMedicationRecordAction;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.BadDrugNameException;
import com.humanharvest.organz.utilities.exceptions.BadGatewayException;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.web.DrugInteractionsHandler;
import com.humanharvest.organz.utilities.web.MedActiveIngredientsHandler;
import com.humanharvest.organz.utilities.web.MedAutoCompleteHandler;

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;

/**
 * Controller for the view/edit medications page.
 */
public class ViewMedicationsController extends SubController {

    private Session session;
    private ActionInvoker invoker;
    private ClientManager manager;
    private Client client;
    private List<String> lastResponse;
    private MedAutoCompleteHandler autoCompleteHandler;
    private MedActiveIngredientsHandler activeIngredientsHandler;
    private DrugInteractionsHandler drugInteractionsHandler;

    @FXML
    private Pane sidebarPane, menuBarPane;

    @FXML
    private TextField newMedField;

    @FXML
    private HBox newMedicationPane;

    @FXML
    private Button moveToHistoryButton, moveToCurrentButton, deleteButton;

    @FXML
    private ListView<MedicationRecord> pastMedicationsView, currentMedicationsView;

    private ListView<MedicationRecord> selectedListView = null;
    private boolean selectingMultiple = false;

    public ViewMedicationsController() {
        session = State.getSession();
        invoker = State.getInvoker();
        manager = State.getClientManager();
    }

    void setDrugInteractionsHandler(DrugInteractionsHandler handler) {
        this.drugInteractionsHandler = handler;
    }

    void setActiveIngredientsHandler(MedActiveIngredientsHandler handler) {
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
        new AutoCompletionTextFieldBinding<>(newMedField, param -> {
            String input = param.getUserText().trim();
            return getSuggestions(input);
        });

        activeIngredientsHandler = new MedActiveIngredientsHandler();
        drugInteractionsHandler = new DrugInteractionsHandler();

        pastMedicationsView.getSelectionModel().selectedItemProperty().addListener(
                (observable) -> {
                    selectedListView = pastMedicationsView;
                    // Clear the other list if CTRL or SHIFT is not being held down
                    if (!selectingMultiple) {
                        currentMedicationsView.getSelectionModel().clearSelection();
                    }
                });

        currentMedicationsView.getSelectionModel().selectedItemProperty().addListener(
                (observable) -> {
                    selectedListView = currentMedicationsView;
                    // Clear the other list if CTRL or SHIFT is not being held down
                    if (!selectingMultiple) {
                        pastMedicationsView.getSelectionModel().clearSelection();
                    }
                });

        pastMedicationsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        currentMedicationsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
        if (session.getLoggedInUserType() == Session.UserType.CLIENT) {
            client = session.getLoggedInClient();
            mainController.loadSidebar(sidebarPane);
            newMedicationPane.setVisible(false);
            newMedicationPane.setManaged(false);
            moveToHistoryButton.setDisable(true);
            moveToCurrentButton.setDisable(true);
            deleteButton.setDisable(true);
        } else if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
            mainController.loadMenuBar(menuBarPane);
        }

        refreshMedicationLists();

        refresh();
        trackControlOrShiftKeyPressed();
    }

    @Override
    public void refresh() {
        if (session.getLoggedInUserType() == UserType.CLIENT) {
            mainController.setTitle("View Medications:  " + client.getPreferredName());
        } else if (windowContext.isClinViewClientWindow()) {
            mainController.setTitle("View Medications:  " + client.getFullName());

        }
        refreshMedicationLists();
    }

    /**
     * Refreshes the past/current medication list views from the client's properties.
     */
    private void refreshMedicationLists() {
        pastMedicationsView.setItems(FXCollections.observableArrayList(client.getPastMedications()));
        currentMedicationsView.setItems(FXCollections.observableArrayList(client.getCurrentMedications()));
    }

    /**
     * Moves the MedicationRecord selected in the current medications list to the past medications list. Also:
     * - Sets the date the client stopped taking the medication to the current date.
     * - Removes the MedicationRecord from the current medications list.
     * - Refreshes both list views.
     */
    @FXML
    private void moveMedicationToHistory() {
        MedicationRecord record = currentMedicationsView.getSelectionModel().getSelectedItem();
        if (record != null) {
            ModifyMedicationRecordAction action = new ModifyMedicationRecordAction(record, manager);
            action.changeStopped(LocalDate.now());

            invoker.execute(action);
            PageNavigator.refreshAllWindows();
            refreshMedicationLists();
        }
    }

    /**
     * Moves the MedicationRecord selected in the past medications list to the current medications list. Also:
     * - Sets the date the client started taking the medication to the current date.
     * - Sets the date the client stopped taking the medication to null (hasn't stopped yet).
     * - Removes the MedicationRecord from the past medications list.
     * - Refreshes both list views.
     */
    @FXML
    private void moveMedicationToCurrent() {
        MedicationRecord record = pastMedicationsView.getSelectionModel().getSelectedItem();
        if (record != null) {
            ModifyMedicationRecordAction action = new ModifyMedicationRecordAction(record, manager);
            action.changeStopped(null);

            invoker.execute(action);
            PageNavigator.refreshAllWindows();
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
     * Tracks if the control key is pressed or released, and updates selectingMultiple accordingly.
     */
    private void trackControlOrShiftKeyPressed() {
        Scene scene = mainController.getStage().getScene();
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.CONTROL || e.getCode() == KeyCode.SHIFT) {
                selectingMultiple = true;
            }
        });
        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.CONTROL || e.getCode() == KeyCode.SHIFT) {
                selectingMultiple = false;
            }
        });
    }

    /**
     * Adds a new medication with the current value of the new medication text field.
     */
    @FXML
    private void addButtonPressed() {
        addMedication(newMedField.getText());
    }

    /**
     * Creates a new MedicationRecord for a medication with the given name, sets its 'started' date to the
     * current date, then adds it to the client's current medications list.
     * @param newMedName The name of the medication to add a new instance of.
     */
    private void addMedication(String newMedName) {
        if (!newMedName.equals("")) {
            MedicationRecord record = new MedicationRecord(newMedName, LocalDate.now(), null);
            AddMedicationRecordAction action = new AddMedicationRecordAction(client, record, manager);

            invoker.execute(action);
            newMedField.setText("");
            PageNavigator.refreshAllWindows();
            refreshMedicationLists();
        }
    }

    /**
     * Returns the currently selected record from the currently selected list view.
     * @return The selected record, or null if no record is currently selected.
     */
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
     */
    @FXML
    private void deleteMedication() {
        MedicationRecord record = getSelectedRecord();
        if (record != null) {
            DeleteMedicationRecordAction action = new DeleteMedicationRecordAction(client, record, manager);

            invoker.execute(action);
            PageNavigator.refreshAllWindows();
            refreshMedicationLists();
        }
    }

    /**
     * Generates a pop-up with a list of active ingredients.
     */
    @FXML
    private void viewActiveIngredients() {
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
            });

            task.setOnFailed(e -> {
                alert.setAlertType(AlertType.ERROR);
                alert.setContentText("Error loading results. Please try again later.");

            });

            new Thread(task).start();
        }
    }


    /**
     * Generates a pop-up with a list of interactions between the 2 medications selected. If any errors occurs when
     * retrieving the interactions, the popup will display the appropriate error message instead.
     */
    @FXML
    private void viewInteractions() {

        // Check if there are two medications selected
        List<MedicationRecord> selectedItems = new ArrayList<>();
        selectedItems.addAll(currentMedicationsView.getSelectionModel().getSelectedItems());
        selectedItems.addAll(pastMedicationsView.getSelectionModel().getSelectedItems());

        if (selectedItems.size() != 2) {
            PageNavigator.showAlert(AlertType.ERROR,
                    String.format("Incorrect number of medications selected (%d).", selectedItems.size()),
                    "Please select exactly two medications to view their interactions.");

        } else {
            Collections.sort(selectedItems);
            String medication1 = selectedItems.get(0).getMedicationName();
            String medication2 = selectedItems.get(1).getMedicationName();

            // Generate initial alert popup
            Alert alert = PageNavigator.generateAlert(AlertType.INFORMATION,
                    String.format("Interactions between %s and %s", medication1, medication2),
                    "Loading...");
            alert.show();

            Task<List<String>> task = new Task<List<String>>() {
                @Override
                public List<String> call() throws IOException, BadDrugNameException, BadGatewayException {
                    return drugInteractionsHandler.getInteractions(client, medication1, medication2);
                }
            };

            task.setOnFailed(event -> {
                alert.setAlertType(AlertType.ERROR);
                alert.setContentText("An error occurred when retrieving drug interactions: \n" +
                        task.getException().getMessage());
                task.getException().printStackTrace();
            });

            task.setOnSucceeded(event -> {
                List<String> interactions = task.getValue();

                if (interactions.size() == 0) {
                    alert.setAlertType(AlertType.WARNING);
                    alert.setContentText(String.format(
                            "A study has not yet been done on the interactions between '%s' and '%s'.",
                            medication1, medication2));
                } else {
                    String interactionsText = interactions.stream().collect(Collectors.joining("\n"));
                    alert.setContentText(interactionsText);
                }
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
