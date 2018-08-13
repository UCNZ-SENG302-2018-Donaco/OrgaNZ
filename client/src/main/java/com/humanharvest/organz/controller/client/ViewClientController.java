package com.humanharvest.organz.controller.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.clinician.ViewBaseController;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.validators.client.ClientBornAndDiedDatesValidator;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.views.client.ModifyClientObject;
import org.apache.commons.io.IOUtils;
import org.controlsfx.control.Notifications;

/**
 * Controller for the view/edit client page.
 */
public class ViewClientController extends ViewBaseController {

    private static final Logger LOGGER = Logger.getLogger(ViewClientController.class.getName());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withZone(ZoneId.systemDefault());
    private static final int maxFileSize = 2000000; // (2mb)

    private final Session session;
    private final ClientManager manager;
    private Client viewedClient;

    @FXML
    private Pane sidebarPane, menuBarPane, deathDetailsPane;
    @FXML
    private Label creationDate, lastModified, legalNameLabel, dobLabel, heightLabel, weightLabel, ageDisplayLabel,
            ageLabel, bmiLabel, fullName, dodLabel, timeOfDeathLabel, countryOfDeathLabel, regionOfDeathLabel,
            cityOfDeathLabel;
    @FXML
    private TextField fname, lname, mname, pname, height, weight, regionTF, deathTimeField, deathRegionTF, deathCity;
    @FXML
    private TextArea address;
    @FXML
    private Button uploadPhotoButton, deletePhotoButton, applyButton, cancelButton;
    @FXML
    private DatePicker dob, deathDatePicker;
    @FXML
    private ChoiceBox<Gender> gender, genderIdentity;
    @FXML
    private ChoiceBox<BloodType> btype;
    @FXML
    private ChoiceBox<Region> regionCB, deathRegionCB;
    @FXML
    private ChoiceBox<Country> country, deathCountry;
    @FXML
    private ImageView imageView;
    @FXML
    private ToggleGroup isDeadToggleGroup;
    @FXML
    private ToggleButton aliveToggleBtn, deadToggleBtn;

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
        // Setup all dropdowns with correct values
        gender.setItems(FXCollections.observableArrayList(Gender.values()));
        genderIdentity.setItems(FXCollections.observableArrayList(Gender.values()));
        btype.setItems(FXCollections.observableArrayList(BloodType.values()));
        regionCB.setItems(FXCollections.observableArrayList(Region.values()));
        deathRegionCB.setItems(FXCollections.observableArrayList(Region.values()));
        setEnabledCountries();

        // Add listeners to switch input types for region depending on if the country is NZ
        country.valueProperty().addListener(change -> enableAppropriateRegionInput(country, regionCB, regionTF));
        deathCountry.valueProperty().addListener(change -> enableAppropriateRegionInput(deathCountry, deathRegionCB,
                deathRegionTF));
        // Add listeners to enable/disable death details pane if dead/alive is selected
        isDeadToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == deadToggleBtn) {
                deathDetailsPane.setDisable(false);
                // Pre-populate inputs with current location details
                deathDatePicker.setValue(LocalDate.now());
                deathCountry.setValue(viewedClient.getCountry());
                if (viewedClient.getCountry() == Country.NZ) {
                    deathRegionCB.setValue(Region.fromString(viewedClient.getRegion()));
                } else {
                    deathRegionTF.setText(viewedClient.getRegion());
                }
                deathCity.setText(viewedClient.getCurrentAddress());
            } else if (newValue == aliveToggleBtn) {
                deathDetailsPane.setDisable(true);
                // Clear current input values
                deathDatePicker.setValue(null);
                deathTimeField.setText("");
                deathCountry.setValue(null);
                deathRegionCB.setValue(null);
                deathRegionTF.setText("");
                deathCity.setText("");
            }
        });
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
        refresh();
    }

    @Override
    public void refresh() {
        setEnabledCountries();

        // Refresh client data from server
        try {
            viewedClient = manager.getClientByID(viewedClient.getUid()).orElseThrow(ServerRestException::new);
        } catch (ServerRestException e) {
            e.printStackTrace();
            PageNavigator.showAlert(AlertType.ERROR,
                    "Server Error",
                    "An error occurred while trying to fetch from the server.\nPlease try again later.");
            return;
        }
        // Update all fields in the view with new data
        updateClientFields();
        loadImage();

        // Set window title accordingly
        mainController.refreshNavigation();
        if (session.getLoggedInUserType() == UserType.CLIENT) {
            mainController.setTitle("View Client: " + viewedClient.getPreferredNameFormatted());
            aliveToggleBtn.setDisable(true);
            deadToggleBtn.setDisable(true);
            deathDetailsPane.setDisable(true);
        } else if (windowContext.isClinViewClientWindow()) {
            mainController.setTitle("View Client: " + viewedClient.getFullName());
            if (viewedClient.getDateOfDeathIsEditable()) {
                aliveToggleBtn.setDisable(true);
                deadToggleBtn.setDisable(true);
                deathDatePicker.setDisable(true);
                deathDatePicker.setTooltip(new Tooltip("Date of death is not editable because at least one organ has been "
                        + "manually overridden."));
            }
        }


        // Run these to reset all labels to correct colours
        checkMandatoryFields();
        checkNonMandatoryFields();
        checkDeathDetailsFields();
    }

    /**
     * Sets which countries are enabled for the country/deathCountry dropdowns.
     */
    private void setEnabledCountries() {
        ObservableList<Country> enabledCountries = FXCollections.observableArrayList(
                State.getConfigManager().getAllowedCountries());
        country.setItems(enabledCountries);
        deathCountry.setItems(enabledCountries);
    }

    /**
     * Updates all of the fields of the client.
     */
    private void updateClientFields() {
        fname.setText(viewedClient.getFirstName());
        lname.setText(viewedClient.getLastName());
        mname.setText(viewedClient.getMiddleName());
        pname.setText(viewedClient.getPreferredName());
        dob.setValue(viewedClient.getDateOfBirth());
        gender.setValue(viewedClient.getGender());
        genderIdentity.setValue(viewedClient.getGenderIdentity());
        height.setText(String.valueOf(viewedClient.getHeight()));
        weight.setText(String.valueOf(viewedClient.getWeight()));
        btype.setValue(viewedClient.getBloodType());
        country.setValue(viewedClient.getCountry());

        enableAppropriateRegionInput(deathCountry, deathRegionCB, deathRegionTF);
        enableAppropriateRegionInput(country, regionCB, regionTF);
        if (viewedClient.getCountry() == Country.NZ && viewedClient.getRegion() != null) {
            regionCB.setValue(Region.fromString(viewedClient.getRegion()));
        } else {
            regionTF.setText(viewedClient.getRegion());
        }
        address.setText(viewedClient.getCurrentAddress());
        fullName.setText(viewedClient.getPreferredNameFormatted());

        creationDate.setText(formatter.format(viewedClient.getCreatedTimestamp()));

        if (viewedClient.getModifiedTimestamp() == null) {
            lastModified.setText("Not yet modified.");
        } else {
            lastModified.setText(formatter.format(viewedClient.getModifiedTimestamp()));
        }
        displayBMI();
        displayAge();

        // Set values of death details fields
        if (viewedClient.isDead()) {
            isDeadToggleGroup.selectToggle(deadToggleBtn);
            aliveToggleBtn.setDisable(true);
            deathDatePicker.setValue(viewedClient.getDateOfDeath());
            deathTimeField.setText(viewedClient.getTimeOfDeath().toString());
            deathCountry.setValue(viewedClient.getCountryOfDeath());
            if (viewedClient.getCountryOfDeath() == Country.NZ && viewedClient.getRegionOfDeath() != null) {
                deathRegionCB.setValue(Region.fromString(viewedClient.getRegionOfDeath()));
            } else {
                deathRegionTF.setText(viewedClient.getRegionOfDeath());
            }
            deathCity.setText(viewedClient.getCityOfDeath());
        } else {
            isDeadToggleGroup.selectToggle(aliveToggleBtn);
        }
    }

    private void enableAppropriateRegionInput(ChoiceBox<Country> countryChoice, ChoiceBox<Region> regionChoice,
            TextField regionTextField) {
        if (countryChoice.getValue() == Country.NZ) {
            regionChoice.setVisible(true);
            regionTextField.setVisible(false);
        } else {
            regionChoice.setVisible(false);
            regionTextField.setVisible(true);
        }
    }

    /**
     * Saves the changes a user makes to the viewed client if all their inputs are valid.
     * Otherwise the invalid fields text turns red.
     */
    @FXML
    private void apply() {
        if (checkMandatoryFields() & checkNonMandatoryFields() & checkDeathDetailsFields()) {
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
        byte[] bytes;
        try {
            deletePhotoButton.setDisable(false);
            bytes = State.getImageManager().getClientImage(viewedClient.getUid());
        } catch (NotFoundException ex) {
            try {
                deletePhotoButton.setDisable(true);
                bytes = State.getImageManager().getDefaultImage();

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "IO Exception when loading image ", e);
                return;
            }
        } catch (ServerRestException e) {
            PageNavigator.showAlert(AlertType.ERROR, "Server Error", "Something went wrong with the server. "
                    + "Please try again later.");
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return;
        }
        Image image = new Image(new ByteArrayInputStream(bytes));
        imageView.setImage(image);
    }

    /**
     * Prompts a user with a file chooser which is restricted to png's and jpg's. If a valid file of correct size is
     * input, this photo is uploaded as the viewed clients new profile photo.
     */
    @FXML
    public void uploadPhoto() {
        boolean uploadSuccess = false;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("PNG files (*.png)", "*.png") // Restricting only this file type.
        );

        File selectedFile = fileChooser.showOpenDialog(mainController.getStage());
        if (selectedFile != null) {
            if (selectedFile.length() > maxFileSize) {
                PageNavigator.showAlert(AlertType.WARNING, "Image Size Too Large",
                        "The image size is too large. It must be under 2MB.");
            } else if (!selectedFile.canRead()) {
                PageNavigator.showAlert(AlertType.WARNING, "File Couldn't Be Read",
                        "This file could not be read. Ensure you are uploading a valid .png or .jpg");
            } else {
                try {
                    InputStream in = new FileInputStream(selectedFile);
                    uploadSuccess = State.getImageManager()
                            .postClientImage(viewedClient.getUid(), IOUtils.toByteArray(in));

                } catch (FileNotFoundException ex) {
                    PageNavigator.showAlert(AlertType.WARNING, "File Couldn't Be Found",
                            "This file was not found.");
                } catch (IOException ex) {
                    PageNavigator.showAlert(AlertType.WARNING, "File Couldn't Be Read",
                            "This file could not be read. Ensure you are uploading a valid .png or .jpg");
                } catch (ServerRestException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    PageNavigator.showAlert(AlertType.ERROR, "Server Error", "Something went wrong with the server. "
                            + "Please try again later.");
                }
            }
        }
        if (uploadSuccess) {
            refresh();
            PageNavigator.showAlert(AlertType.CONFIRMATION, "Success", "The image has been posted.");
        }
    }

    /**
     * Sets the profile image to null and refreshes the image.
     */
    @FXML
    public void deletePhoto() {
        try {
            State.getImageManager().deleteClientImage(viewedClient.getUid());
            refresh();
        } catch (ServerRestException e) {
            PageNavigator.showAlert(AlertType.ERROR, "Server Error", "Something went wrong with the server. "
                    + "Please try again later.");
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
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
        // Validate legal name
        if (fname.getText().isEmpty() || lname.getText().isEmpty()) {
            legalNameLabel.setTextFill(Color.RED);
            update = false;
        } else {
            legalNameLabel.setTextFill(Color.BLACK);
        }

        // Validate date of birth
        if (ClientBornAndDiedDatesValidator.dateOfBirthIsValid(dob.getValue())) {
            dobLabel.setTextFill(Color.BLACK);
        } else {
            dobLabel.setTextFill(Color.RED);
            update = false;
        }
        return update;
    }

    /**
     * Checks that non mandatory fields have either valid input, or no input. Otherwise red text is shown.
     * @return true if all non mandatory fields have valid/no input.
     */
    private boolean checkNonMandatoryFields() {
        boolean update = true;

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
     * Checks that death details fields have either valid input, or no input. Otherwise red text is shown.
     * @return true if all non mandatory fields have valid input (or the dead toggle button is not selected).
     */
    private boolean checkDeathDetailsFields() {
        boolean allValid = true;
        if (deadToggleBtn.isSelected()) { // they are marked dead

            LocalDate dateOfBirth = dob.getValue();
            LocalDate dateOfDeath = deathDatePicker.getValue();

            // Validate date of death
            if (ClientBornAndDiedDatesValidator.dateOfDeathIsValid(dateOfDeath, dateOfBirth)) {
                dodLabel.setTextFill(Color.BLACK);
            } else {
                dodLabel.setTextFill(Color.RED);
                allValid = false;
            }

            // Validate time of death
            try {
                LocalTime timeOfDeath = LocalTime.parse(deathTimeField.getText());
                if (ClientBornAndDiedDatesValidator.timeOfDeathIsValid(dateOfDeath, timeOfDeath)) {
                    timeOfDeathLabel.setTextFill(Color.BLACK);
                } else {
                    timeOfDeathLabel.setTextFill(Color.RED);
                    allValid = false;
                }
            } catch (DateTimeParseException e) {
                timeOfDeathLabel.setTextFill(Color.RED);
                allValid = false;
            }

            // Validate country of death
            if (deathCountry.getValue() == null) {
                countryOfDeathLabel.setTextFill(Color.RED);
                allValid = false;
            } else {
                countryOfDeathLabel.setTextFill(Color.BLACK);
            }

            // Validate region of death
            if (deathCountry.getValue() == Country.NZ) {
                if (deathRegionCB.getValue() == null) {
                    regionOfDeathLabel.setTextFill(Color.RED);
                    allValid = false;
                } else {
                    regionOfDeathLabel.setTextFill(Color.BLACK);
                }
            } else {
                if (deathRegionTF.getText() == null || deathRegionTF.getText().isEmpty()) {
                    regionOfDeathLabel.setTextFill(Color.RED);
                    allValid = false;
                } else {
                    regionOfDeathLabel.setTextFill(Color.BLACK);
                }
            }

            // Validate city of death
            if (deathCity.getText() == null || deathCity.getText().isEmpty()) {
                cityOfDeathLabel.setTextFill(Color.RED);
                allValid = false;
            } else {
                cityOfDeathLabel.setTextFill(Color.BLACK);
            }
        }
        return allValid;
    }

    /**
     * Records the changes updated as a ModifyClientAction to trace the change in record.
     * @return If there were any changes made
     */
    private boolean updateChanges() {
        ModifyClientObject modifyClientObject = new ModifyClientObject();

        // Register changes on generic fields
        addChangeIfDifferent(modifyClientObject, viewedClient, "firstName", fname.getText());
        addChangeIfDifferent(modifyClientObject, viewedClient, "lastName", lname.getText());
        addChangeIfDifferent(modifyClientObject, viewedClient, "middleName", mname.getText());
        addChangeIfDifferent(modifyClientObject, viewedClient, "preferredName", pname.getText());
        addChangeIfDifferent(modifyClientObject, viewedClient, "dateOfBirth", dob.getValue());
        addChangeIfDifferent(modifyClientObject, viewedClient, "gender", gender.getValue());
        addChangeIfDifferent(modifyClientObject, viewedClient, "genderIdentity", genderIdentity.getValue());
        addChangeIfDifferent(modifyClientObject, viewedClient, "height", Double.parseDouble(height.getText()));
        addChangeIfDifferent(modifyClientObject, viewedClient, "weight", Double.parseDouble(weight.getText()));
        addChangeIfDifferent(modifyClientObject, viewedClient, "bloodType", btype.getValue());
        addChangeIfDifferent(modifyClientObject, viewedClient, "currentAddress", address.getText());
        addChangeIfDifferent(modifyClientObject, viewedClient, "country", country.getValue());
        // Register region change
        if (country.getValue() == Country.NZ) {
            Region region = regionCB.getValue() == null ? Region.UNSPECIFIED : regionCB.getValue();
            addChangeIfDifferent(modifyClientObject, viewedClient, "region", region.toString());
        } else {
            addChangeIfDifferent(modifyClientObject, viewedClient, "region", regionTF.getText());
        }

        // DEATH DETAILS
        if (deadToggleBtn.isSelected()) {
            // Warn user if about to mark a client as dead
            if (viewedClient.isAlive()) {
                ButtonType optionPicked = PageNavigator.showAlert(AlertType.CONFIRMATION,
                        "Are you sure you want to mark this client as dead?",
                        "This will cancel all waiting transplant requests for this client.")
                        .orElse(ButtonType.CANCEL);
                if (optionPicked != ButtonType.OK) {
                    return false;
                }
            }

            addChangeIfDifferent(modifyClientObject, viewedClient, "dateOfDeath", deathDatePicker.getValue());
            try {
                addChangeIfDifferent(modifyClientObject, viewedClient, "timeOfDeath",
                        LocalTime.parse(deathTimeField.getText()));
            } catch (DateTimeParseException e) {
                // NOTE: this exception shouldn't occur, as checkDeathDetailsFields() should've been run first
                timeOfDeathLabel.setTextFill(Color.RED);
                return false;
            }
            addChangeIfDifferent(modifyClientObject, viewedClient, "countryOfDeath", deathCountry.getValue());
            addChangeIfDifferent(modifyClientObject, viewedClient, "cityOfDeath", deathCity.getText());
            // Register death region change
            if (deathCountry.getValue() == Country.NZ) {
                Region region = deathRegionCB.getValue() == null ? Region.UNSPECIFIED : deathRegionCB.getValue();
                addChangeIfDifferent(modifyClientObject, viewedClient, "regionOfDeath", region.toString());
            } else {
                addChangeIfDifferent(modifyClientObject, viewedClient, "regionOfDeath", deathRegionTF.getText());
            }
        }

        if (modifyClientObject.getModifiedFields().isEmpty()) {
            // Literally nothing was changed
            Notifications.create()
                    .title("No changes were made.")
                    .text("No changes were made to the client.")
                    .showWarning();
            return false;
        }

        try {
            State.getClientResolver().modifyClientDetails(viewedClient, modifyClientObject);
            String actionText = modifyClientObject.toString();
            Notifications.create()
                    .title("Updated Client")
                    .text(actionText)
                    .showInformation();

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
