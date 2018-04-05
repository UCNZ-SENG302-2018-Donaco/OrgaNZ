package seng302.Controller.Donor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.Donor;
import seng302.MedicationHistoryItem;
import seng302.State.Session;
import seng302.State.Session.UserType;
import seng302.State.State;

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;

/**
 * Controller for the view/edit medications page.
 */
public class ViewMedicationsController extends SubController {

    private Session session;
    private Donor donor;

    @FXML
    private Pane sidebarPane;

    @FXML
    private TextField newMedField;

    @FXML
    private HBox newMedicationPane;

    @FXML
    private Button moveToHistoryButton, moveToCurrentButton, deleteButton;

    @FXML
    private ListView<MedicationHistoryItem> pastMedicationsView, currentMedicationsView;

    private ListView<MedicationHistoryItem> selectedListView = null;

    public ViewMedicationsController() {
        session = State.getSession();
    }

    /**
     * Initializes the UI for this page.
     * - Sets listeners for changing selection on two list views so that if an item is selected on one, the selection
     * is removed from the other.
     * - Checks if the logged in user is a donor, and if so, makes the page non-editable.
     */
    @FXML
    private void initialize() {
        new AutoCompletionTextFieldBinding<>(newMedField, param -> getSuggestions(newMedField.getText()));

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

        if (session.getLoggedInUserType() == UserType.DONOR) {
            newMedicationPane.setVisible(false);
            newMedicationPane.setManaged(false);

            moveToHistoryButton.setDisable(true);
            moveToCurrentButton.setDisable(true);
            deleteButton.setDisable(true);
        }
    }

    /**
     * Sets up the page using the MainController given.
     * - Loads the sidebar.
     * - Checks if the session login type is a donor or a clinician, and sets the viewed donor appropriately.
     * - Refreshes the medication list views to set initial state based on the viewed donor.
     * @param mainController The MainController for the window this page is loaded on.
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.loadSidebar(sidebarPane);

        if (session.getLoggedInUserType() == Session.UserType.DONOR) {
            donor = session.getLoggedInDonor();
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
     * Moves the MedicationHistoryItem selected in the current medications list to the past medications list. Also:
     * - Sets the date the donor stopped taking the medication to the current date.
     * - Removes the MedicationHistoryItem from the current medications list.
     * - Refreshes both list views.
     * @param event When the '<' button is pressed.
     */
    @FXML
    private void moveMedicationToHistory(ActionEvent event) {
        MedicationHistoryItem item = currentMedicationsView.getSelectionModel().getSelectedItem();
        if (item != null) {
            item.setStopped(LocalDate.now());
            donor.getCurrentMedications().remove(item);
            donor.getPastMedications().add(item);
            refreshMedicationLists();
        }
    }

    /**
     * Moves the MedicationHistoryItem selected in the past medications list to the current medications list. Also:
     * - Sets the date the donor started taking the medication to the current date.
     * - Sets the date the donor stopped taking the medication to null (hasn't stopped yet).
     * - Removes the MedicationHistoryItem from the past medications list.
     * - Refreshes both list views.
     * @param event When the '>' button is pressed.
     */
    @FXML
    private void moveMedicationToCurrent(ActionEvent event) {
        MedicationHistoryItem item = pastMedicationsView.getSelectionModel().getSelectedItem();
        if (item != null) {
            item.setStarted(LocalDate.now());
            item.setStopped(null);
            donor.getPastMedications().remove(item);
            donor.getCurrentMedications().add(item);
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
     * Creates a new MedicationHistoryItem for a medication with the given name, sets its 'started' date to the
     * current date, then adds it to the donor's current medications list.
     * @param newMedName The name of the medication to add a new instance of.
     */
    private void addMedication(String newMedName) {
        donor.getCurrentMedications().add(new MedicationHistoryItem(newMedName, LocalDate.now(), null));
        newMedField.setText("");
        refreshMedicationLists();
    }

    /**
     * Deletes the currently selected MedicationHistoryItem. Will determine which of the list views is currently
     * selected, then delete from the appropriate one. If neither list view is currently selected, this will have no
     * effect.
     * @param event When the 'delete' button is clicked.
     */
    @FXML
    private void deleteMedication(ActionEvent event) {
        if (selectedListView != null) {
            MedicationHistoryItem item = selectedListView.getSelectionModel().getSelectedItem();
            if (item != null) {
                if (selectedListView == pastMedicationsView) {
                    donor.getPastMedications().remove(item);
                } else if (selectedListView == currentMedicationsView) {
                    donor.getCurrentMedications().remove(item);
                }
                refreshMedicationLists();
            }
        }
    }

    private List<String> getSuggestions(String input) {
        return new ArrayList<>();
    }
}
