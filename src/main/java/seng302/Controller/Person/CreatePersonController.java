package seng302.Controller.Person;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.Person.CreatePersonAction;
import seng302.Controller.SubController;
import seng302.Person;
import seng302.HistoryItem;
import seng302.State.PersonManager;
import seng302.State.Session;
import seng302.State.State;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;

/**
 * Controller for the create person page.
 */
public class CreatePersonController extends SubController {

    @FXML
    private DatePicker dobFld;
    @FXML
    private TextField firstNameFld, middleNamefld, lastNamefld;

    private PersonManager manager;
    private ActionInvoker invoker;

    /**
     * Initializes the UI for this page.
     * - Gets the PersonManager and ActionInvoker from the current state.
     */
    public CreatePersonController() {
        manager = State.getPersonManager();
        invoker = State.getInvoker();
    }

    /**
     * Creates a new person based on the information supplied in the fields.
     * Shows appropriate alerts if the information is invalid, or if the person already exists.
     * Shows an alert if successful, then redirects to the view page for the new person.
     * @param event When the create button is clicked.
     */
    @FXML
    private void createPerson(ActionEvent event) {
        if (firstNameFld.getText().equals("") || lastNamefld.getText().equals("") || dobFld.getValue() == null) {
            PageNavigator.showAlert(AlertType.ERROR, "Required Field Empty",
                    "Please make sure that all the required fields are given.");
        } else {
            //Duplicate user warning alert
            if (manager.collisionExists(firstNameFld.getText(), lastNamefld.getText(), dobFld.getValue())) {
                System.out.println("Duplicate");
                ButtonType option = PageNavigator.showAlert(AlertType.CONFIRMATION,
                        "Duplicate Person Warning",
                        "This person is a duplicate of one that already exists. Would you still like to create it?")
                        .get();
                if (option != ButtonType.OK) {
                    // ... user chose CANCEL or closed the dialog
                    return;
                }
            }

            int uid = manager.getUid();
            Person person = new Person(firstNameFld.getText(), middleNamefld.getText(), lastNamefld.getText(),
                    dobFld.getValue(), uid);
            Action action = new CreatePersonAction(person, manager);
            invoker.execute(action);
            HistoryItem save = new HistoryItem("CREATE PERSON",
                    "Person " + firstNameFld.getText() + " " + lastNamefld.getText() + "was created with ID " + uid);
            JSONConverter.updateHistory(save, "action_history.json");

            PageNavigator.showAlert(AlertType.INFORMATION,
                    "Success",
                    String.format("Successfully created person %s %s %s with ID %d.",
                            person.getFirstName(), person.getMiddleName(), person.getLastName(), uid));

            State.login(Session.UserType.PERSON, person);
            PageNavigator.loadPage(Page.VIEW_PERSON, mainController);
        }
    }

    /**
     * Redirects the UI back to the landing page.
     * @param event When the back button is clicked.
     */
    @FXML
    private void goBack(ActionEvent event) {
        PageNavigator.loadPage(Page.LANDING, mainController);
    }
}
