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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.controlsfx.control.Notifications;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.controller.AlertHelper;
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
import com.humanharvest.organz.utilities.validators.NotEmptyStringValidator;
import com.humanharvest.organz.utilities.validators.client.ClientBornAndDiedDatesValidator;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext;
import com.humanharvest.organz.views.client.ModifyClientObject;

import org.apache.commons.io.IOUtils;

/**
 * Controller for the view/edit client page.
 */
public class ViewClientController extends ViewBaseController {

    private static final Logger LOGGER = Logger.getLogger(ViewClientController.class.getName());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withZone(ZoneId.systemDefault());

    private static final int MAX_FILE_SIZE = 2_000_000; // (2mb)

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
    private Button deletePhotoButton;
    @FXML
    private DatePicker dob, deathDatePicker;
    @FXML
    private ChoiceBox<Gender> gender, genderIdentity;
    @FXML
    private ChoiceBox<BloodType> btype;
    @FXML
    private ChoiceBox<Hospital> hospital;
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
    @FXML
    private GridPane dateOfDeathPane;

    public ViewClientController() {
        manager = State.getClientManager();
        session = State.getSession();
    }

    private static void enableAppropriateRegionInput(
            ChoiceBox<Country> countryChoice,
            ChoiceBox<Region> regionChoice,
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
     * Given a field and it's corresponding label, check that it is a valid positive number.
     * If it is not, set the label text to red. If it is, set it to black.
     *
     * @param field The field to check
     * @param label The label to apply color to
     * @return If the field value was a valid non negative number
     */
    private static boolean doubleFieldIsInvalid(TextField field, Label label) {
        try {
            double w = Double.parseDouble(field.getText());
            if (w < 0) {
                label.setTextFill(Color.RED);
                return true;
            } else {
                label.setTextFill(Color.BLACK);
                return false;
            }

        } catch (NumberFormatException ex) {
            label.setTextFill(Color.RED);
            return true;
        }
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
        Set<Hospital> hospitalSet = State.getConfigManager().getHospitals();
        ObservableList<Hospital> hospitals = FXCollections.observableArrayList(new ArrayList<>(hospitalSet));
        hospital.setItems(hospitals);
        hospital.getItems().sort(Comparator.comparing(Hospital::getName));
        regionCB.setItems(FXCollections.observableArrayList(Region.values()));
        deathRegionCB.setItems(FXCollections.observableArrayList(Region.values()));
        setEnabledCountries();

        // Add listeners to switch input types for region depending on if the country is NZ
        country.valueProperty().addListener(change -> enableAppropriateRegionInput(country, regionCB, regionTF));
        deathCountry.valueProperty().addListener(change -> enableAppropriateRegionInput(deathCountry, deathRegionCB,
                deathRegionTF));
        // Add listeners to enable/disable death details pane if dead/alive is selected
        isDeadToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(newValue, deadToggleBtn)) {
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
            } else if (Objects.equals(newValue, aliveToggleBtn)) {
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
            AlertHelper.showRestAlert(LOGGER, e, mainController);
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
            // date of death is not editable - disable all the things
            aliveToggleBtn.setDisable(!viewedClient.getDateOfDeathIsEditable());
            deadToggleBtn.setDisable(!viewedClient.getDateOfDeathIsEditable());
            deathDatePicker.setDisable(!viewedClient.getDateOfDeathIsEditable());
            deathTimeField.setDisable(!viewedClient.getDateOfDeathIsEditable());
            if (!viewedClient.getDateOfDeathIsEditable()) {
                Tooltip tooltip = new Tooltip(
                        "Date and time of death is not editable, "
                                + "because at least one organ has been manually overridden. "
                                + "To edit the date and/or time, please cancel all manual overrides.");
                Tooltip.install(dateOfDeathPane, tooltip);
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
        if (viewedClient.getHospital() == null) {
            hospital.setValue(null);
        } else {
            hospital.setValue(hospital.getItems().stream()
                    .filter(hospitalItem -> hospitalItem.getId().equals(viewedClient.getHospital().getId()))
                    .findFirst()
                    .orElse(null));
        }

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

    /**
     * Saves the changes a user makes to the viewed client if all their inputs are valid.
     * Otherwise the invalid fields text turns red.
     */
    @FXML
    private void apply() {
        // We need to call all functions because they have side effects
        boolean mandatoryFields = checkMandatoryFields();
        boolean nonMandatoryFields = checkNonMandatoryFields();
        boolean deathDetails = checkDeathDetailsFields();
        if (mandatoryFields && nonMandatoryFields && deathDetails) {
            updateChanges();
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
        } catch (NotFoundException ignored) {
            try {
                deletePhotoButton.setDisable(true);
                bytes = State.getImageManager().getDefaultImage();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "IO Exception when loading image ", e);
                return;
            }
        } catch (ServerRestException e) {
            PageNavigator.showAlert(AlertType.ERROR, "Server Error", "Something went wrong with the server. "
                    + "Please try again later.", mainController.getStage());
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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("PNG files (*.png)", "*.png") // Restricting only this file type.
        );

        File selectedFile = fileChooser.showOpenDialog(State.getPrimaryStage());
        boolean uploadSuccess = false;
        if (selectedFile != null) {
            if (selectedFile.length() > MAX_FILE_SIZE) {
                PageNavigator.showAlert(AlertType.WARNING, "Image Size Too Large",
                        "The image size is too large. It must be under 2MB.", mainController.getStage());
            } else if (!selectedFile.canRead()) {
                PageNavigator.showAlert(AlertType.WARNING, "File Couldn't Be Read",
                        "This file could not be read. Ensure you are uploading a valid .png or .jpg",
                        mainController.getStage());
            } else {
                try (InputStream in = new FileInputStream(selectedFile)) {
                    uploadSuccess = State.getImageManager()
                            .postClientImage(viewedClient.getUid(), IOUtils.toByteArray(in));

                } catch (FileNotFoundException e) {
                    LOGGER.log(Level.INFO, e.getMessage(), e);
                    PageNavigator.showAlert(AlertType.WARNING, "File Couldn't Be Found",
                            "This file was not found.", mainController.getStage());
                } catch (IOException e) {
                    LOGGER.log(Level.INFO, e.getMessage(), e);
                    PageNavigator.showAlert(AlertType.WARNING, "File Couldn't Be Read",
                            "This file could not be read. Ensure you are uploading a valid .png or .jpg",
                            mainController.getStage());
                } catch (ServerRestException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    PageNavigator.showAlert(AlertType.ERROR, "Server Error", "Something went wrong with the server. "
                            + "Please try again later.", mainController.getStage());
                }
            }
        }
        if (uploadSuccess) {
            refresh();
            PageNavigator.showAlert(AlertType.CONFIRMATION, "Success", "The image has been posted.",
                    mainController.getStage());
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
                    + "Please try again later.", mainController.getStage());
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
     *
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
     *
     * @return true if all non mandatory fields have valid/no input.
     */
    private boolean checkNonMandatoryFields() {
        boolean update = true;

        if (doubleFieldIsInvalid(weight, weightLabel)) {
            update = false;
        }
        if (doubleFieldIsInvalid(height, heightLabel)) {
            update = false;
        }

        return update;
    }

    /**
     * Checks that death details fields have either valid input, or no input. Otherwise red text is shown.
     *
     * @return true if all non mandatory fields have valid input (or the dead toggle button is not selected).
     */
    private boolean checkDeathDetailsFields() {
        boolean allValid = true;
        if (!deathDetailsPane.isDisabled()) { // they are marked dead

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
            // If they are in NZ and the combobox is null,
            // or they aren't in NZ and the textbox is empty, then the region is invalid.
            if ((deathCountry.getValue() == Country.NZ && deathRegionCB.getValue() == null)
                    || (deathCountry.getValue() != Country.NZ
                    && NotEmptyStringValidator.isInvalidString(deathRegionTF.getText()))) {
                regionOfDeathLabel.setTextFill(Color.RED);
                allValid = false;
            } else {
                regionOfDeathLabel.setTextFill(Color.BLACK);
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
     * Applies the changes to the client, with various error checking by given method.
     * Flow is:
     * updateChanges calls addChangesIfDifferent,
     * which adds various changes to the ModifyClientObject only if they have changed.
     * Then, if the client has been marked as dead, prompts the user for confirmation.
     * If yes, or if the user is already dead, updateDeathFields is called, which adds any changed death fields.
     * Once that occurs, updateDeathFields calls applyChanges, or that is called directly if the client is not dead.
     */
    private void updateChanges() {

        ModifyClientObject modifyClientObject = new ModifyClientObject();

        // Add the basic changes to the ModifyClientObject
        addChangesIfDifferent(modifyClientObject);

        // If we are marking a client as dead, we need to alert them that this will also resolve the transplant requests
        // Calling either method will flow through the chain.
        // Prompt will continue if okay is selected and call updateDeathFields
        // and updateDeathFields calls applyChanges
        if (deathDetailsPane.isDisabled()) {
            applyChanges(modifyClientObject);
        } else {
            if (viewedClient.isAlive()) {
                promptMarkAsDead(modifyClientObject);
            } else {
                updateDeathFields(modifyClientObject);
            }
        }
    }

    /**
     * Applies simple property changes to the client only if they've changed.
     *
     * @param modifyClientObject The object to apply changes to.
     */
    private void addChangesIfDifferent(ModifyClientObject modifyClientObject) {
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
        addChangeIfDifferent(modifyClientObject, viewedClient, "hospital", hospital.getValue());

        // Register region change
        if (country.getValue() == Country.NZ) {
            Region region = regionCB.getValue() == null ? Region.UNSPECIFIED : regionCB.getValue();
            addChangeIfDifferent(modifyClientObject, viewedClient, "region", region.toString());
        } else {
            addChangeIfDifferent(modifyClientObject, viewedClient, "region", regionTF.getText());
        }
    }

    /**
     * Prompt the user for confirmation if they are marking the client as dead.
     * Awaits a response, if it is true then the execution continues with updateDeathFields.
     *
     * @param modifyClientObject The object to pass along that changes are applied to.
     */
    private void promptMarkAsDead(ModifyClientObject modifyClientObject) {
        Property<Boolean> response = PageNavigator.showAlert(AlertType.CONFIRMATION,
                "Are you sure you want to mark this client as dead?",
                "This will cancel all waiting transplant requests for this client.", mainController.getStage());

        if (response.getValue() != null) {
            updateDeathFields(modifyClientObject);
        } else {
            response.addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    updateDeathFields(modifyClientObject);
                }
            });
        }
    }

    /**
     * Apply death details changes to the client
     *
     * @param modifyClientObject The object to apply the changes to
     */
    private void updateDeathFields(ModifyClientObject modifyClientObject) {
        addChangeIfDifferent(modifyClientObject, viewedClient, "dateOfDeath", deathDatePicker.getValue());
        try {
            addChangeIfDifferent(modifyClientObject, viewedClient, "timeOfDeath",
                    LocalTime.parse(deathTimeField.getText()));
        } catch (DateTimeParseException e) {
            // NOTE: this exception shouldn't occur, as checkDeathDetailsFields() should've been run first
            timeOfDeathLabel.setTextFill(Color.RED);
            return;
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

        applyChanges(modifyClientObject);
    }

    /**
     * Checks if any changes have been made, and if so applies those changes.
     * If none have been made or other errors occur, errors are displayed.
     *
     * @param modifyClientObject The object to apply the changes from.
     */
    private void applyChanges(ModifyClientObject modifyClientObject) {
        if (modifyClientObject.getModifiedFields().isEmpty()) {
            // Literally nothing was changed
            Notifications.create()
                    .title("No changes were made.")
                    .text("No changes were made to the client.")
                    .showWarning();
        } else {
            try {
                State.getClientResolver().modifyClientDetails(viewedClient, modifyClientObject);
                String actionText = modifyClientObject.toString();
                Notifications.create()
                        .title("Updated Client")
                        .text(actionText)
                        .showInformation();

                finishUpdateChanges();

            } catch (NotFoundException e) {
                AlertHelper.showNotFoundAlert(LOGGER, e, mainController);
            } catch (ServerRestException e) {
                AlertHelper.showRestAlert(LOGGER, e, mainController);
            } catch (IfMatchFailedException e) {
                AlertHelper.showIfMatchAlert(LOGGER, e, mainController);
            }
        }
    }

    /**
     * Called after the applyChanges function successfully resolves
     */
    private void finishUpdateChanges() {
        PageNavigator.refreshAllWindows();
        displayBMI();
        displayAge();
        lastModified.setText(formatter.format(viewedClient.getModifiedTimestamp()));
    }

    /**
     * Displays the currently viewed clients BMI.
     */
    private void displayBMI() {
        bmiLabel.setText(String.format(Locale.UK, "%.01f", viewedClient.getBMI()));
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

    @FXML
    private void openRecDetForLiver() {

        MainController newMain = PageNavigator.openNewWindow(200, 400);
        if (newMain != null) {
            newMain.setWindowContext(new WindowContext.WindowContextBuilder()
                    .setAsClinicianViewClientWindow()
                    .viewClient(viewedClient)
                    .build());
            PageNavigator.loadPage(Page.RECEIVER_OVERVIEW, newMain);
        }

    }
}
