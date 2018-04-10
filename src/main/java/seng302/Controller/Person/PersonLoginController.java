package seng302.Controller.Person;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

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
 * Controller for the login page.
 */
public class PersonLoginController extends SubController {

    @FXML
    private ListView<Person> personList;

    /**
     * Initializes the UI for this page.
     * - Gets the person manager from the current state.
     * - Sets up the cell factory to show users with their id and name.
     * - Adds all persons currently in the person manager to the person list.
     */
    @FXML
    private void initialize() {
        PersonManager personManager = State.getPersonManager();

        personList.setCellFactory(cell -> new ListCell<Person>() {
            @Override
            public void updateItem(Person item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("ID %d: %s %s", item.getUid(), item.getFirstName(), item.getLastName()));
                }
            }
        });
        personList.setItems(FXCollections.observableArrayList(personManager.getPeople()));
    }

    /**
     * Attempts to login with the selected person.
     * If successful, redirects to the view person page for that person.
     * @param event When the sign in button is clicked.
     */
    @FXML
    private void signIn(ActionEvent event) {
        Person selectedPerson = personList.getSelectionModel().getSelectedItem();

        if (selectedPerson != null) {
            HistoryItem loginHistory = new HistoryItem("LOGIN_PERSON", String.format("Person %s %s (%d) logged in.",
                    selectedPerson.getFirstName(), selectedPerson.getLastName(), selectedPerson.getUid()));
            JSONConverter.updateHistory(loginHistory, "action_history.json");

            State.login(Session.UserType.PERSON, selectedPerson);
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
