package com.humanharvest.organz.controller.client;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.controller.AlertHelper;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.BadDrugNameException;
import com.humanharvest.organz.utilities.exceptions.BadGatewayException;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.web.DrugInteractionsHandler;
import com.humanharvest.organz.utilities.web.MedActiveIngredientsHandler;
import com.humanharvest.organz.utilities.web.MedAutoCompleteHandler;
import com.humanharvest.organz.views.client.CreateMedicationRecordView;

/**
 * Controller for the view/edit medications page.
 */
public class ViewMedicationsController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(ViewMedicationsController.class.getName());

    private final Session session;
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
    private TextArea medicationIngredients, medicationInteractions;

    @FXML
    private HBox newMedicationPane;

    @FXML
    private Button moveToHistoryButton, moveToCurrentButton, deleteButton;

    @FXML
    private ListView<MedicationRecord> pastMedicationsView, currentMedicationsView;

    private ListView<MedicationRecord> selectedListView;

    public ViewMedicationsController() {
        session = State.getSession();
    }

    void setDrugInteractionsHandler(DrugInteractionsHandler handler) {
        drugInteractionsHandler = handler;
    }

    void setActiveIngredientsHandler(MedActiveIngredientsHandler handler) {
        activeIngredientsHandler = handler;
    }

    /**
     * Creates a cell factory for the list view, that allows cells to be deselected by clicking a second time
     * Only up to two cells may be selected at once
     *
     * @param listView to create a cellfactory for
     */
    private void configureCellFactory(ListView<MedicationRecord> listView) {
        listView.setCellFactory(view -> {
            ListCell<MedicationRecord> cell = new ListCell<MedicationRecord>() {
                @Override
                public void updateItem(MedicationRecord record, boolean empty) {
                    super.updateItem(record, empty);
                    if (empty) {
                        setText("");
                    } else {
                        setText(record.toString());
                    }
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                listView.requestFocus();
                if (!cell.isEmpty()) {
                    int numSelected = getSelectedRecords().size();
                    int index = cell.getIndex();

                    if (listView.getSelectionModel().getSelectedIndices().contains(index)) {
                        // Already selected, so deselect it
                        listView.getSelectionModel().clearSelection(index);
                    } else if (numSelected < 2) {  // Only select if there are less than two currently selected
                        listView.getSelectionModel().select(index);
                    }
                    updateMedicationInformation(); // display either interactions, ingredients, or neither
                    event.consume();
                }
            });
            return cell;
        });
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

        configureCellFactory(currentMedicationsView);
        configureCellFactory(pastMedicationsView);

        pastMedicationsView.getSelectionModel().selectedItemProperty().addListener(
                observable -> {
                    selectedListView = pastMedicationsView;
                });

        currentMedicationsView.getSelectionModel().selectedItemProperty().addListener(
                observable -> {
                    selectedListView = currentMedicationsView;
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
     *
     * @param mainController The MainController for the window this page is loaded on.
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        if (session.getLoggedInUserType() == Session.UserType.CLIENT) {
            client = session.getLoggedInClient();
            newMedicationPane.setVisible(false);
            newMedicationPane.setManaged(false);
            moveToHistoryButton.setDisable(true);
            moveToCurrentButton.setDisable(true);
            deleteButton.setDisable(true);
        } else if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
        }

        mainController.loadNavigation(menuBarPane);
        refreshMedicationLists();

        refresh();
    }

    /**
     * Updates the window title and medication lists
     * This page will always refresh as all change are immediately applied so there is no risk of loss
     */
    @Override
    public void refresh() {
        if (session.getLoggedInUserType() == UserType.CLIENT) {
            mainController.setTitle("View Medications:  " + client.getPreferredNameFormatted());
        } else if (windowContext.isClinViewClientWindow()) {
            mainController.setTitle("View Medications:  " + client.getFullName());
        }
        refreshMedicationLists();
    }

    /**
     * Refreshes the past/current medication list views from the client's properties.
     */
    private void refreshMedicationLists() {
        try {
            client.setMedicationHistory(State.getClientResolver().getMedicationRecords(client));

        } catch (NotFoundException e) {
            AlertHelper.showNotFoundAlert(LOGGER, e, mainController);
            return;
        } catch (ServerRestException e) {
            AlertHelper.showRestAlert(LOGGER, e, mainController);
            return;
        }

        pastMedicationsView.setItems(FXCollections.observableArrayList(client.getPastMedications()));
        currentMedicationsView.setItems(FXCollections.observableArrayList(client.getCurrentMedications()));
    }

    /**
     * Gets all selected medication records from both the current and past medication lists
     *
     * @return list of all currently selected medication records
     */
    private List<MedicationRecord> getSelectedRecords() {
        List<MedicationRecord> selectedItems = new ArrayList<>();
        selectedItems.addAll(currentMedicationsView.getSelectionModel().getSelectedItems());
        selectedItems.addAll(pastMedicationsView.getSelectionModel().getSelectedItems());

        return selectedItems;
    }

    /**
     * Creates and executes the resolver to update the given medication record, either setting it as a current
     * medication or a past one
     *
     * @param date date to set the stop date of the medication record to, either null or the current date
     * @param record the record to modify
     */
    private void updateMedicationHistory(LocalDate date, MedicationRecord record) {

        try {
            State.getClientResolver().modifyMedicationRecord(client, record, date);
            record.setStopped(date);
        } catch (NotFoundException e) {
            AlertHelper.showNotFoundAlert(LOGGER, e, mainController);
        } catch (ServerRestException e) {
            AlertHelper.showRestAlert(LOGGER, e, mainController);
        } catch (IfMatchFailedException e) {
            AlertHelper.showIfMatchAlert(LOGGER, e, mainController);
        }
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
            updateMedicationHistory(LocalDate.now(), record);
            PageNavigator.refreshAllWindows();
            refreshMedicationLists();
        }
    }

    /**
     * Moves the MedicationRecord selected in the past medications list to the current medications list. Also:
     * - Sets the date the client stopped taking the medication to null (hasn't stopped yet).
     * - Refreshes both list views.
     */
    @FXML
    private void moveMedicationToCurrent() {
        MedicationRecord record = pastMedicationsView.getSelectionModel().getSelectedItem();
        if (record != null) {
            updateMedicationHistory(null, record);
            PageNavigator.refreshAllWindows();
            refreshMedicationLists();
        }
    }

    /**
     * Checks whether the key pressed was ENTER, and if so, adds a new medication with the current value of the new
     * medication text field.
     *
     * @param keyEvent When a key is pressed and focus is on the new medication text field.
     */
    @FXML
    public void newMedKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            addMedication(newMedField.getText());
        }
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
     *
     * @param newMedName The name of the medication to add a new instance of.
     */
    private void addMedication(String newMedName) {
        if (!Objects.equals(newMedName, "")) {
            CreateMedicationRecordView record = new CreateMedicationRecordView(newMedName, LocalDate.now());

            try {
                State.getClientResolver().addMedicationRecord(client, record);
            } catch (NotFoundException e) {
                AlertHelper.showNotFoundAlert(LOGGER, e, mainController);
                return;
            } catch (ServerRestException e) {
                AlertHelper.showRestAlert(LOGGER, e, mainController);
                return;
            } catch (IfMatchFailedException e) {
                AlertHelper.showIfMatchAlert(LOGGER, e, mainController);
                return;
            }

            newMedField.setText("");
            PageNavigator.refreshAllWindows();
            refreshMedicationLists();
        }
    }

    /**
     * Returns the currently selected record from the currently selected list view.
     *
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

            try {
                State.getClientResolver().deleteMedicationRecord(client, record);
            } catch (NotFoundException e) {
                AlertHelper.showNotFoundAlert(LOGGER, e, mainController);
                return;
            } catch (ServerRestException e) {
                AlertHelper.showRestAlert(LOGGER, e, mainController);
                return;
            }

            PageNavigator.refreshAllWindows();
            refreshMedicationLists();
        }
    }

    /**
     * Checks what medications are currently selected, if one is selected, its ingredients are displayed, if two are
     * selected, their interactions are displayed. Otherwise nothing is displayed for medication ingredients and
     * interactions
     */
    private void updateMedicationInformation() {
        List<MedicationRecord> selectedItems = getSelectedRecords();

        if (selectedItems.size() == 1) {
            setActiveIngredients(selectedItems.get(0));
            medicationInteractions.clear();
        } else if (selectedItems.size() == 2) {
            setInteractions(selectedItems);
            medicationIngredients.clear();
        } else {
            // If None or more than two are selected, the textareas are reset to their prompts
            medicationIngredients.clear();
            medicationInteractions.clear();
        }
    }

    /**
     * Displays the ingredients of the currently selected medication, given that it is a valid medication
     *
     * @param selectedMedication Currently selected medication
     */
    private void setActiveIngredients(MedicationRecord selectedMedication) {

        if (selectedMedication != null) {
            String medicationName = selectedMedication.getMedicationName();

            medicationIngredients.setText("Loading ...");

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
                    medicationIngredients.setText("No active ingredients found for " + medicationName);
                } else {
                    // Build list of active ingredients into a string, each ingredient on a new line
                    String sb = String.join("\n", activeIngredients);
                    String formattedIngredients = String.format("Active ingredients in %s: %n%s", medicationName, sb);
                    medicationIngredients.setText(formattedIngredients);
                }
            });

            task.setOnFailed(e -> {
                medicationIngredients.setText("Error loading ingredients, please try again later");
                LOGGER.log(Level.WARNING, "Error loading ingredients", e);
            });

            new Thread(task).start();
        }

    }

    /**
     * Displays the interactions between the two currently selected medications, given that both are valid medications
     *
     * @param selectedMedications The two currently selected medications
     */
    private void setInteractions(List<MedicationRecord> selectedMedications) {
        selectedMedications.sort(Comparator.comparing(MedicationRecord::getMedicationName));
        String medication1 = selectedMedications.get(0).getMedicationName();
        String medication2 = selectedMedications.get(1).getMedicationName();

        medicationIngredients.clear();

        medicationInteractions.setText("Loading ...");

        Task<List<String>> task = new Task<List<String>>() {
            @Override
            public List<String> call() throws IOException, BadDrugNameException, BadGatewayException {
                return drugInteractionsHandler.getInteractions(client, medication1, medication2);
            }
        };

        task.setOnFailed(event -> {
            medicationInteractions.setText("An error occurred when retrieving drug interactions: \n" +
                    task.getException().getMessage());
            LOGGER.log(Level.WARNING, "Error when retrieving drug interactions", task.getException());
        });

        task.setOnSucceeded(event -> {
            List<String> interactions = task.getValue();

            if (interactions.isEmpty()) {

                medicationInteractions.setText(String.format(
                        "There is no information on interactions between %s and %s.",
                        medication1, medication2));
            } else {
                String interactionsText = String.join("\n", interactions);
                String formattedInteractions =
                        String.format("Interactions between %s and %s: %n%s",
                                medication1,
                                medication2,
                                interactionsText);
                medicationInteractions.setText(formattedInteractions);
            }
        });

        new Thread(task).start();
    }

    /**
     * Gets a list of medication suggestions for the given input from the autocomplete WebAPIHandler.
     *
     * @param input The string to search for suggested drug names that start with this.
     * @return The list of suggested medication names.
     */
    private List<String> getSuggestions(String input) {
        if (Objects.equals(input, "")) {
            return Collections.emptyList();
        } else {
            List<String> results = autoCompleteHandler.getSuggestions(input);
            if (Objects.equals(input, newMedField.getText())) {
                lastResponse = results;
                return results;
            } else {
                return lastResponse;
            }
        }
    }
}
