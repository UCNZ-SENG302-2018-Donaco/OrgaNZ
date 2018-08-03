package com.humanharvest.organz.controller.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.EnumSet;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import com.humanharvest.organz.utilities.JSONConverter;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext;
import com.humanharvest.organz.views.client.ModifyClientObject;
import org.apache.commons.io.IOUtils;
import org.controlsfx.control.Notifications;

/**
 * Controller for the view/edit client page.
 */
public class ViewClientController extends ViewBaseController {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withZone(ZoneId.systemDefault());

    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final Logger LOGGER = Logger.getLogger(ViewClientController.class.getName());
    private static final int maxFileSize = 2000000; // (2mb)
    private final Session session;
    private final ClientManager manager;
    private Client viewedClient;

    @FXML
    private Pane sidebarPane, menuBarPane;
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
    private DatePicker dob, dod, deathDatePicker;
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
        gender.setItems(FXCollections.observableArrayList(Gender.values()));
        genderIdentity.setItems(FXCollections.observableArrayList(Gender.values()));
        btype.setItems(FXCollections.observableArrayList(BloodType.values()));
        regionCB.setItems(FXCollections.observableArrayList(Region.values()));
        deathRegionCB.setItems(FXCollections.observableArrayList(Region.values()));
        updateCountries();
        fullName.setWrapText(true);

        country.valueProperty().addListener(change -> checkCountry());
        deathCountry.valueProperty().addListener(change -> checkDeathCountry());
        isDeadToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == deadToggleBtn) {
                deathCity.setDisable(false);
                deathTimeField.setDisable(false);
                deathDatePicker.setDisable(false);
                deathCountry.setDisable(false);
                deathRegionCB.setDisable(false);
                deathRegionTF.setDisable(false);
            }
            else if (newValue == aliveToggleBtn) {
                deathCity.setDisable(true);
                deathTimeField.setDisable(true);
                deathDatePicker.setDisable(true);
                deathCountry.setDisable(true);
                deathRegionCB.setDisable(true);
                deathRegionTF.setDisable(true);
            }
        });
    }

    private void updateCountries() {
        EnumSet<Country> countries = EnumSet.noneOf(Country.class);
        countries.addAll(State.getConfigManager().getAllowedCountries());
        country.setItems(FXCollections.observableArrayList(countries));
        if (viewedClient != null && viewedClient.getCountry() != null) {
            country.setValue(viewedClient.getCountry());
        }
        deathCountry.setItems(FXCollections.observableArrayList(countries));
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
        if (viewedClient.isDead()) {
            deadToggleBtn.setSelected(true);
        } else if (!viewedClient.isDead()) {
            aliveToggleBtn.setSelected(true);
        }

        if (viewedClient.isDead()) {
            if (viewedClient.getCountryOfDeath() != null) {
                deathCountry.setValue(viewedClient.getCountryOfDeath());
            }
            if (viewedClient.getDateOfDeath() != null) {
                deathDatePicker.setValue(viewedClient.getDateOfDeath());
            }
            if (viewedClient.getTimeOfDeath() != null) {
                deathTimeField.setText(timeFormat.format(viewedClient.getTimeOfDeath()));
            }
            if (viewedClient.getCityOfDeath() != null) {
                deathCity.setText(viewedClient.getCityOfDeath());
            }
            if (viewedClient.getCountryOfDeath() == Country.NZ) {
                deathRegionCB.setValue(Region.fromString(viewedClient.getRegionOfDeath()));
            } else if (viewedClient.getCountryOfDeath() != Country.NZ) {
                deathRegionTF.setText(viewedClient.getRegionOfDeath());
            }
        }
        else if (viewedClient.isAlive()){
            deathCountry.setValue(viewedClient.getCountry());
            deathCity.setText(viewedClient.getCurrentAddress());
            if (viewedClient.getCountry() == Country.NZ && viewedClient.getRegionOfDeath() != null) {
                deathRegionTF.setVisible(false);
                deathRegionCB.setDisable(false);
                deathRegionCB.setVisible(true);
                deathRegionTF.setDisable(true);
                deathRegionCB.setValue(Region.fromString(viewedClient.getRegion()));
            } else if (viewedClient.getCountry() != Country.NZ && viewedClient.getRegionOfDeath() != null) {
                deathRegionTF.setDisable(false);
                deathRegionTF.setVisible(true);
                deathRegionCB.setDisable(true);
                deathRegionCB.setVisible(false);
                deathRegionTF.setText(viewedClient.getRegion());
            }

        }
        refresh();
    }

        @Override
        public void refresh () {
            try {
                viewedClient = manager.getClientByID(viewedClient.getUid()).orElseThrow(ServerRestException::new);
            } catch (ServerRestException e) {
                e.printStackTrace();
                PageNavigator.showAlert(AlertType.ERROR,
                        "Server Error",
                        "An error occurred while trying to fetch from the server.\nPlease try again later.");
                return;
            }
            updateClientFields();
            mainController.refreshNavigation();
            if (session.getLoggedInUserType() == UserType.CLIENT) {
                mainController.setTitle("View Client: " + viewedClient.getPreferredNameFormatted());
            } else if (windowContext.isClinViewClientWindow()) {
                mainController.setTitle("View Client: " + viewedClient.getFullName());
            }
            loadImage();
            updateCountries();
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

        checkDeathCountry();
        checkCountry();
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

    }

    /**
     * Checks the clients country, changes region input to a choicebox of NZ regions if the country is New Zealand,
     * and changes to a textfield input for any other country
     */
    private void checkCountry() {
        if (country.getValue() == Country.NZ) {
            regionCB.setVisible(true);
            regionTF.setVisible(false);
        } else {
            regionCB.setVisible(false);
            regionTF.setVisible(true);
        }
    }

    /**
     * Checks the clients country, changes region input to a choicebox of NZ regions if the country is
     * New Zealand, and changes to a textfield input for any other country
     */
    private void checkDeathCountry() {
        if (viewedClient.getCountryOfDeath() == Country.NZ) {
            deathRegionCB.setVisible(true);
            deathRegionTF.setVisible(false);
        } else {
            deathRegionCB.setVisible(false);
            deathRegionTF.setVisible(true);
        }
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
                checkCountry();
                checkDeathCountry();

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

        File selectedFile = fileChooser.showOpenDialog(AppUI.getWindow());
        if (selectedFile != null) {
            if (selectedFile.length() > maxFileSize) {
                PageNavigator.showAlert(AlertType.WARNING, "Image Size Too Large",
                        "The image size is too large. It must be under 2MB.");
            } else if (!selectedFile.canRead()) {
                PageNavigator.showAlert(AlertType.WARNING, "File Couldn't Be Read",
                        "This file could not be read. Ensure you are uploading a valid .png or .jpg");
            } else {
//                image = new Image(selectedFile.toURI().toString());
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
        if (fname.getText().isEmpty()) {
            legalNameLabel.setTextFill(Color.RED);
            update = false;
        } else {
            legalNameLabel.setTextFill(Color.BLACK);
        }

        if (lname.getText().isEmpty()) {
            legalNameLabel.setTextFill(Color.RED);
            update = false;
        } else {
            legalNameLabel.setTextFill(Color.BLACK);
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

        // DEATH DETAILS
        try {
            addChangeIfDifferent(modifyClientObject, viewedClient, "timeOfDeath",
                    LocalTime.parse(deathTimeField.getText()));

        } catch (DateTimeParseException e) {
            PageNavigator.showAlert(AlertType.WARNING, "Incorrect time format",
                    "Please enter the time of death"
                            + " in 'HH:mm:ss'. Time of death not saved.");
            return false;
        }
        addChangeIfDifferent(modifyClientObject, viewedClient, "dateOfDeath", deathDatePicker.getValue());
        addChangeIfDifferent(modifyClientObject, viewedClient, "countryOfDeath", deathCountry.getValue());

        addChangeIfDifferent(modifyClientObject, viewedClient, "cityOfDeath", deathCity.getText());

        if (country.getValue() == Country.NZ) {
            Region region = regionCB.getValue();
            if (region == null) {
                region = Region.UNSPECIFIED;
            }
            addChangeIfDifferent(modifyClientObject, viewedClient, "region", region.toString());
        } else {
            addChangeIfDifferent(modifyClientObject, viewedClient, "region", regionTF.getText());

        }

        if (deathCountry.getValue() == Country.NZ) {
            Region region = deathRegionCB.getValue();
            if (region == null) {
                region = Region.UNSPECIFIED;
            }
            addChangeIfDifferent(modifyClientObject, viewedClient, "regionOfDeath", region.toString());
        } else {
            addChangeIfDifferent(modifyClientObject, viewedClient, "regionOfDeath", deathRegionTF.getText());

        }

        checkCountry();
        checkDeathCountry();

        PageNavigator.refreshAllWindows();

        if (modifyClientObject.getModifiedFields().isEmpty()) {
            if (clientDied) { //only the client's date of death was changed
                return true;
            } else { //literally nothing was changed
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

    @FXML
    private void editDeathDetails() {

        MainController newMain  = PageNavigator.openNewWindow();
        newMain.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinicianViewClientWindow()
                .viewClient(viewedClient)
                .build());

        PageNavigator.loadPage(Page.EDIT_DEATH_DETAILS, newMain);
    }

}
