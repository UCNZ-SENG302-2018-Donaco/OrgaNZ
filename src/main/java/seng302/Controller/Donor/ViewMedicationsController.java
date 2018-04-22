package seng302.Controller.Donor;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
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

import seng302.Actions.ActionInvoker;
import seng302.Actions.Donor.AddMedicationRecordAction;
import seng302.Actions.Donor.DeleteMedicationRecordAction;
import seng302.Actions.Donor.ModifyMedicationRecordAction;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.Donor;
import seng302.MedicationRecord;
import seng302.State.Session;
import seng302.State.Session.UserType;
import seng302.State.State;
import seng302.Utilities.Exceptions.BadGatewayException;
import seng302.Utilities.View.PageNavigator;
import seng302.Utilities.Web.DrugInteractionsHandler;
import seng302.Utilities.Web.MedAutoCompleteHandler;
import seng302.Utilities.Web.MedActiveIngredientsHandler;

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;

/**
 * Controller for the view/edit medications page.
 */
public class ViewMedicationsController extends SubController {

    private static final String SUCCESSFUL = "Successful";
    private static final String BAD_NAME = "Bad name";
    private static final String BAD_GATEWAY = "Bad gateway";
    private static final String IO_EXCEPTION = "IO Exception";

    private Session session;
    private ActionInvoker invoker;
    private Donor donor;
    private List<String> lastResponse;
    private MedAutoCompleteHandler autoCompleteHandler;
    private MedActiveIngredientsHandler activeIngredientsHandler;
    private DrugInteractionsHandler drugInteractionsHandler;

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
    private boolean controlIsDepressed = false;

    public ViewMedicationsController() {
        session = State.getSession();
        invoker = State.getInvoker();
    }

    public void setDrugInteractionsHandler(DrugInteractionsHandler handler) {
        this.drugInteractionsHandler = handler;
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
        drugInteractionsHandler = new DrugInteractionsHandler();

        pastMedicationsView.getSelectionModel().selectedItemProperty().addListener(
                (observable) -> {
                    trackControlKeyPressed();
                    selectedListView = pastMedicationsView;
                    // Clear the other list if Ctrl is not being held down
                    if (!controlIsDepressed) {
                        currentMedicationsView.getSelectionModel().clearSelection();
                    }
                });

        currentMedicationsView.getSelectionModel().selectedItemProperty().addListener(
                (observable) -> {
                    trackControlKeyPressed();
                    selectedListView = currentMedicationsView;
                    // Clear the other list if Ctrl is not being held down
                    if (!controlIsDepressed) {
                        pastMedicationsView.getSelectionModel().clearSelection();
                    }
                });

        pastMedicationsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        currentMedicationsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
     * Tracks if the control key is pressed or released, and updates controlIsDepressed accordingly.
     */
    private void trackControlKeyPressed() {
        Scene scene = sidebarPane.getScene();
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.CONTROL) {
                controlIsDepressed = true;
            }
        });
        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.CONTROL) {
                controlIsDepressed = false;
            }
        });
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

    /**
     * Deletes the currently selected MedicationRecord. Will determine which of the list views is currently
     * selected, then delete from the appropriate one. If neither list view is currently selected, this will have no
     * effect.
     * @param event When the 'delete' button is clicked.
     */
    @FXML
    private void deleteMedication(ActionEvent event) {
        if (selectedListView != null) {
            MedicationRecord record = selectedListView.getSelectionModel().getSelectedItem();
            if (record != null) {
                DeleteMedicationRecordAction action = new DeleteMedicationRecordAction(donor, record);

                invoker.execute(action);
                refreshMedicationLists();
            }
        }
    }


    /**
     * Generates a pop-up with a list of active ingredients.
     * @param event When the 'View active ingredients' button is clicked.
     */
    @FXML
    private void viewActiveIngredients(ActionEvent event) {
        // Figure out which record is currently selected
        MedicationRecord currentMedicationRecord = currentMedicationsView.getSelectionModel().getSelectedItem();
        MedicationRecord pastMedicationRecord = pastMedicationsView.getSelectionModel().getSelectedItem();
        MedicationRecord medicationRecord;
        if (currentMedicationRecord != null) {
            medicationRecord = currentMedicationRecord;
        } else if (pastMedicationRecord != null) {
            medicationRecord = pastMedicationRecord;
        } else {
            medicationRecord = null;
        }

        if (medicationRecord != null) {
            String currentMedication = medicationRecord.getMedicationName();
            // Generate initial alert popup
            String alertTitle = "Active ingredients in " + currentMedication;
            Alert alert = PageNavigator.generateAlert(AlertType.INFORMATION, alertTitle, "Loading...");
            alert.show();

            Task<List<String>> task = new Task<List<String>>() {
                @Override
                public List<String> call() {
                    return activeIngredientsHandler.getActiveIngredients(currentMedication);
                }
            };

            task.setOnSucceeded(e -> {
                List<String> activeIngredients = task.getValue();
                // If there are no results, display an error, else display the results.
                // It is assumed that every valid drug has active ingredients, thus if an empty list is returned,
                //     then the drug name wasn't valid.
                if (activeIngredients.isEmpty()) {
                    alert.setAlertType(AlertType.ERROR);
                    alert.setContentText("No results found for " + currentMedication);
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

            new Thread(task).start();
        }
    }


    /**
     * Generates a pop-up with a list of interactions.
     * @param event When the 'View interactions' button is clicked.
     */
    @FXML
    void viewInteractions(ActionEvent event) {

        // Check if there are two medications selected
        List<MedicationRecord> selectedItems = new ArrayList<>();
        selectedItems.addAll(currentMedicationsView.getSelectionModel().getSelectedItems());
        selectedItems.addAll(pastMedicationsView.getSelectionModel().getSelectedItems());

        if (selectedItems.size() != 2) {
            PageNavigator.showAlert(AlertType.ERROR, "Incorrect number of medications selected",
                    "Please select exactly two medications to view their interactions.");

        } else {
            Collections.sort(selectedItems); // get them into alphabetical order - the API appears to want this
            MedicationRecord medicationRecord1 = selectedItems.get(0);
            MedicationRecord medicationRecord2 = selectedItems.get(1);

            String medication1 = medicationRecord1.getMedicationName();
            String medication2 = medicationRecord2.getMedicationName();

            // Generate initial alert popup
            String alertTitle = "Interactions between " + medication1 + " and " + medication2;
            Alert alert = PageNavigator.generateAlert(AlertType.INFORMATION, alertTitle, "Loading...");
            alert.show();

            Task<List<String>> task = new Task<List<String>>() {
                @Override
                public List<String> call() {
                    // Call the drug interactions API wrapper, and wait for a response
                    List<String> interactions;
                    try {
                        interactions = drugInteractionsHandler.getInteractions(donor, medication1,
                                medication2);
                        interactions = new ArrayList<>(interactions); //make the list modifiable
                        interactions.add(0, SUCCESSFUL); //add successful tag to the start of the list
                    } catch (IllegalArgumentException e) {
                        // Invalid drug name(s)
                        interactions = Collections.singletonList(BAD_NAME);
                    } catch (BadGatewayException e) {
                        interactions = Collections.singletonList(BAD_GATEWAY);
                    } catch (IOException e) {
                        interactions = Collections.singletonList(IO_EXCEPTION);
                    }
                    return interactions;
                }
            };

            // Update the popup
            task.setOnSucceeded(e -> {
                List<String> interactions = task.getValue();

                if (interactions.size() > 1) {
                    interactions.remove(0); //remove the SUCCESSFUL tag
                    // Build list of interactions into a string, each interaction on a new line
                    StringBuilder sb = new StringBuilder();
                    for (String interaction : interactions) {
                        sb.append(interaction).append("\n");
                    }
                    alert.setContentText(sb.toString());
                } else if (interactions.get(0).equals(BAD_NAME)) {
                    // Invalid drug name(s)
                    alert.setAlertType(AlertType.ERROR);
                    alert.setContentText("Either " + medication1 + " or " + medication2 + " is not a valid drug name.");
                } else if (interactions.get(0).equals(BAD_GATEWAY)) {
                    alert.setAlertType(AlertType.ERROR);
                    alert.setContentText("Sorry, there was an error connecting to the server (502: Bad Gateway). "
                            + "Please try again later.");
                } else if (interactions.get(0).equals(IO_EXCEPTION)) {
                    alert.setAlertType(AlertType.ERROR);
                    alert.setContentText("Sorry, there was an error connecting to the server. Please try again later.");
                } else {
                    // only element in list is SUCCESSFUL tag
                    alert.setAlertType(AlertType.ERROR);
                    alert.setContentText("No results found for " + medication1 + " and " + medication2);
                }
                PageNavigator.resizeAlert(alert);
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
