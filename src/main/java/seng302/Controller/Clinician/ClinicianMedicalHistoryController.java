package seng302.Controller.Clinician;

import java.time.LocalDate;
import java.util.ArrayList;
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

import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.Donor.AddIllnessRecord;
import seng302.Actions.Donor.DeleteIllnessRecord;
import seng302.Actions.Donor.ModifyIllnessRecordAction;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.Donor;
import seng302.IllnessRecord;
import seng302.State.Session;
import seng302.State.Session.UserType;
import seng302.State.State;

public class ClinicianMedicalHistoryController extends SubController{

  private Session session;
  private ActionInvoker invoker;
  private Donor donor;

  @FXML
  private TextField IllnessField;

  @FXML
  private Text errorMessage;

  @FXML
  private DatePicker dateDiagnosed;

  @FXML
  private CheckBox chronicBox;


  @FXML
  private Pane sidebarPane;

  @FXML
  private HBox newIllnessPane;

  @FXML
  private Button moveToHistoryButton, moveToCurrentButton, deleteButton,noLongerChronic,
      defaultFilter,alphabeticalFilter,diagnosisFilter;






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

    if (session.getLoggedInUserType() == UserType.DONOR) {
      donor = session.getLoggedInDonor();

      newIllnessPane.setVisible(false);
      newIllnessPane.setManaged(false);
      moveToHistoryButton.setDisable(true);
      moveToCurrentButton.setDisable(true);
      deleteButton.setDisable(true);
    } else if (windowContext.isClinViewDonorWindow()) {
      donor = windowContext.getViewDonor();
    }

    refreshIllnessLists();
  }

  /**
   * Sorts past/current illnesses ensures Chronic illnesses are at the top.
   */
  private List<IllnessRecord> sortCurrentIllnessList(){
    List<IllnessRecord> currentIllnesses = donor.getCurrentIllnesses();

    for (int j = 0; j < currentIllnesses.size(); j++){
      IllnessRecord item = currentIllnesses.get(j);
      if(item.getChronic()){
        currentIllnesses.remove(item);
        currentIllnesses.add(0,item);
      }

    }
    return currentIllnesses;
  }


  /**
   * Refreshes the past/current illness list views from the donor's properties.
   */
  private void refreshIllnessLists() {
    pastIllnessView.setItems(FXCollections.observableArrayList(donor.getPastIllnesses()));
    currentIllnessView.setItems(FXCollections.observableArrayList(sortCurrentIllnessList()));
  }


  /**
   * Moves Selected Illness to past Illnesses List Provided it is not chronic.
   *
   * @param event '<<' Button is pressed
   */
  @FXML
  private void moveIllnessToHistory(ActionEvent event){
    IllnessRecord record = currentIllnessView.getSelectionModel().getSelectedItem();
    if (record != null){
      if(!record.getChronic()){
        ModifyIllnessRecordAction action = new ModifyIllnessRecordAction(record);
        action.changeStopped(LocalDate.now());

        invoker.execute(action);
        refreshIllnessLists();

      }
    }
  }

  @FXML
  private void moveIllnessToCurrent(ActionEvent event){
    IllnessRecord record = pastIllnessView.getSelectionModel().getSelectedItem();
    if(record != null){
      ModifyIllnessRecordAction action = new ModifyIllnessRecordAction(record);
      action.changeStopped(null);
      invoker.execute(action);
      refreshIllnessLists();
    }
  }

  @FXML
  private void addButtonPressed(ActionEvent event) {

    addIllness(IllnessField.getText(),dateDiagnosed.getValue(),chronicBox.isSelected());
  }

  @FXML
  private void defaultFilterPressed(ActionEvent event){
    sortCurrentIllnessList();

  }

  @FXML
  private void alphabeticalFilterPressed(ActionEvent event){

    List<IllnessRecord> currentIllnesses = donor.getCurrentIllnesses();
    List<IllnessRecord> pastIllnesses = donor.getPastIllnesses();
    for (int j = 0; j < currentIllnesses.size(); j += 2){
      IllnessRecord item = currentIllnesses.get(j);
      IllnessRecord item2 = currentIllnesses.get(j+1);
      item.getIllnessName().compareToIgnoreCase(item2.getIllnessName());

    }



  }

  @FXML
  private void diagnosisFilterPressed(ActionEvent event){

  }

  @FXML
  private void deleteIllness(ActionEvent event) {
    if (selectedListView != null) {
      IllnessRecord record = selectedListView.getSelectionModel().getSelectedItem();
      if (record != null) {
        DeleteIllnessRecord action = new DeleteIllnessRecord(donor, record);

        invoker.execute(action);
        refreshIllnessLists();
      }
    }
  }





  @FXML
  private void removeChronicStatus(ActionEvent event){
      IllnessRecord record = currentIllnessView.getSelectionModel().getSelectedItem();
      if(record.getChronic()){
          ModifyIllnessRecordAction action = new ModifyIllnessRecordAction(record);
          action.changeChronicStatus(false);
          invoker.execute(action);
          record.toString();
          refreshIllnessLists();
      }

  }

  private void addIllness(String illnessName,LocalDate dateDiagnosed,Boolean isChronic){
    Boolean afterBirth = donor.getDateOfBirth().isBefore(dateDiagnosed);
    Boolean notInFuture = LocalDate.now().isAfter(dateDiagnosed);
    if(!illnessName.equals("")){
      if(!afterBirth){
        errorMessage.setText("Diagnosis date cannot be before person is born!");
        errorMessage.setOpacity(1);
      }

      if(!notInFuture){
        errorMessage.setText("Diagnosis date cannot be in the future!");
        errorMessage.setOpacity(1);
      }

      else{
        IllnessRecord record =  new IllnessRecord(illnessName,dateDiagnosed,null,
            isChronic);
        AddIllnessRecord action = new AddIllnessRecord(donor,record);

        invoker.execute(action);
        IllnessField.setText("");
        refreshIllnessLists();
      }


    } else{
      errorMessage.setText("Illness Name must be longer than 0 characters!");
      errorMessage.setOpacity(1);
    }
  }







}
