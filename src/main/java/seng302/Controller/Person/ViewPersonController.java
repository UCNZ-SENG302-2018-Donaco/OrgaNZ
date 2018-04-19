package seng302.Controller.Person;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import seng302.Actions.ActionInvoker;
import seng302.Actions.Person.ModifyPersonAction;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.Person;
import seng302.HistoryItem;
import seng302.State.PersonManager;
import seng302.State.Session;
import seng302.State.State;
import seng302.Utilities.Enums.BloodType;
import seng302.Utilities.Enums.Gender;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;

/**
 * Controller for the view/edit person page.
 */
public class ViewPersonController extends SubController {

    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy\nh:mm:ss a");

    private Session session;
    private PersonManager manager;
    private ActionInvoker invoker;
    private Person viewedPerson;

    @FXML
    private Pane sidebarPane, idPane, inputsPane;
    @FXML
    private Label creationDate, lastModified, noPersonLabel, fnameLabel, lnameLabel, dobLabel,
            dodLabel, heightLabel, weightLabel, ageDisplayLabel, ageLabel, BMILabel;
    @FXML
    private TextField id, fname, lname, mname, height, weight, address;
    @FXML
    private DatePicker dob, dod;
    @FXML
    private ChoiceBox<Gender> gender;
    @FXML
    private ChoiceBox<BloodType> btype;
    @FXML
    private ChoiceBox<Region> region;

    public ViewPersonController() {
        manager = State.getPersonManager();
        invoker = State.getInvoker();
        session = State.getSession();
    }

    /**
     * Initializes the UI for this page.
     * - Loads the sidebar.
     * - Adds all values to the gender and blood type dropdown lists.
     * - Disables all fields.
     * - If a person is logged in, populates with their info and removes ability to view a different person.
     * - If the viewUserId is set, populates with their info.
     */
    @FXML
    private void initialize() {
        gender.setItems(FXCollections.observableArrayList(Gender.values()));
        btype.setItems(FXCollections.observableArrayList(BloodType.values()));
        region.setItems(FXCollections.observableArrayList(Region.values()));
        setFieldsDisabled(true);
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.loadSidebar(sidebarPane);

        if (session.getLoggedInUserType() == Session.UserType.PERSON) {
            viewedPerson = session.getLoggedInPerson();
            idPane.setVisible(false);
            idPane.setManaged(false);
        } else if (windowContext.isClinViewPersonWindow()) {
            viewedPerson = windowContext.getViewPerson();
        }

        id.setText(Integer.toString(viewedPerson.getUid()));
        searchPerson();
    }

    /**
     * Searches for a person based off the id number supplied in the text field. The users fields will be displayed if
     * this user exists, otherwise an error message will display.
     */
    @FXML
    private void searchPerson() {
        int id_value;
        try {
            id_value = Integer.parseInt(id.getText());
        } catch (Exception e) {
            noPersonLabel.setVisible(true);
            setFieldsDisabled(true);
            return;
        }

        viewedPerson = manager.getPersonByID(id_value);
        if (viewedPerson == null) {
            noPersonLabel.setVisible(true);
            setFieldsDisabled(true);
        } else {
            noPersonLabel.setVisible(false);
            setFieldsDisabled(false);

            fname.setText(viewedPerson.getFirstName());
            lname.setText(viewedPerson.getLastName());
            mname.setText(viewedPerson.getMiddleName());
            dob.setValue(viewedPerson.getDateOfBirth());
            dod.setValue(viewedPerson.getDateOfDeath());
            gender.setValue(viewedPerson.getGender());
            height.setText(String.valueOf(viewedPerson.getHeight()));
            weight.setText(String.valueOf(viewedPerson.getWeight()));
            btype.setValue(viewedPerson.getBloodType());
            region.setValue(viewedPerson.getRegion());
            address.setText(viewedPerson.getCurrentAddress());

            creationDate.setText(viewedPerson.getCreatedTimestamp().format(dateTimeFormat));
            if (viewedPerson.getModifiedTimestamp() == null) {
                lastModified.setText("User has not been modified yet.");
            } else {
                lastModified.setText(viewedPerson.getModifiedTimestamp().format(dateTimeFormat));
            }

            HistoryItem save = new HistoryItem("SEARCH PERSON",
                    "Person " + viewedPerson.getFirstName() + " " + viewedPerson.getLastName() + " (" + viewedPerson
                            .getUid() + ") was searched");
            JSONConverter.updateHistory(save, "action_history.json");

            displayBMI();
            displayAge();
        }
    }

    /**
     * Disables the view of user fields as these will all be irrelevant to the id number supplied if no such person
     * exists with this id. Or sets it to visible so that the user can see all fields relevant to the person.
     * @param disabled the state of the pane.
     */
    private void setFieldsDisabled(boolean disabled) {
        inputsPane.setVisible(!disabled);
    }

    /**
     * Saves the changes a user makes to the viewed person if all their inputs are valid. Otherwise the invalid fields
     * text turns red.
     */
    @FXML
    private void saveChanges() {
        if (checkMandatoryFields() && checkNonMandatoryFields()) {
            updateChanges();
            displayBMI();
            displayAge();
            lastModified.setText(viewedPerson.getModifiedTimestamp().format(dateTimeFormat));
            //TODO show what in particular was updated
            HistoryItem save = new HistoryItem("UPDATE PERSON INFO",
                    "Updated changes to person " + viewedPerson.getFirstName() + " " + viewedPerson.getLastName()
                            + "updated person info: " + viewedPerson.getPersonInfoString());
            JSONConverter.updateHistory(save, "action_history.json");
        }
    }

    /**
     * Checks that all mandatory fields have valid arguments inside. Otherwise display red text on the invalidly entered
     * labels.
     * @return true if all mandatory fields have valid input.
     */
    private boolean checkMandatoryFields() {
        boolean update = true;
        if (fname.getText().equals("")) {
            fnameLabel.setTextFill(Color.RED);
            update = false;
        } else {
            fnameLabel.setTextFill(Color.BLACK);
        }

        if (lname.getText().equals("")) {
            lnameLabel.setTextFill(Color.RED);
            update = false;
        } else {
            lnameLabel.setTextFill(Color.BLACK);
        }
        if (dob.getValue() == null || dob.getValue().isAfter(LocalDate.now())) {
            dobLabel.setTextFill(Color.RED);
            update = false;
        } else {
            dobLabel.setTextFill(Color.BLACK);
        }
        return update;
    }


    /**
     * Checks that non mandatory fields have either valid input, or no input. Otherwise red text is shown.
     * @return true if all non mandatory fields have valid/no input.
     */
    private boolean checkNonMandatoryFields() {
        boolean update = true;
        if (dod.getValue() == null || dod.getValue().isBefore(LocalDate.now())) {
            dodLabel.setTextFill(Color.BLACK);
        } else {
            dodLabel.setTextFill(Color.RED);
            update = false;
        }

        try {
            double h = Double.parseDouble(height.getText());
            if (h < 0) {
                heightLabel.setTextFill(Color.RED);
                update = false;
            } else {
                heightLabel.setTextFill(Color.BLACK);
            }

        } catch (NumberFormatException ex) {
            heightLabel.setTextFill(Color.RED);
        }

        try {
            double w = Double.parseDouble(weight.getText());
            if (w < 0) {
                weightLabel.setTextFill(Color.RED);
                update = false;
            } else {
                weightLabel.setTextFill(Color.BLACK);
            }

        } catch (NumberFormatException ex) {
            weightLabel.setTextFill(Color.RED);
            update = false;
        }
        return update;
    }

    /**
     * Records the changes updated as a ModifyPersonAction to trace the change in record.
     */
    private void updateChanges() {
        try {
            ModifyPersonAction action = new ModifyPersonAction(viewedPerson);

            action.addChange("setFirstName", viewedPerson.getFirstName(), fname.getText());
            action.addChange("setLastName", viewedPerson.getLastName(), lname.getText());
            action.addChange("setMiddleName", viewedPerson.getMiddleName(), mname.getText());
            action.addChange("setDateOfBirth", viewedPerson.getDateOfBirth(), dob.getValue());
            action.addChange("setDateOfDeath", viewedPerson.getDateOfDeath(), dod.getValue());
            action.addChange("setGender", viewedPerson.getGender(), gender.getValue());
            action.addChange("setHeight", viewedPerson.getHeight(), Double.parseDouble(height.getText()));
            action.addChange("setWeight", viewedPerson.getWeight(), Double.parseDouble(weight.getText()));
            action.addChange("setBloodType", viewedPerson.getBloodType(), btype.getValue());
            action.addChange("setRegion", viewedPerson.getRegion(), region.getValue());
            action.addChange("setCurrentAddress", viewedPerson.getCurrentAddress(), address.getText());

            invoker.execute(action);

            PageNavigator.showAlert(Alert.AlertType.INFORMATION,
                    "Success",
                    String.format("Successfully updated person %s %s %s %d.",
                            viewedPerson.getFirstName(), viewedPerson.getMiddleName(),
                            viewedPerson.getLastName(), viewedPerson.getUid()));

        } catch (NoSuchFieldException | NoSuchMethodException exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Displays the currently viewed persons BMI.
     */
    private void displayBMI() {
        if (viewedPerson.getDateOfDeath() == null) {
            BMILabel.setText(String.format("%.01f", viewedPerson.getBMI()));
        } else {
            BMILabel.setText(String.format("%.01f", viewedPerson.getBMI()));
        }
    }

    /**
     * Displays either the current age, or age at death of the person depending on if the date of death field has been
     * filled in.
     */
    private void displayAge() {
        if (viewedPerson.getDateOfDeath() == null) {
            ageDisplayLabel.setText("Age");
        } else {
            ageDisplayLabel.setText("Age at Death");
        }
        ageLabel.setText(String.valueOf(viewedPerson.getAge()));
    }

    /**
     * Navigate to the page to display organs for the currently specified person.
     */
    @FXML
    public void viewOrgansForPerson() {
        PageNavigator.loadPage(Page.REGISTER_ORGAN_DONATIONS, mainController);
    }
}
