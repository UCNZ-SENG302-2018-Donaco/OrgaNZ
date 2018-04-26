package seng302.Controller.Donor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import seng302.Actions.ActionInvoker;
import seng302.Actions.Donor.ModifyDonorAction;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.Donor;
import seng302.HistoryItem;
import seng302.State.DonorManager;
import seng302.State.Session;
import seng302.State.State;
import seng302.Utilities.Enums.BloodType;
import seng302.Utilities.Enums.Gender;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;

import org.controlsfx.control.Notifications;

/**
 * Controller for the view/edit donor page.
 */
public class ViewDonorController extends SubController {

    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy\nh:mm:ss a");

    private Session session;
    private DonorManager manager;
    private ActionInvoker invoker;
    private Donor viewedDonor;

    @FXML
    private Pane sidebarPane, idPane, inputsPane;
    @FXML
    private Label creationDate, lastModified, noDonorLabel, fnameLabel, lnameLabel, dobLabel,
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

    public ViewDonorController() {
        manager = State.getDonorManager();
        invoker = State.getInvoker();
        session = State.getSession();
    }

    /**
     * Initializes the UI for this page.
     * - Loads the sidebar.
     * - Adds all values to the gender and blood type dropdown lists.
     * - Disables all fields.
     * - If a donor is logged in, populates with their info and removes ability to view a different donor.
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

        if (session.getLoggedInUserType() == Session.UserType.DONOR) {
            viewedDonor = session.getLoggedInDonor();
            idPane.setVisible(false);
            idPane.setManaged(false);
        } else if (windowContext.isClinViewDonorWindow()) {
            viewedDonor = windowContext.getViewDonor();
        }

        mainController.setTitle("Donor profile: " + viewedDonor.getFullName());
        id.setText(Integer.toString(viewedDonor.getUid()));
        searchDonor();
    }

    @Override
    public void refresh() {
        searchDonor();
    }

    /**
     * Searches for a donor based off the id number supplied in the text field. The users fields will be displayed if
     * this user exists, otherwise an error message will display.
     */
    @FXML
    private void searchDonor() {
        int id_value;
        try {
            id_value = Integer.parseInt(id.getText());
        } catch (Exception e) {
            noDonorLabel.setVisible(true);
            setFieldsDisabled(true);
            return;
        }

        viewedDonor = manager.getDonorByID(id_value);
        if (viewedDonor == null) {
            noDonorLabel.setVisible(true);
            setFieldsDisabled(true);
        } else {
            noDonorLabel.setVisible(false);
            setFieldsDisabled(false);

            fname.setText(viewedDonor.getFirstName());
            lname.setText(viewedDonor.getLastName());
            mname.setText(viewedDonor.getMiddleName());
            dob.setValue(viewedDonor.getDateOfBirth());
            dod.setValue(viewedDonor.getDateOfDeath());
            gender.setValue(viewedDonor.getGender());
            height.setText(String.valueOf(viewedDonor.getHeight()));
            weight.setText(String.valueOf(viewedDonor.getWeight()));
            btype.setValue(viewedDonor.getBloodType());
            region.setValue(viewedDonor.getRegion());
            address.setText(viewedDonor.getCurrentAddress());

            creationDate.setText(viewedDonor.getCreatedTimestamp().format(dateTimeFormat));
            if (viewedDonor.getModifiedTimestamp() == null) {
                lastModified.setText("User has not been modified yet.");
            } else {
                lastModified.setText(viewedDonor.getModifiedTimestamp().format(dateTimeFormat));
            }

            HistoryItem save = new HistoryItem("SEARCH DONOR",
                    "Donor " + viewedDonor.getFirstName() + " " + viewedDonor.getLastName() + " (" + viewedDonor
                            .getUid() + ") was searched");
            JSONConverter.updateHistory(save, "action_history.json");

            displayBMI();
            displayAge();
        }
    }

    /**
     * Disables the view of user fields as these will all be irrelevant to the id number supplied if no such donor
     * exists with this id. Or sets it to visible so that the user can see all fields relevant to the donor.
     * @param disabled the state of the pane.
     */
    private void setFieldsDisabled(boolean disabled) {
        inputsPane.setVisible(!disabled);
    }

    /**
     * Saves the changes a user makes to the viewed donor if all their inputs are valid. Otherwise the invalid fields
     * text turns red.
     */
    @FXML
    private void saveChanges() {
        if (checkMandatoryFields() && checkNonMandatoryFields()) {
            updateChanges();
            displayBMI();
            displayAge();
            lastModified.setText(viewedDonor.getModifiedTimestamp().format(dateTimeFormat));
            //TODO show what in particular was updated
            HistoryItem save = new HistoryItem("UPDATE DONOR INFO",
                    "Updated changes to donor " + viewedDonor.getFirstName() + " " + viewedDonor.getLastName()
                            + "updated donor info: " + viewedDonor.getDonorInfoString());
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

    private void addChangeIfDifferent(ModifyDonorAction action, String field, Object oldValue, Object newValue) {
        try {
            if (!Objects.equals(oldValue, newValue)) {
                action.addChange(field, oldValue, newValue);
            }
        } catch (NoSuchFieldException | NoSuchMethodException exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Records the changes updated as a ModifyDonorAction to trace the change in record.
     */
    private void updateChanges() {
        ModifyDonorAction action = new ModifyDonorAction(viewedDonor);

        addChangeIfDifferent(action, "setFirstName", viewedDonor.getFirstName(), fname.getText());
        addChangeIfDifferent(action, "setLastName", viewedDonor.getLastName(), lname.getText());
        addChangeIfDifferent(action, "setMiddleName", viewedDonor.getMiddleName(), mname.getText());
        addChangeIfDifferent(action, "setDateOfBirth", viewedDonor.getDateOfBirth(), dob.getValue());
        addChangeIfDifferent(action, "setDateOfDeath", viewedDonor.getDateOfDeath(), dod.getValue());
        addChangeIfDifferent(action, "setGender", viewedDonor.getGender(), gender.getValue());
        addChangeIfDifferent(action, "setHeight", viewedDonor.getHeight(), Double.parseDouble(height.getText()));
        addChangeIfDifferent(action, "setWeight", viewedDonor.getWeight(), Double.parseDouble(weight.getText()));
        addChangeIfDifferent(action, "setBloodType", viewedDonor.getBloodType(), btype.getValue());
        addChangeIfDifferent(action, "setRegion", viewedDonor.getRegion(), region.getValue());
        addChangeIfDifferent(action, "setCurrentAddress", viewedDonor.getCurrentAddress(), address.getText());

        String actionText = invoker.execute(action);
        PageNavigator.refreshAllWindows();

        Notifications.create()
                .title("Updated Donor")
                .text(actionText)
                .showInformation();
    }

    /**
     * Displays the currently viewed donors BMI.
     */
    private void displayBMI() {
        if (viewedDonor.getDateOfDeath() == null) {
            BMILabel.setText(String.format("%.01f", viewedDonor.getBMI()));
        } else {
            BMILabel.setText(String.format("%.01f", viewedDonor.getBMI()));
        }
    }

    /**
     * Displays either the current age, or age at death of the donor depending on if the date of death field has been
     * filled in.
     */
    private void displayAge() {
        if (viewedDonor.getDateOfDeath() == null) {
            ageDisplayLabel.setText("Age");
        } else {
            ageDisplayLabel.setText("Age at Death");
        }
        ageLabel.setText(String.valueOf(viewedDonor.getAge()));
    }

    /**
     * Navigate to the page to display organs for the currently specified donor.
     */
    @FXML
    public void viewOrgansForDonor() {
        PageNavigator.loadPage(Page.REGISTER_ORGANS, mainController);
    }
}
