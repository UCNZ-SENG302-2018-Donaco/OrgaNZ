package seng302.Controller.Client;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.Client.MarkClientAsDeadAction;
import seng302.Actions.Client.ModifyClientAction;
import seng302.Client;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.HistoryItem;
import seng302.State.ClientManager;
import seng302.State.Session;
import seng302.State.Session.UserType;
import seng302.State.State;
import seng302.Utilities.Enums.BloodType;
import seng302.Utilities.Enums.Gender;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;

import org.controlsfx.control.Notifications;

/**
 * Controller for the view/edit client page.
 */
public class ViewClientController extends SubController {

    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy\nh:mm:ss a");

    private Session session;
    private ClientManager manager;
    private ActionInvoker invoker;
    private Client viewedClient;

    @FXML
    private Pane sidebarPane, idPane, inputsPane;
    @FXML
    private Label creationDate, lastModified, noClientLabel, fnameLabel, lnameLabel, dobLabel,
            dodLabel, heightLabel, weightLabel, ageDisplayLabel, ageLabel, BMILabel;
    @FXML
    private TextField id, fname, lname, mname, pname, height, weight, address;
    @FXML
    private DatePicker dob, dod;
    @FXML
    private ChoiceBox<Gender> gender, genderIdentity;
    @FXML
    private ChoiceBox<BloodType> btype;
    @FXML
    private ChoiceBox<Region> region;

    public ViewClientController() {
        manager = State.getClientManager();
        invoker = State.getInvoker();
        session = State.getSession();
    }

    /**
     * Initializes the UI for this page.
     * - Loads the sidebar.
     * - Adds all values to the gender, genderIdentity, blood type, and region dropdown lists.
     * - Disables all fields.
     * - If a client is logged in, populates with their info and removes ability to view a different client.
     * - If the viewUserId is set, populates with their info.
     */
    @FXML
    private void initialize() {
        gender.setItems(FXCollections.observableArrayList(Gender.values()));
        genderIdentity.setItems(FXCollections.observableArrayList(Gender.values()));
        btype.setItems(FXCollections.observableArrayList(BloodType.values()));
        region.setItems(FXCollections.observableArrayList(Region.values()));
        setFieldsDisabled(true);
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.loadSidebar(sidebarPane);

        if (session.getLoggedInUserType() == Session.UserType.CLIENT) {
            viewedClient = session.getLoggedInClient();
            idPane.setVisible(false);
            idPane.setManaged(false);
        } else if (windowContext.isClinViewClientWindow()) {
            viewedClient = windowContext.getViewClient();
        }
        id.setText(Integer.toString(viewedClient.getUid()));
        searchClient();
    }

    @Override
    public void refresh() {
        searchClient();
        if (session.getLoggedInUserType() == UserType.CLIENT) {
            mainController.setTitle("View Client: " + viewedClient.getPreferredName());
        } else if (windowContext.isClinViewClientWindow()) {
            mainController.setTitle("View Client: " + viewedClient.getFullName());
        }
    }

    /**
     * Searches for a client based off the id number supplied in the text field. The users fields will be displayed if
     * this user exists, otherwise an error message will display.
     */
    @FXML
    private void searchClient() {
        int id_value;
        try {
            id_value = Integer.parseInt(id.getText());
        } catch (Exception e) {
            noClientLabel.setVisible(true);
            setFieldsDisabled(true);
            return;
        }

        viewedClient = manager.getClientByID(id_value);
        if (viewedClient == null) {
            noClientLabel.setVisible(true);
            setFieldsDisabled(true);
        } else {
            noClientLabel.setVisible(false);
            setFieldsDisabled(false);

            fname.setText(viewedClient.getFirstName());
            lname.setText(viewedClient.getLastName());
            mname.setText(viewedClient.getMiddleName());
            pname.setText(viewedClient.getPreferredName());
            dob.setValue(viewedClient.getDateOfBirth());
            dod.setValue(viewedClient.getDateOfDeath());
            gender.setValue(viewedClient.getGender());
            genderIdentity.setValue(viewedClient.getGenderIdentity());
            height.setText(String.valueOf(viewedClient.getHeight()));
            weight.setText(String.valueOf(viewedClient.getWeight()));
            btype.setValue(viewedClient.getBloodType());
            region.setValue(viewedClient.getRegion());
            address.setText(viewedClient.getCurrentAddress());

            creationDate.setText(viewedClient.getCreatedTimestamp().format(dateTimeFormat));
            if (viewedClient.getModifiedTimestamp() == null) {
                lastModified.setText("User has not been modified yet.");
            } else {
                lastModified.setText(viewedClient.getModifiedTimestamp().format(dateTimeFormat));
            }

            HistoryItem save = new HistoryItem("SEARCH CLIENT",
                    "Client " + viewedClient.getFirstName() + " " + viewedClient.getLastName() + " (" + viewedClient
                            .getUid() + ") was searched");
            JSONConverter.updateHistory(save, "action_history.json");

            displayBMI();
            displayAge();
        }
    }

    /**
     * Disables the view of user fields as these will all be irrelevant to the id number supplied if no such client
     * exists with this id. Or sets it to visible so that the user can see all fields relevant to the client.
     * @param disabled the state of the pane.
     */
    private void setFieldsDisabled(boolean disabled) {
        inputsPane.setVisible(!disabled);
    }

    /**
     * Saves the changes a user makes to the viewed client if all their inputs are valid. Otherwise the invalid fields
     * text turns red.
     */
    @FXML
    private void saveChanges() {
        if (checkMandatoryFields() && checkNonMandatoryFields()) {
            updateChanges();
            displayBMI();
            displayAge();
            lastModified.setText(viewedClient.getModifiedTimestamp().format(dateTimeFormat));
            //TODO show what in particular was updated
            HistoryItem save = new HistoryItem("UPDATE CLIENT INFO",
                    "Updated changes to client " + viewedClient.getFirstName() + " " + viewedClient.getLastName()
                            + "updated client info: " + viewedClient.getClientInfoString());
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
        if (dod.getValue() == null ||
                (dod.getValue().isAfter(dob.getValue())) && dod.getValue().isBefore(LocalDate.now().plusDays(1))) {
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

    private void addChangeIfDifferent(ModifyClientAction action, String field, Object oldValue, Object newValue) {
        try {
            if (!Objects.equals(oldValue, newValue)) {
                action.addChange(field, oldValue, newValue);
            }
        } catch (NoSuchFieldException | NoSuchMethodException exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Records the changes updated as a ModifyClientAction to trace the change in record.
     */
    private void updateChanges() {
        ModifyClientAction action = new ModifyClientAction(viewedClient);

        addChangeIfDifferent(action, "setFirstName", viewedClient.getFirstName(), fname.getText());
        addChangeIfDifferent(action, "setLastName", viewedClient.getLastName(), lname.getText());
        addChangeIfDifferent(action, "setMiddleName", viewedClient.getMiddleName(), mname.getText());
        addChangeIfDifferent(action, "setPreferredName", viewedClient.getPreferredName(), pname.getText());
        addChangeIfDifferent(action, "setDateOfBirth", viewedClient.getDateOfBirth(), dob.getValue());
        addChangeIfDifferent(action, "setGender", viewedClient.getGender(), gender.getValue());
        addChangeIfDifferent(action, "setGenderIdentity", viewedClient.getGenderIdentity(), genderIdentity.getValue());
        addChangeIfDifferent(action, "setHeight", viewedClient.getHeight(), Double.parseDouble(height.getText()));
        addChangeIfDifferent(action, "setWeight", viewedClient.getWeight(), Double.parseDouble(weight.getText()));
        addChangeIfDifferent(action, "setBloodType", viewedClient.getBloodType(), btype.getValue());
        addChangeIfDifferent(action, "setRegion", viewedClient.getRegion(), region.getValue());
        addChangeIfDifferent(action, "setCurrentAddress", viewedClient.getCurrentAddress(), address.getText());

        try {
            String actionText = invoker.execute(action);

            Notifications.create()
                    .title("Updated Client")
                    .text(actionText)
                    .showInformation();
        } catch (IllegalStateException exc) {
            if (Objects.equals(viewedClient.getDateOfDeath(), dod.getValue())) {
                Notifications.create()
                        .title("No changes were made.")
                        .text("No changes were made to the client.")
                        .showWarning();
            }
        }

        if (viewedClient.getDateOfDeath() == null && dod.getValue() != null) {
            Optional<ButtonType> buttonOpt = PageNavigator.showAlert(AlertType.CONFIRMATION,
                    "Are you sure you want to mark this client as dead?",
                    "This will cancel all waiting transplant requests for this client.");

            if (buttonOpt.isPresent() && buttonOpt.get() == ButtonType.OK) {
                Action markDeadAction = new MarkClientAsDeadAction(viewedClient, dod.getValue());
                String actionText = invoker.execute(markDeadAction);

                Notifications.create()
                        .title("Marked Client as Dead")
                        .text(actionText)
                        .showConfirm();
            }
        }

        PageNavigator.refreshAllWindows();
    }

    /**
     * Displays the currently viewed clients BMI.
     */
    private void displayBMI() {
        if (viewedClient.getDateOfDeath() == null) {
            BMILabel.setText(String.format("%.01f", viewedClient.getBMI()));
        } else {
            BMILabel.setText(String.format("%.01f", viewedClient.getBMI()));
        }
    }

    /**
     * Displays either the current age, or age at death of the client depending on if the date of death field has been
     * filled in.
     */
    private void displayAge() {
        if (viewedClient.getDateOfDeath() == null) {
            ageDisplayLabel.setText("Age:");
        } else {
            ageDisplayLabel.setText("Age at death:");
        }
        ageLabel.setText(String.valueOf(viewedClient.getAge()));
    }

    /**
     * Navigate to the page to display organs for the currently specified client.
     */
    @FXML
    public void viewOrgansForClient() {
        PageNavigator.loadPage(Page.REGISTER_ORGAN_DONATIONS, mainController);
    }
}
