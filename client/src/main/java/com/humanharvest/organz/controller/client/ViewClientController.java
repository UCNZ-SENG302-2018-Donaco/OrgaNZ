package com.humanharvest.organz.controller.client;

import static com.humanharvest.organz.views.ModifyBaseObject.addChangeIfDifferent;

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
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
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
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.touch.MultitouchHandler;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.validators.NotEmptyStringValidator;
import com.humanharvest.organz.utilities.validators.client.ClientBornAndDiedDatesValidator;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.views.client.ModifyClientObject;

import org.apache.commons.io.IOUtils;

/**
 * Controller for the view/edit client page.
 */
public class ViewClientController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(ViewClientController.class.getName());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

    private static final int MAX_FILE_SIZE = 2_000_000; // (2mb)

    private final Session session;
    private final ClientManager manager;
    private Client viewedClient;
    private File imageToUpload;
    private boolean deleteImage;

    @FXML
    private Pane menuBarPane, deathDetailsPane;
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

    private final ObservableList<Country> observableEnabledCountries = FXCollections.observableArrayList();
    private final ObservableList<Hospital> observableHospitals = FXCollections.observableArrayList();
    private final SortedList<Hospital> sortedHospitals = new SortedList<>(observableHospitals);

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
        hospital.setItems(sortedHospitals);
        sortedHospitals.setComparator(Comparator.comparing(Hospital::getName));
        updateHospitals();
        regionCB.setItems(FXCollections.observableArrayList(Region.values()));
        deathRegionCB.setItems(FXCollections.observableArrayList(Region.values()));
        country.setItems(observableEnabledCountries);
        deathCountry.setItems(observableEnabledCountries);

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
                deathTimeField.setText(LocalTime.now().format(timeFormatter));
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
        mainController.loadNavigation(menuBarPane);
        if (session.getLoggedInUserType() == Session.UserType.CLIENT) {
            viewedClient = session.getLoggedInClient();
        } else if (windowContext.isClinViewClientWindow()) {
            viewedClient = windowContext.getViewClient();
        }
        refreshData();
    }

    /**
     * Fully updates the Client if there have not been any changes. If there have, then no new data is loaded
     */
    @Override
    public void refresh() {
        if (!hasChanges()) {
            // The client has not been modified, so update the data
            refreshData();
        }
    }

    private void refreshData() {
        setEnabledCountries();

        Task<Client> retrieveClient = new Task<Client>() {
            @Override
            protected Client call() throws ServerRestException {
                return manager.getClientByID(viewedClient.getUid())
                        .orElseThrow(NotFoundException::new);
            }
        };

        retrieveClient.setOnSucceeded(success -> {
            viewedClient = retrieveClient.getValue();

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

                // client is dead - disable resurrecting
                aliveToggleBtn.setDisable(viewedClient.isDead());
                deadToggleBtn.setDisable(viewedClient.isDead());

                // date of death is not editable - disable editing of date and time
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
        });

        retrieveClient.setOnFailed(fail -> {
            LOGGER.log(Level.SEVERE, retrieveClient.getException().getMessage(), retrieveClient.getException());
            Notifications.create()
                    .title("Server Error")
                    .text("Could not retrieve the client's data from the server.")
                    .showError();
        });

        new Thread(retrieveClient).start();
    }

    private boolean hasChanges() {
        ModifyClientObject modifyClientObject = new ModifyClientObject();

        addChangesIfDifferent(modifyClientObject);

        if (deathDetailsPane.isDisabled()) {
            return !modifyClientObject.getModifiedFields().isEmpty();
        } else {
            updateDeathFields(modifyClientObject);
            return !modifyClientObject.getModifiedFields().isEmpty();
        }
    }

    /**
     * Sets which countries are enabled for the country/deathCountry dropdowns.
     */
    private void setEnabledCountries() {
        Task<Set<Country>> task = new Task<Set<Country>>() {
            @Override
            protected Set<Country> call() throws ServerRestException {
                return com.humanharvest.organz.state.State.getConfigManager().getAllowedCountries();
            }
        };

        task.setOnSucceeded(success -> {
            observableEnabledCountries.setAll(task.getValue());
            FXCollections.sort(observableEnabledCountries);
        });

        task.setOnFailed(fail -> {
            LOGGER.log(Level.SEVERE, task.getException().getMessage(), task.getException());
            Notifications.create()
                    .title("Server Error")
                    .text("Could not retrieve enabled countries from the server.")
                    .showError();
        });

        new Thread(task).start();
    }

    private void updateHospitals() {
        Task<Set<Hospital>> task = new Task<Set<Hospital>>() {
            @Override
            protected Set<Hospital> call() throws ServerRestException {
                return com.humanharvest.organz.state.State.getConfigManager().getHospitals();
            }
        };

        task.setOnSucceeded(success -> {
            observableHospitals.setAll(task.getValue());
            if (viewedClient == null || viewedClient.getHospital() == null) {
                hospital.setValue(null);
            } else {
                hospital.setValue(hospital.getItems().stream()
                        .filter(hospitalItem -> hospitalItem.getId().equals(viewedClient.getHospital().getId()))
                        .findFirst()
                        .orElse(null));
            }
        });

        task.setOnFailed(fail -> {
            LOGGER.log(Level.SEVERE, task.getException().getMessage(), task.getException());
            Notifications.create()
                    .title("Server Error")
                    .text("Could not retrieve hospitals from the server.")
                    .showError();
        });

        new Thread(task).start();
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
        if (!country.getItems().contains(viewedClient.getCountry())) {
            country.getItems().add(viewedClient.getCountry());
        }
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

        displayLastModified();
        displayBMI();
        displayAge();

        // Set values of death details fields
        if (viewedClient.isDead()) {
            isDeadToggleGroup.selectToggle(deadToggleBtn);
            aliveToggleBtn.setDisable(true);
            deathDatePicker.setValue(viewedClient.getDateOfDeath());
            deathTimeField.setText(viewedClient.getTimeOfDeath().format(timeFormatter));
            if (!deathCountry.getItems().contains(viewedClient.getCountryOfDeath())) {
                deathCountry.getItems().add(viewedClient.getCountryOfDeath());
            }
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

    private void displayLastModified() {
        if (viewedClient.getModifiedTimestamp() == null) {
            lastModified.setText("Not yet modified.");
        } else {
            lastModified.setText(formatter.format(viewedClient.getModifiedTimestamp()));
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
        // Retrieve the picture from the server in a new thread
        Task<Image> task = new Task<Image>() {
            @Override
            protected Image call() throws ServerRestException, IOException {
                try {
                    return new Image(new ByteArrayInputStream(
                            com.humanharvest.organz.state.State.getImageManager()
                                    .getClientImage(viewedClient.getUid())));
                } catch (NotFoundException exc) {
                    return new Image(new ByteArrayInputStream(
                            com.humanharvest.organz.state.State.getImageManager().getDefaultImage()));
                }
            }
        };

        task.setOnSucceeded(event -> imageView.setImage(task.getValue()));

        task.setOnFailed(event -> {
            try {
                throw task.getException();
            } catch (IOException exc) {
                LOGGER.log(Level.SEVERE, "IOException when loading default image.", exc);
            } catch (ServerRestException exc) {
                LOGGER.log(Level.SEVERE, "", exc);
                Notifications.create()
                        .title("Server Error")
                        .text("A client's profile picture could not be retrieved from the server.")
                        .showError();
            } catch (Throwable exc) {
                LOGGER.log(Level.SEVERE, exc.getMessage(), exc);
            }
        });

        new Thread(task).start();
    }

    /**
     * Prompts a user with a file chooser which is restricted to PNGs. If a valid file of correct size is
     * input, this photo is set as the client's profile picture (and will be uploaded when the user clicks Apply).
     */
    @FXML
    public void choosePhotoClicked() {
        MultitouchHandler.handleTouch(false);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("PNG files (*.png)", "*.png") // Restricting only this file type.
        );

        File selectedFile = fileChooser.showOpenDialog(State.getPrimaryStage());
        MultitouchHandler.handleTouch(true);

        if (selectedFile != null) {
            if (selectedFile.length() > MAX_FILE_SIZE) {
                PageNavigator.showAlert(AlertType.WARNING, "Image Size Too Large",
                        "The image is too large. It must be under 2 megabytes.", mainController.getStage());
            } else if (!selectedFile.canRead()) {
                PageNavigator.showAlert(AlertType.WARNING, "File Couldn't Be Read",
                        "This file could not be read. Ensure you are uploading a valid png file.",
                        mainController.getStage());
            } else {
                imageToUpload = selectedFile;
                deletePhotoButton.setDisable(false);
                deleteImage = false;
                imageView.setImage(new Image(imageToUpload.toURI().toString()));
            }
        }
    }

    private void uploadImage() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws IOException, ServerRestException {
                try (InputStream in = new FileInputStream(imageToUpload)) {
                    com.humanharvest.organz.state.State.getImageManager()
                            .postClientImage(viewedClient.getUid(), IOUtils.toByteArray(in));
                }
                return null;
            }
        };

        task.setOnSucceeded(success -> {
            imageToUpload = null;
        });

        task.setOnFailed(fail -> {
            try {
                throw task.getException();
            } catch (FileNotFoundException exc) {
                LOGGER.log(Level.INFO, exc.getMessage(), exc);
                PageNavigator.showAlert(AlertType.WARNING,
                        "File Couldn't Be Found",
                        "This file was not found.",
                        mainController.getStage());
            } catch (IOException exc) {
                LOGGER.log(Level.INFO, exc.getMessage(), exc);
                PageNavigator.showAlert(AlertType.WARNING,
                        "File Couldn't Be Read",
                        "This file could not be read. Ensure you are uploading a valid .png or .jpg",
                        mainController.getStage());
            } catch (ServerRestException exc) {
                LOGGER.log(Level.SEVERE, exc.getMessage(), exc);
                PageNavigator.showAlert(AlertType.ERROR,
                        "Server Error",
                        "Something went wrong with the server. Please try again later.",
                        mainController.getStage());
            } catch (Throwable exc) {
                LOGGER.log(Level.SEVERE, exc.getMessage(), exc);
            }
        });

        new Thread(task).start();
    }

    /**
     * Sets the profile image to null and refreshes the image.
     */
    @FXML
    public void deletePhotoClicked() {
        deleteImage = true;
        imageToUpload = null;
        deletePhotoButton.setDisable(true);
        try {
            imageView.setImage(new Image(new ByteArrayInputStream(
                    com.humanharvest.organz.state.State.getImageManager().getDefaultImage())));
        } catch (IOException | ServerRestException exc) {
            LOGGER.log(Level.INFO, exc.getMessage(), exc);
            PageNavigator.showAlert(AlertType.WARNING,
                    "Default File Couldn't Be Retrieved",
                    "The default image could not be retrieved from the server.",
                    mainController.getStage());
        }
    }

    private void deleteImage() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws ServerRestException {
                com.humanharvest.organz.state.State.getImageManager()
                        .deleteClientImage(viewedClient.getUid());
                return null;
            }
        };

        task.setOnSucceeded(success -> deleteImage = false);

        task.setOnFailed(fail -> {
            LOGGER.log(Level.SEVERE, task.getException().getMessage(), task.getException());
            Notifications.create()
                    .title("Server Error")
                    .text("A server error occurred when trying to delete the client's photo.")
                    .showError();
        });

        new Thread(task).start();
    }

    /**
     * Resets the page back to its default state.
     */
    @FXML
    private void cancel() {
        refreshData();
        imageToUpload = null;
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
                LocalTime timeOfDeath = LocalTime.parse(deathTimeField.getText(), timeFormatter);
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
                applyChanges(modifyClientObject);
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
        PageNavigator.showAlert(AlertType.CONFIRMATION,
                "Are you sure you want to mark this client as dead?",
                "This will cancel all waiting transplant requests for this client.", mainController.getStage(),
                () -> {
                    updateDeathFields(modifyClientObject);
                    applyChanges(modifyClientObject);
                });
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
                    LocalTime.parse(deathTimeField.getText(), timeFormatter));
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
    }

    /**
     * Checks if any changes have been made, and if so applies those changes.
     * If none have been made or other errors occur, errors are displayed.
     *
     * @param modifyClientObject The object to apply the changes from.
     */
    private void applyChanges(ModifyClientObject modifyClientObject) {
        if (modifyClientObject.getModifiedFields().isEmpty() && imageToUpload == null && !deleteImage) {
            // Literally nothing was changed
            Notifications.create()
                    .title("No changes were made.")
                    .text("No changes were made to the client.")
                    .showWarning();
        } else {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws ServerRestException {
                    com.humanharvest.organz.state.State.getClientResolver()
                            .modifyClientDetails(viewedClient, modifyClientObject);
                    return null;
                }
            };

            task.setOnSucceeded(success -> {
                String actionText = modifyClientObject.toString();

                if (imageToUpload != null) {
                    uploadImage();
                    actionText += "\nChanged profile picture.";
                } else if (deleteImage) {
                    deleteImage();
                    actionText += "\nRemoved profile picture.";
                }
                Notifications.create()
                        .title("Updated Client")
                        .text(actionText)
                        .showInformation();

                finishUpdateChanges();
            });

            task.setOnFailed(fail -> {
                try {
                    throw task.getException();
                } catch (NotFoundException e) {
                    AlertHelper.showNotFoundAlert(LOGGER, e, mainController);
                } catch (ServerRestException e) {
                    AlertHelper.showRestAlert(LOGGER, e, mainController);
                } catch (IfMatchFailedException e) {
                    AlertHelper.showIfMatchAlert(LOGGER, e, mainController);
                } catch (Throwable exc) {
                    LOGGER.log(Level.SEVERE, exc.getMessage(), exc);
                }
            });

            new Thread(task).start();
        }
    }

    /**
     * Called after the applyChanges function successfully resolves
     */
    private void finishUpdateChanges() {
        PageNavigator.refreshAllWindows();
        refreshData();
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
}
