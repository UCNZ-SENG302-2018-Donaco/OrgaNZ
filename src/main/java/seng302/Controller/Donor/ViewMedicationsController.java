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

    private void refreshMedicationLists() {
        pastMedicationsView.setItems(FXCollections.observableArrayList(donor.getPastMedications()));
        currentMedicationsView.setItems(FXCollections.observableArrayList(donor.getCurrentMedications()));
    }

    @FXML
    private void moveMedicationToHistory(ActionEvent actionEvent) {
        MedicationHistoryItem item = currentMedicationsView.getSelectionModel().getSelectedItem();
        if (item != null) {
            item.setStopped(LocalDate.now());
            donor.getCurrentMedications().remove(item);
            donor.getPastMedications().add(item);
            refreshMedicationLists();
        }
    }

    @FXML
    private void moveMedicationToCurrent(ActionEvent actionEvent) {
        MedicationHistoryItem item = pastMedicationsView.getSelectionModel().getSelectedItem();
        if (item != null) {
            item.setStarted(LocalDate.now());
            item.setStopped(null);
            donor.getPastMedications().remove(item);
            donor.getCurrentMedications().add(item);
            refreshMedicationLists();
        }
    }

    @FXML
    public void newMedKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            addMedication(newMedField.getText());
        }
    }

    @FXML
    private void addButtonPressed(ActionEvent event) {
        addMedication(newMedField.getText());
    }

    private void addMedication(String newMedName) {
        donor.getCurrentMedications().add(new MedicationHistoryItem(newMedName, LocalDate.now(), null));
        newMedField.setText("");
        refreshMedicationLists();
    }

    @FXML
    private void deleteMedication(ActionEvent event) {
        MedicationHistoryItem item = selectedListView.getSelectionModel().getSelectedItem();
        if (selectedListView == pastMedicationsView) {
            donor.getPastMedications().remove(item);
        } else if (selectedListView == currentMedicationsView) {
            donor.getCurrentMedications().remove(item);
        }
        refreshMedicationLists();
    }

    private List<String> getSuggestions(String input) {
        return new ArrayList<>();
    }
}
