package seng302.Controller.Clinician;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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

    private Session session;
    private ActionInvoker invoker;
    private Client client;

    @FXML
    private TextField IllnessField;

    @FXML
    private Text errorMessage;

    @FXML
    private DatePicker dateDiagnosedPicker;

    @FXML
    private CheckBox chronicBox;


    @FXML
    private Pane sidebarPane;

    @FXML
    private HBox newIllnessPane;

    @FXML
    private Button moveToHistoryButton, moveToCurrentButton, deleteButton, noLongerChronic,
            defaultFilter, alphabeticalFilter, diagnosisFilter;


    @FXML
    private ListView<IllnessRecord> pastIllnessView, currentIllnessView;

    private ListView<IllnessRecord> selectedListView = null;

    public ClinicianMedicalHistoryController() {
        session = State.getSession();
        invoker = State.getInvoker();
    }


    @FXML
    public void initialize() {
        pastIllnessView.getSelectionModel().selectedItemProperty().addListener(
                (observable) -> {
                    selectedListView = pastIllnessView;
                    currentIllnessView.getSelectionModel().clearSelection();
                });

        currentIllnessView.getSelectionModel().selectedItemProperty().addListener(
                (observable) -> {
                    selectedListView = currentIllnessView;
                    pastIllnessView.getSelectionModel().clearSelection();
                });

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
        } else if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
        }

        refresh();
    }

    /**
     * Sorts past/current illnesses ensures Chronic illnesses are at the top.
     */
    private List<IllnessRecord> sortCurrentIllnessList() {
        List<IllnessRecord> currentIllnesses = client.getCurrentIllnesses();

        for (int j = 0; j < currentIllnesses.size(); j++) {
            IllnessRecord item = currentIllnesses.get(j);
            if (item.getChronic()) {
                currentIllnesses.remove(item);
                currentIllnesses.add(0, item);
            }

        }
        return currentIllnesses;
    }


    /**
     * Refreshes the past/current illness list views from the client's properties.
     */
    @Override
    public void refresh() {
        pastIllnessView.setItems(FXCollections.observableArrayList(client.getPastIllnesses()));
        currentIllnessView.setItems(FXCollections.observableArrayList(sortCurrentIllnessList()));
    }


    /**
     * Moves Selected Illness to past Illnesses List Provided it is not chronic.
     * @param event '<<' Button is pressed
     */
    @FXML
    private void moveIllnessToHistory(ActionEvent event) {
        IllnessRecord record = currentIllnessView.getSelectionModel().getSelectedItem();
        if (record != null) {
            if (!record.getChronic()) {
                ModifyIllnessRecordAction action = new ModifyIllnessRecordAction(record);
                action.changeCuredDate(LocalDate.now());

                invoker.execute(action);
                PageNavigator.refreshAllWindows();
            }
        }
    }

    @FXML
    private void moveIllnessToCurrent(ActionEvent event) {
        IllnessRecord record = pastIllnessView.getSelectionModel().getSelectedItem();
        if (record != null) {
            ModifyIllnessRecordAction action = new ModifyIllnessRecordAction(record);
            action.changeCuredDate(null);
            invoker.execute(action);
            PageNavigator.refreshAllWindows();
        }
    }

    @FXML
    private void addButtonPressed(ActionEvent event) {
        LocalDate dateValue = dateDiagnosedPicker.getValue();
        addIllness(IllnessField.getText(), dateValue, chronicBox.isSelected());
    }

    @FXML
    private void defaultFilterPressed(ActionEvent event) {
        sortCurrentIllnessList();

    }

    public List<IllnessRecord> seperateChronics(List<IllnessRecord> illnessRecords){
        List<IllnessRecord> chronics = new ArrayList<>();
        for (int i = 0; i < illnessRecords.size(); i++){
            if(illnessRecords.get(i).getChronic()){
                chronics.add(illnessRecords.get(i));
                illnessRecords.remove(illnessRecords.get(i));
            }

        }
        return chronics;
    }


    public void filterFunction(String filterType, boolean isInverted) {
        List<IllnessRecord> currentIllnesses = client.getCurrentIllnesses();
        List<IllnessRecord> pastIllnesses = client.getPastIllnesses();
        List<IllnessRecord> chronics = seperateChronics(currentIllnesses);
        if (filterType.equals("Alphabetical")) {
            chronics.sort(Comparator.comparing(IllnessRecord::getIllnessName));
            currentIllnesses.sort(Comparator.comparing(IllnessRecord::getIllnessName));
            pastIllnesses.sort(Comparator.comparing(IllnessRecord::getIllnessName));

            if(isInverted){
                Collections.reverse(chronics);
                Collections.reverse(currentIllnesses);
                Collections.reverse(pastIllnesses);
            }

            chronics.addAll(currentIllnesses);
        } else if (filterType.equals("Diagnosis Date")) {
            chronics.sort(Comparator.comparing(IllnessRecord::getDiagnosisDate));
            currentIllnesses.sort(Comparator.comparing(IllnessRecord::getDiagnosisDate));
            pastIllnesses.sort(Comparator.comparing(IllnessRecord::getDiagnosisDate));
            if(isInverted){
                Collections.reverse(chronics);
                Collections.reverse(currentIllnesses);
                Collections.reverse(pastIllnesses);
            }
            chronics.addAll(currentIllnesses);

        }

            currentIllnessView.setItems(FXCollections.observableArrayList(chronics));
            pastIllnessView.setItems(FXCollections.observableArrayList(pastIllnesses));
    }

    @FXML
    private void alphabeticalFilterPressed(ActionEvent event) {
        filterFunction("Alphabetical", false);

    }

    @FXML
    private void invertedAlphabeticalFilterPressed(ActionEvent event) {
        filterFunction("Alphabetical", true);

    }

    @FXML
    private void diagnosisFilterPressed(ActionEvent event) {
        filterFunction("Diagnosis Date", false);


    }

    @FXML
    private void invertedDiagnosisFilterPressed(ActionEvent event) {
        filterFunction("Diagnosis Date", true);

    }


    @FXML
    private void deleteIllness(ActionEvent event) {
        if (selectedListView != null) {
            IllnessRecord record = selectedListView.getSelectionModel().getSelectedItem();
            if (record != null) {
                DeleteIllnessRecord action = new DeleteIllnessRecord(client, record);

                invoker.execute(action);
                PageNavigator.refreshAllWindows();
            }
        }
    }


    @FXML
    private void removeChronicStatus(ActionEvent event) {
        IllnessRecord record = currentIllnessView.getSelectionModel().getSelectedItem();
        if (record.getChronic()) {
            ModifyIllnessRecordAction action = new ModifyIllnessRecordAction(record);
            action.changeChronicStatus(false);
            invoker.execute(action);
            PageNavigator.refreshAllWindows();
        }

    }

    private void addIllness(String illnessName, LocalDate dateDiagnosed, boolean isChronic) {
        boolean afterBirth = client.getDateOfBirth().isBefore(dateDiagnosed);
        boolean notInFuture = LocalDate.now().plus(1, ChronoUnit.DAYS).isAfter(dateDiagnosed);
        if (!illnessName.equals("")) {
            if (!afterBirth) {
                errorMessage.setText("Diagnosis date cannot be before person is born!");
                errorMessage.setOpacity(1);
            } else if (!notInFuture) {
                errorMessage.setText("Diagnosis date cannot be in the future!");
                errorMessage.setOpacity(1);
            } else {
                IllnessRecord record = new IllnessRecord(illnessName, dateDiagnosed, null, isChronic);
                AddIllnessRecord action = new AddIllnessRecord(client, record);
                invoker.execute(action);

                IllnessField.setText(null);
                errorMessage.setText(null);
                dateDiagnosedPicker.setValue(LocalDate.now());
                PageNavigator.refreshAllWindows();
            }

        } else {
            errorMessage.setText("Illness Name must be longer than 0 characters!");
            errorMessage.setOpacity(1);
        }
    }
}
