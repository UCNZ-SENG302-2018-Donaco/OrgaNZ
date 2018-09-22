package com.humanharvest.organz.controller.client;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import org.controlsfx.control.Notifications;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.controller.components.DurationUntilExpiryCell;
import com.humanharvest.organz.controller.components.ManualOverrideCell;
import com.humanharvest.organz.controller.components.TouchAlertTextController;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.PageNavigator;

/**
 * Controller for the register organs page.
 */
public class RegisterOrganDonationController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(RegisterOrganDonationController.class.getName());
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("d MMM yyyy hh:mm a");

    private final Map<Organ, CheckBox> organCheckBoxes = new HashMap<>();

    private Session session;
    private Client client;
    private Map<Organ, Boolean> donationStatus;
    @FXML
    private Pane sidebarPane, menuBarPane, registerPane, donatedOrgansPane;
    @FXML
    private CheckBox checkBoxLiver, checkBoxKidney, checkBoxPancreas, checkBoxHeart, checkBoxLung, checkBoxIntestine,
            checkBoxCornea, checkBoxMiddleEar, checkBoxSkin, checkBoxBone, checkBoxBoneMarrow, checkBoxConnTissue;
    @FXML
    private Label fullName;
    @FXML
    private TableView<DonatedOrgan> donatedOrgansTable;
    @FXML
    private TableColumn<DonatedOrgan, Organ> organCol;
    @FXML
    private TableColumn<DonatedOrgan, Duration> timeUntilExpiryCol;
    @FXML
    private TableColumn<DonatedOrgan, DonatedOrgan> manualOverrideCol;

    public RegisterOrganDonationController() {
        session = State.getSession();
    }

    /**
     * Handles the event when the user wants to cancel the override on a given donated organ.
     *
     * @param donatedOrgan The donated organ the user wants to cancel the override for.
     */
    private static void handleCancelOverride(DonatedOrgan donatedOrgan) {
        try {
            State.getClientResolver().cancelManualOverrideForOrgan(donatedOrgan);
            PageNavigator.refreshAllWindows();
        } catch (IfMatchFailedException exc) {
            // TODO deal with outdated error
        } catch (NotFoundException exc) {
            LOGGER.log(Level.WARNING, "Client/Organ Not Found", exc);
            Notifications.create()
                    .title("Client/Organ Not Found")
                    .text("The client/donated organ could not be found on the server; it may have been deleted.")
                    .showWarning();
        } catch (ServerRestException exc) {
            LOGGER.log(Level.WARNING, exc.getMessage(), exc);
            Notifications.create()
                    .title("Server Error")
                    .text("A server error occurred when cancelling the override on this donated organ; please try "
                            + "again later.")
                    .showError();
        }
    }

    private static String formatChange(Organ organ, boolean newValue) {
        if (newValue) {
            return String.format("Registered %s for donation.", organ.toString());
        } else {
            return String.format("Deregistered %s for donation.", organ.toString());
        }
    }

    /**
     * Initializes the UI for this page.
     * - Loads the sidebar.
     * - Adds all checkboxes with their respective Organ to the organCheckBoxes map.
     */
    @FXML
    private void initialize() {
        organCheckBoxes.put(Organ.LIVER, checkBoxLiver);
        organCheckBoxes.put(Organ.KIDNEY, checkBoxKidney);
        organCheckBoxes.put(Organ.PANCREAS, checkBoxPancreas);
        organCheckBoxes.put(Organ.HEART, checkBoxHeart);
        organCheckBoxes.put(Organ.LUNG, checkBoxLung);
        organCheckBoxes.put(Organ.INTESTINE, checkBoxIntestine);
        organCheckBoxes.put(Organ.CORNEA, checkBoxCornea);
        organCheckBoxes.put(Organ.MIDDLE_EAR, checkBoxMiddleEar);
        organCheckBoxes.put(Organ.SKIN, checkBoxSkin);
        organCheckBoxes.put(Organ.BONE, checkBoxBone);
        organCheckBoxes.put(Organ.BONE_MARROW, checkBoxBoneMarrow);
        organCheckBoxes.put(Organ.CONNECTIVE_TISSUE, checkBoxConnTissue);
        initTable();
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);

        if (session.getLoggedInUserType() == Session.UserType.CLIENT) {
            client = session.getLoggedInClient();
            mainController.loadSidebar(sidebarPane);
            donatedOrgansPane.setVisible(false);
        } else if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
            mainController.loadTouchActionsBar(menuBarPane);
            donatedOrgansPane.setVisible(true);
        }
        refresh();
    }

    private void initTable() {
        // Setup the cell factories
        organCol.setCellValueFactory(new PropertyValueFactory<>("organType"));
        timeUntilExpiryCol.setCellValueFactory(new PropertyValueFactory<>("durationUntilExpiry"));
        timeUntilExpiryCol.setCellFactory(DurationUntilExpiryCell::new);
        manualOverrideCol.setCellValueFactory(row -> new SimpleObjectProperty<>(row.getValue()));
        manualOverrideCol.setCellFactory(column -> new ManualOverrideCell(column,
                this::handleOverride,
                this::handleEditOverride,
                RegisterOrganDonationController::handleCancelOverride));

        // Sets the comparator for sorting by time until expiry column.
        timeUntilExpiryCol.setComparator((dur1, dur2) -> {
            if (Objects.equals(dur1, dur2)) {
                return 0;
            } else if (dur1 == null) {
                return 1;  // nulls are considered "bigger" than actual durations
            } else if (dur2 == null) {
                return -1;
            } else {
                return dur1.compareTo(dur2);
            }
        });

        // Sort by time until expiry column by default.
        donatedOrgansTable.getSortOrder().clear();
        donatedOrgansTable.getSortOrder().add(timeUntilExpiryCol);
    }

    /**
     * Handles the event when the user wants to override a given donated organ. Creates a popup with a text field for
     * the user to enter the reason they are overriding this organ.
     *
     * @param donatedOrgan The donated organ the user wants to override.
     */
    private void handleOverride(DonatedOrgan donatedOrgan) {
        // Create a popup with a text field to enter the reason

        TouchAlertTextController controller = PageNavigator.showTextAlert("Manually Override Organ",
                "Enter the reason for overriding this organ:", mainController.getStage());

        if (controller.getResultProperty().getValue() != null) {
            if (controller.getResultProperty().getValue()) {
                overrideOrgan(controller.getText(), donatedOrgan);
            }
        } else {
            controller.getResultProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    overrideOrgan(controller.getText(), donatedOrgan);
                }
            });
        }
    }

    private void overrideOrgan(String response, DonatedOrgan donatedOrgan) {
        try {
            StringBuilder overrideReason = new StringBuilder(response);
            overrideReason.append("\n").append(LocalDateTime.now().format(dateTimeFormat));
            if (session.getLoggedInUserType() == UserType.CLINICIAN) {
                overrideReason.append(String.format("%nOverriden by clinician %d (%s)",
                        session.getLoggedInClinician().getStaffId(), session.getLoggedInClinician().getFullName()));
            } else if (session.getLoggedInUserType() == UserType.ADMINISTRATOR) {
                overrideReason.append(String.format("%nOverriden by admin '%s'.",
                        session.getLoggedInAdministrator().getUsername()));
            }
            State.getClientResolver().manuallyOverrideOrgan(donatedOrgan, overrideReason.toString());
            PageNavigator.refreshAllWindows();
        } catch (IfMatchFailedException exc) {
            // TODO deal with outdated error
        } catch (NotFoundException exc) {
            LOGGER.log(Level.WARNING, "Client/Organ Not Found", exc);
            Notifications.create()
                    .title("Client/Organ Not Found")
                    .text("The client/donated organ could not be found on the server; it may have been deleted.")
                    .showWarning();
        } catch (ServerRestException exc) {
            LOGGER.log(Level.WARNING, exc.getMessage(), exc);
            Notifications.create()
                    .title("Server Error")
                    .text("A server error occurred when overriding this donated organ; please try again later.")
                    .showError();
        }
    }

    /**
     * Handles the event when the user wants to edit the override on a given donated organ. Creates a popup with a text
     * field for the user to modify the override reason on this organ.
     *
     * @param donatedOrgan The donated organ the user wants to edit the override for.
     */
    private void handleEditOverride(DonatedOrgan donatedOrgan) {

        TouchAlertTextController controller = PageNavigator.showTextAlert("Edit Manual Override",
                "Enter the reason for overriding this organ:", mainController.getStage());

        if (controller.getResultProperty().getValue() != null) {
            if (controller.getResultProperty().getValue()) {
                editOverride(controller.getText(), donatedOrgan);
            }
        } else {
            controller.getResultProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    editOverride(controller.getText(), donatedOrgan);
                }
            });
        }
    }

    private void editOverride(String response, DonatedOrgan donatedOrgan) {
        try {
            StringBuilder overrideReason = new StringBuilder(response);
            overrideReason.append("\n").append(LocalDateTime.now().format(dateTimeFormat));
            if (session.getLoggedInUserType() == UserType.CLINICIAN) {
                overrideReason.append(String.format("%nOverriden by clinician %d (%s)",
                        session.getLoggedInClinician().getStaffId(), session.getLoggedInClinician().getFullName()));
            } else if (session.getLoggedInUserType() == UserType.ADMINISTRATOR) {
                overrideReason.append(String.format("%nOverriden by admin '%s'.",
                        session.getLoggedInAdministrator().getUsername()));
            }
            State.getClientResolver().editManualOverrideForOrgan(donatedOrgan, overrideReason.toString());
            PageNavigator.refreshAllWindows();
        } catch (IfMatchFailedException exc) {
            // TODO deal with outdated error
        } catch (NotFoundException exc) {
            LOGGER.log(Level.WARNING, "Client/Organ Not Found", exc);
            Notifications.create()
                    .title("Client/Organ Not Found")
                    .text("The client/donated organ could not be found on the server; it may have been deleted.")
                    .showWarning();
        } catch (ServerRestException exc) {
            LOGGER.log(Level.WARNING, exc.getMessage(), exc);
            Notifications.create()
                    .title("Server Error")
                    .text("A server error occurred when overriding this donated organ; please try again later.")
                    .showError();
        }
    }

    @Override
    public void refresh() {
        // Retrieve organ donation status and transplant requests from the server
        try {
            client.setTransplantRequests(State.getClientResolver().getTransplantRequests(client));
            client.setOrganDonationStatus(State.getClientResolver().getOrganDonationStatus(client));
            client.setDonatedOrgans(State.getClientResolver().getDonatedOrgans(client));
        } catch (NotFoundException e) {
            LOGGER.log(Level.WARNING, "Client Not Found", e);
            Notifications.create()
                    .title("Client Not Found")
                    .text("The client could not be found on the server; it may have been deleted.")
                    .showWarning();
            return;
        } catch (ServerRestException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            Notifications.create()
                    .title("Server Error")
                    .text("A server error occurred when retrieving the client; please try again later.")
                    .showError();
            return;
        }

        List<TransplantRequest> transplantRequests = client.getTransplantRequests();
        donationStatus = client.getOrganDonationStatus();

        // Determine which organs are/have been involved in transplant requests by this client
        EnumSet<Organ> allPreviouslyRequestedOrgans = transplantRequests
                .stream()
                .map(TransplantRequest::getRequestedOrgan)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Organ.class)));

        // Set the checkbox as ticked if the client is registered to donate an organ
        for (Map.Entry<Organ, CheckBox> entry : organCheckBoxes.entrySet()) {
            entry.getValue().setSelected(donationStatus.get(entry.getKey()));
            // Highlight & add tooltip if this organ is/has been involved in a transplant request by this client
            if (allPreviouslyRequestedOrgans.contains(entry.getKey())) {
                entry.getValue().setStyle("-fx-color: lightcoral;");
                entry.getValue().setTooltip(new Tooltip("This organ was/is part of a transplant request."));
            } else {
                entry.getValue().setStyle(null);
                entry.getValue().setTooltip(null);
            }
        }

        // Set appropriate name on window title and name label
        String name = "";
        if (session.getLoggedInUserType() == UserType.CLIENT) {
            mainController.setTitle("Donate  Organs: " + client.getPreferredNameFormatted());
            name = client.getPreferredNameFormatted();
        } else if (windowContext.isClinViewClientWindow()) {
            mainController.setTitle("Donate Organs: " + client.getFullName());
            name = client.getFullName();
            donatedOrgansTable.setItems(
                    FXCollections.observableArrayList(client.getDonatedOrgans())
                            .sorted(donatedOrgansTable.getComparator()));
        }
        if (client.isDead()) {
            name += " (died at " + client.getDatetimeOfDeath().format(dateTimeFormat) + ")";
        }
        fullName.setText(name);

        // Disable organ donation registration pane if client is dead
        if (client.isDead()) {
            registerPane.setDisable(true);
        }
    }

    /**
     * Checks which organs check boxes have been changed, and applies those changes with a ModifyClientOrgansAction.
     */
    @FXML
    private void apply() {
        Map<Organ, Boolean> changes = new HashMap<>();
        boolean hasChanged = false;

        // Determine if any organ registration checkboxes have been changed
        for (Organ organ : organCheckBoxes.keySet()) {
            boolean oldStatus = donationStatus.get(organ);
            boolean newStatus = organCheckBoxes.get(organ).isSelected();

            if (oldStatus != newStatus) {
                changes.put(organ, newStatus);
                hasChanged = true;
            }
        }
        if (hasChanged) {
            try {
                State.getClientResolver().modifyOrganDonation(client, changes);
            } catch (NotFoundException e) {
                LOGGER.log(Level.WARNING, "Client Not Found", e);
                Notifications.create()
                        .title("Client Not Found")
                        .text("The client could not be found on the server; it may have been deleted.")
                        .showWarning();
                return;
            } catch (ServerRestException e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
                Notifications.create()
                        .title("Server Error")
                        .text("A server error occurred when applying changes; please try again later.")
                        .showError();
                return;
            } catch (IfMatchFailedException e) {
                LOGGER.log(Level.INFO, "If-Match did not match", e);
                PageNavigator.showAlert(AlertType.WARNING,
                        "Outdated Data",
                        "The client has been modified since you retrieved the data. "
                                + "\nThe user data will now be refreshed.", mainController.getStage());
                refresh();
            }

            PageNavigator.refreshAllWindows();
            Notifications.create()
                    .title("Updated organs registered for donation")
                    .text(getChangesText(changes))
                    .showInformation();
        } else {
            Notifications.create()
                    .title("No changes made")
                    .text("No changes were made to the client's organ status.")
                    .showWarning();
        }
    }

    private String getChangesText(Map<Organ, Boolean> changes) {
        String changesText = changes.entrySet().stream()
                .map(entry -> formatChange(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));

        return String.format("Changed organ donation registration for client %d: %s:%n%n%s",
                client.getUid(), client.getFullName(), changesText);
    }

    /**
     * Selects all organ checkboxes.
     */
    @FXML
    private void selectAll() {
        for (CheckBox checkBox : organCheckBoxes.values()) {
            checkBox.setSelected(true);
        }
    }

    /**
     * Deselects all organ checkboxes.
     */
    @FXML
    private void selectNone() {
        for (CheckBox checkBox : organCheckBoxes.values()) {
            checkBox.setSelected(false);
        }
    }

    /**
     * Resets the page back to its default state.
     */
    @FXML
    private void cancel() {
        refresh();
    }
}
