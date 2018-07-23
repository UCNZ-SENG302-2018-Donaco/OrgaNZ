package com.humanharvest.organz.controller.client;

import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import com.humanharvest.organz.AppUI;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.clinician.ViewBaseController;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.ui.validation.UIValidation;
import com.humanharvest.organz.utilities.JSONConverter;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.validators.IntValidator;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.views.client.ModifyClientObject;
import org.controlsfx.control.Notifications;

/**
 * Controller for the view/edit client page.
 */
public class ViewClientController extends ViewBaseController {

    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy\nh:mm:ss a");
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withZone(ZoneId.systemDefault());

    private static final Logger LOGGER = Logger.getLogger(ViewClientController.class.getName());
    private static final int maxFileSize = 2000000; // (2mb)
    private final Session session;
    private final ClientManager manager;
    private Client viewedClient;

    @FXML
    private Pane sidebarPane;
    @FXML
    private Pane imagePane;
    @FXML
    private Pane inputsPane;
    @FXML
    private Pane menuBarPane;
    @FXML
    private Label creationDate;
    @FXML
    private Label lastModified;
    @FXML
    private Label fnameLabel;
    @FXML
    private Label lnameLabel;
    @FXML
    private Label dobLabel;
    @FXML
    private Label dodLabel;
    @FXML
    private Label heightLabel;
    @FXML
    private Label weightLabel;
    @FXML
    private Label ageDisplayLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label bmiLabel;
    @FXML
    private Label fullName;
    @FXML
    private TextField fname;
    @FXML
    private TextField lname;
    @FXML
    private TextField mname;
    @FXML
    private TextField pname;
    @FXML
    private TextField height;
    @FXML
    private TextField weight;
    @FXML
    private TextField address;
    @FXML
    private Button uploadPhotoButton;
    @FXML
    private Button deletePhotoButton;
    @FXML
    private DatePicker dob;
    @FXML
    private DatePicker dod;
    @FXML
    private ChoiceBox<Gender> gender;
    @FXML
    private ChoiceBox<Gender> genderIdentity;
    @FXML
    private ChoiceBox<BloodType> btype;
    @FXML
    private ChoiceBox<Region> region;
    @FXML
    private ImageView imageView;
    private Image image;


    public ViewClientController() {
        manager = State.getClientManager();
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
        fullName.setWrapText(true);
    }


    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        if (session.getLoggedInUserType() == Session.UserType.CLIENT) {
            viewedClient = session.getLoggedInClient();
            mainController.loadSidebar(sidebarPane);
        } else if (windowContext.isClinViewClientWindow()) {
            viewedClient = windowContext.getViewClient();
            mainController.loadMenuBar(menuBarPane);
        }
        loadImage();
        refresh();
    }

    @Override
    public void refresh() {
        try {
            viewedClient = manager.getClientByID(viewedClient.getUid()).orElseThrow(ServerRestException::new);
        } catch (ServerRestException e) {
            e.printStackTrace();
            PageNavigator.showAlert(AlertType.ERROR,
                    "Server Error",
                    "An error occurred while trying to fetch from the server.\nPlease try again later.");
        }
        updateClientFields();
        if (session.getLoggedInUserType() == UserType.CLIENT) {
            mainController.setTitle("View Client: " + viewedClient.getPreferredName());
        } else if (windowContext.isClinViewClientWindow()) {
            mainController.setTitle("View Client: " + viewedClient.getFullName());
        }
    }

    /**
     * Updates all of the fields of the client.
     */
    private void updateClientFields() {
        fname.setText(viewedClient.getFirstName());
        lname.setText(viewedClient.getLastName());
        mname.setText(viewedClient.getMiddleName());
        pname.setText(viewedClient.getPreferredNameOnly());
        dob.setValue(viewedClient.getDateOfBirth());
        dod.setValue(viewedClient.getDateOfDeath());
        gender.setValue(viewedClient.getGender());
        genderIdentity.setValue(viewedClient.getGenderIdentity());
        height.setText(String.valueOf(viewedClient.getHeight()));
        weight.setText(String.valueOf(viewedClient.getWeight()));
        btype.setValue(viewedClient.getBloodType());
        region.setValue(viewedClient.getRegion());
        address.setText(viewedClient.getCurrentAddress());
        fullName.setText(viewedClient.getFullName());


        creationDate.setText(formatter.format(viewedClient.getCreatedTimestamp()));

//        creationDate.setText(viewedClient.getCreatedTimestamp().format(dateFormat));
//        creationDate.setTooltip(new Tooltip(viewedClient.getCreatedTimestamp().format(dateTimeFormat)));
        if (viewedClient.getModifiedTimestamp() == null) {
            lastModified.setText("Not yet modified.");
        } else {
//            lastModified.setText(viewedClient.getModifiedTimestamp().format(dateFormat));
//            lastModified.setTooltip(new Tooltip(viewedClient.getModifiedTimestamp().format(dateTimeFormat)));
            lastModified.setText(formatter.format(viewedClient.getModifiedTimestamp()));
        }

        displayBMI();
        displayAge();

    }


    /**
     * Saves the changes a user makes to the viewed client if all their inputs are valid.
     * Otherwise the invalid fields text turns red.
     */
    @FXML
    private void apply() {
        if (checkMandatoryFields() && checkNonMandatoryFields()) {
            if (updateChanges()) {
                displayBMI();
                displayAge();

                lastModified.setText(formatter.format(viewedClient.getModifiedTimestamp()));
            }
        }
    }

    /**
     * Loads the viewed profiles image
     */
    private void loadImage() {
        if (image == null) {
            image = new Image("https://cdn4.iconfinder.com/data/icons/standard-free-icons/139/Profile01-512.png"); // Make this a local image
            deletePhotoButton.setDisable(true);
        } else {
            deletePhotoButton.setDisable(false);
        }

        // GET Image from DB
        // if image exists
        // image = existing image

        imageView.setImage(image);
        imageView.setFitHeight(130);
        imageView.setFitWidth(130);
        imageView.setPreserveRatio(true);
    }

    /**
     * Prompts a user with a file chooser which is restricted to png's and jpg's. If a valid file of correct size is
     * input, this photo is uploaded as the viewed clients new profile photo.
     */
    @FXML
    public void uploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Image Files", "*.png", "*.jpg") // Restricting only these file types.
        );

        File selectedFile = fileChooser.showOpenDialog(AppUI.getWindow());
        if (selectedFile != null) {
            if (selectedFile.length() > maxFileSize) {
                PageNavigator.showAlert(AlertType.WARNING, "Image Size Too Large",
                        "The image size is too large. It must be under 2MB.");
            } else if (!selectedFile.canRead()) {
                PageNavigator.showAlert(AlertType.WARNING, "File Couldn't Be Read",
                        "This file could not be read. Ensure you are uploading a valid .png or .jpg");
            } else {
                image = new Image(selectedFile.toURI().toString());
                loadImage();
            }
        }
    }

    /**
     * Sets the profile image to null and refreshes the image.
     */
    @FXML
    public void deletePhoto() {
        image = null;
        loadImage(); //Make delete API call
    }

    /**
     * Resets the page back to its default state.
     */
    @FXML
    private void cancel() {
        refresh();
    }

    /**
     * Checks that all mandatory fields have valid arguments inside. Otherwise display red text on the invalidly entered
     * labels.
     * @return true if all mandatory fields have valid input.
     */
    private boolean checkMandatoryFields() {
        boolean update = true;
        if (fname.getText().isEmpty()) {
            fnameLabel.setTextFill(Color.RED);
            update = false;
        } else {
            fnameLabel.setTextFill(Color.BLACK);
        }

        if (lname.getText().isEmpty()) {
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


    private void addChangeIfDifferent(ModifyClientObject modifyClientObject, String fieldString, Object newValue) {
        try {
            //Get the field from the string
            Field field = modifyClientObject.getClass().getDeclaredField(fieldString);
            Field clientField = viewedClient.getClass().getDeclaredField(fieldString);
            //Allow access to any fields including private
            field.setAccessible(true);
            clientField.setAccessible(true);
            //Only add the field if it differs from the client
            if (!Objects.equals(clientField.get(viewedClient), newValue)) {
                field.set(modifyClientObject, newValue);
                modifyClientObject.registerChange(fieldString);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Records the changes updated as a ModifyClientAction to trace the change in record.
     * @return If there were any changes made
     */
    private boolean updateChanges() {
        ModifyClientObject modifyClientObject = new ModifyClientObject();

        boolean clientDied = false;

        if (viewedClient.getDateOfDeath() == null && dod.getValue() != null) {
            Optional<ButtonType> buttonOpt = PageNavigator.showAlert(AlertType.CONFIRMATION,
                    "Are you sure you want to mark this client as dead?",
                    "This will cancel all waiting transplant requests for this client.");

            if (buttonOpt.isPresent() && buttonOpt.get() == ButtonType.OK) {


                Client updatedClient;
                try {
                    updatedClient = State.getClientResolver().markClientAsDead(viewedClient, dod.getValue());
                } catch (NotFoundException e) {
                    LOGGER.log(Level.WARNING, "Client not found");
                    PageNavigator.showAlert(
                            AlertType.WARNING,
                            "Client not found",
                            "The client could not be found on the server, it may have been deleted");
                    return false;
                } catch (ServerRestException e) {
                    LOGGER.log(Level.WARNING, e.getMessage(), e);
                    PageNavigator.showAlert(
                            AlertType.WARNING,
                            "Server error",
                            "Could not apply changes on the server, please try again later");
                    return false;
                } catch (IfMatchFailedException e) {
                    LOGGER.log(Level.INFO, "If-Match did not match");
                    PageNavigator.showAlert(
                            AlertType.WARNING,
                            "Outdated Data",
                            "The client has been modified since you retrieved the data.\n"
                                    + "If you would still like to apply these changes please submit again, "
                                    + "otherwise refresh the page to update the data.");
                    return false;
                }
                clientDied = true;
                viewedClient.setTransplantRequests(updatedClient.getTransplantRequests());


                Notifications.create()
                        .title("Marked Client as Dead")
                        .text("All organ transplant requests have been removed")
                        .showConfirm();
            }
        }

        addChangeIfDifferent(modifyClientObject, viewedClient, "firstName", fname.getText());
        addChangeIfDifferent(modifyClientObject, viewedClient, "lastName", lname.getText());
        addChangeIfDifferent(modifyClientObject, viewedClient, "middleName", mname.getText());
        addChangeIfDifferent(modifyClientObject, viewedClient, "preferredName", pname.getText());
        addChangeIfDifferent(modifyClientObject, viewedClient, "dateOfBirth", dob.getValue());
        addChangeIfDifferent(modifyClientObject, viewedClient, "dateOfDeath", dod.getValue());
        addChangeIfDifferent(modifyClientObject, viewedClient, "gender", gender.getValue());
        addChangeIfDifferent(modifyClientObject, viewedClient, "genderIdentity", genderIdentity.getValue());
        addChangeIfDifferent(modifyClientObject, viewedClient, "height", Double.parseDouble(height.getText()));
        addChangeIfDifferent(modifyClientObject, viewedClient, "weight", Double.parseDouble(weight.getText()));
        addChangeIfDifferent(modifyClientObject, viewedClient, "bloodType", btype.getValue());
        addChangeIfDifferent(modifyClientObject, viewedClient, "region", region.getValue());
        addChangeIfDifferent(modifyClientObject, viewedClient, "currentAddress", address.getText());

        if (modifyClientObject.getModifiedFields().isEmpty()) {
            if (!clientDied) {
                Notifications.create()
                        .title("No changes were made.")
                        .text("No changes were made to the client.")
                        .showWarning();
                return false;
            }
        }

        try {
            State.getClientResolver().modifyClientDetails(viewedClient, modifyClientObject);
            String actionText = modifyClientObject.toString();

            Notifications.create()
                    .title("Updated Client")
                    .text(actionText)
                    .showInformation();

            HistoryItem save = new HistoryItem("UPDATE CLIENT INFO",
                    String.format("Updated client %s with values: %s", viewedClient.getFullName(), actionText));
            JSONConverter.updateHistory(save, "action_history.json");

        } catch (NotFoundException e) {
            LOGGER.log(Level.WARNING, "Client not found");
            PageNavigator.showAlert(AlertType.WARNING, "Client not found", "The client could not be found on the "
                    + "server, it may have been deleted");
            return false;
        } catch (ServerRestException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            PageNavigator.showAlert(AlertType.WARNING, "Server error", "Could not apply changes on the server, "
                    + "please try again later");
            return false;
        } catch (IfMatchFailedException e) {
            LOGGER.log(Level.INFO, "If-Match did not match");
            PageNavigator.showAlert(
                    AlertType.WARNING,
                    "Outdated Data",
                    "The client has been modified since you retrieved the data.\nIf you would still like to "
                            + "apply these changes please submit again, otherwise refresh the page to update the data.");
            return false;
        }

        System.out.println("refreshing");
        PageNavigator.refreshAllWindows();
        return true;

    }

    /**
     * Displays the currently viewed clients BMI.
     */
    private void displayBMI() {
        bmiLabel.setText(String.format("%.01f", viewedClient.getBMI()));
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
}
