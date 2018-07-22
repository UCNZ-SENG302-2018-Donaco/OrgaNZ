package com.humanharvest.organz.controller.client;

import static com.humanharvest.organz.utilities.enums.TransplantRequestStatus.WAITING;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;
import com.humanharvest.organz.utilities.enums.ResolveReason;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.views.client.CreateTransplantRequestView;
import com.humanharvest.organz.views.client.ResolveTransplantRequestView;

/**
 * Controller for the Request Organs page. Handles the viewing of current and past organ transplant requests. If the
 * logged in user is a clinician, they also have the ability to create new transplant requests and resolve current ones.
 */
public class RequestOrgansController extends SubController {

    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("d MMM yyyy hh:mm a");
    private static final Logger LOGGER = Logger.getLogger(CreateClientController.class.getName());

    private Session session;
    private ActionInvoker invoker;
    private ClientManager manager;
    private Client client;

    private Collection<TransplantRequest> allRequests;
    private FilteredList<TransplantRequest> currentRequests;
    private FilteredList<TransplantRequest> pastRequests;

    @FXML
    private Pane sidebarPane, menuBarPane;
    @FXML
    private HBox newRequestForm, resolveRequestBar;
    @FXML
    private ChoiceBox<Organ> newOrganChoiceBox;
    @FXML
    private TableView<TransplantRequest> currentRequestsTable, pastRequestsTable;
    @FXML
    private TableColumn<TransplantRequest, Organ> organCurrCol, organPastCol;
    @FXML
    private TableColumn<TransplantRequest, LocalDateTime> requestDateCurrCol, requestDatePastCol, resolvedDatePastCol;
    @FXML
    private TableColumn<TransplantRequest, TransplantRequestStatus> requestStatusPastCol;
    @FXML
    private TableColumn<TransplantRequest, String> resolvedReasonPastCol;
    @FXML
    private ComboBox<ResolveReason> cancelTransplantOptions;
    @FXML
    private TextField customReason;
    @FXML
    private DatePicker deathDatePicker;

    /**
     * Formats a table cell that holds a {@link LocalDateTime} value to display that value in the date time format.
     * @return The cell with the date time formatter set.
     */
    private static TableCell<TransplantRequest, LocalDateTime> formatDateTimeCell() {
        return new TableCell<TransplantRequest, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || (item == null)) {
                    setText(null);
                } else {
                    setText(item.format(dateTimeFormat));
                }
            }
        };
    }

    /**
     * Formats a table row to be coloured if the {@link TransplantRequest} it holds is for an organ that the client
     * is also donating.
     * @return The row with the colouring callback set.
     */
    private TableRow<TransplantRequest> colourIfDonatedAndRequested() {
        return new TableRow<TransplantRequest>() {
            @Override
            protected void updateItem(TransplantRequest request, boolean empty) {
                super.updateItem(request, empty);
                if (empty || request == null) {
                    setStyle(null);
                    setTooltip(null);
                } else if (client.getOrganDonationStatus().get(request.getRequestedOrgan())) {
                    setStyle("-fx-background-color: lightcoral");
                    setTooltip(new Tooltip("This organ is currently set for donation."));
                } else {
                    setStyle(null);
                    setTooltip(null);
                }
            }
        };
    }

    /**
     * Creates a new controller for this page, getting the current session and invoker from the global state.
     */
    public RequestOrgansController() {
        session = State.getSession();
        invoker = State.getInvoker();
        manager = State.getClientManager();
    }

    /**
     * Sets up the two tables.
     */
    @FXML
    private void initialize() {
        // Populate organ and resolve boxes with all values
        newOrganChoiceBox.setItems(FXCollections.observableArrayList(Organ.values()));
        cancelTransplantOptions.setItems(FXCollections.observableArrayList(ResolveReason.values()));
        cancelTransplantOptions.setValue(ResolveReason.ERROR);

        customReason.setManaged(false);
        deathDatePicker.setManaged(false);
        deathDatePicker.setValue(LocalDate.now());

        // Setup all cell value factories
        organCurrCol.setCellValueFactory(new PropertyValueFactory<>("requestedOrgan"));
        requestDateCurrCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        organPastCol.setCellValueFactory(new PropertyValueFactory<>("requestedOrgan"));
        requestDatePastCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        requestStatusPastCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        resolvedDatePastCol.setCellValueFactory(new PropertyValueFactory<>("resolvedDate"));
        resolvedReasonPastCol.setCellValueFactory(new PropertyValueFactory<>("resolvedReason"));

        // Format all the datetime cells
        requestDateCurrCol.setCellFactory(cell -> formatDateTimeCell());
        requestDatePastCol.setCellFactory(cell -> formatDateTimeCell());
        resolvedDatePastCol.setCellFactory(cell -> formatDateTimeCell());

        // Colour each row if it is a request for an organ that the client is also registered to donate.
        currentRequestsTable.setRowFactory(row -> colourIfDonatedAndRequested());
        pastRequestsTable.setRowFactory(row -> colourIfDonatedAndRequested());

        // Add listeners to clear the other table when anything is selected in each table (and enable/disable buttons).
        currentRequestsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable) -> enableAppropriateButtons());
        currentRequestsTable.setOnMouseClicked(
                (observable) -> pastRequestsTable.getSelectionModel().clearSelection());
        pastRequestsTable.setOnMouseClicked(
                (observable) -> currentRequestsTable.getSelectionModel().clearSelection());

        cancelTransplantOptions.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    customReason.setManaged(false);
                    customReason.setVisible(false);
                    deathDatePicker.setManaged(false);
                    deathDatePicker.setVisible(false);

                    if (newValue == ResolveReason.CUSTOM) {
                        customReason.setManaged(true);
                        customReason.setVisible(true);
                    } else if (newValue == ResolveReason.DECEASED) {
                        deathDatePicker.setManaged(true);
                        deathDatePicker.setVisible(true);
                    }
                }
        );
    }

    /**
     * Loads the sidebar, hides sections of the page according to which type of user is logged in, and sets the
     * window's title.
     * @param mainController The main controller that defines which window this subcontroller belongs to.
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);

        if (session.getLoggedInUserType() == Session.UserType.CLIENT) {
            client = session.getLoggedInClient();
            newRequestForm.setManaged(false);
            newRequestForm.setVisible(false);
            resolveRequestBar.setManaged(false);
            resolveRequestBar.setVisible(false);
            mainController.loadSidebar(sidebarPane);
        } else if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
            mainController.loadMenuBar(menuBarPane);
        }

        refresh();
        enableAppropriateButtons();
    }

    /**
     * Refreshes the contents of the two tables based on the client's current transplant requests.
     */
    @Override
    public void refresh() {
        allRequests = client.getTransplantRequests();

        currentRequests = new FilteredList<>(
                FXCollections.observableArrayList(allRequests),
                request -> request.getStatus() == WAITING
        );
        pastRequests = new FilteredList<>(
                FXCollections.observableArrayList(allRequests),
                request -> request.getStatus() != WAITING
        );

        currentRequestsTable.setItems(currentRequests);
        pastRequestsTable.setItems(pastRequests);

        if (session.getLoggedInUserType() == UserType.CLIENT) {
            mainController.setTitle("Request Organs: " + client.getPreferredName());
        } else if (windowContext.isClinViewClientWindow()) {
            mainController.setTitle("Request Organs: " + client.getFullName());

        }
    }

    private void enableAppropriateButtons() {
        if (windowContext.isClinViewClientWindow()) {
            if (currentRequestsTable.getSelectionModel().getSelectedItem() == null) {
                resolveRequestBar.setDisable(true);
            } else {
                resolveRequestBar.setDisable(false);
            }
        }
    }

    /**
     * Creates a new transplant request for this client based on the contents of the organ choice box.
     * If there is an error (bad organ selection), then it will show an alert.
     * Otherwise, it will:
     * - Create a request
     * - Send it to the server (if the server returns an error, it will alert the user and return)
     * - Update the client's list of transplant requests based on the server's response
     * - Refresh the page
     */
    @FXML
    private void submitNewRequest() {
        Organ selectedOrgan = newOrganChoiceBox.getValue();
        if (selectedOrgan == null) { // Haven't selected an organ
            PageNavigator.showAlert(
                    AlertType.ERROR,
                    "Select an organ",
                    "You must select an organ to make a transplant request for.");
        } else if (client.getCurrentlyRequestedOrgans().contains(selectedOrgan)) { // Already requested organ
            PageNavigator.showAlert(
                    AlertType.ERROR,
                    "Request already exists",
                    "Client already has a waiting request for this organ.");
        } else { // Bluesky scenario
            // Create a request
            CreateTransplantRequestView newRequest =
                    new CreateTransplantRequestView(client, selectedOrgan, LocalDateTime.now());

            // Resolve the request
            List<TransplantRequest> updatedTransplantRequests;
            try {
                updatedTransplantRequests = State.getClientResolver().createTransplantRequest(client, newRequest);
            } catch (ServerRestException e) { //500
                LOGGER.severe(e.getMessage());
                PageNavigator.showAlert(AlertType.ERROR,
                        "Server Error",
                        "An error occurred on the server while trying to create the transplant request.\n"
                                + "Please try again later.");
                return;
            } catch (IfMatchFailedException e) { //412
                PageNavigator.showAlert(
                        AlertType.WARNING,
                        "Outdated Data",
                        "The client has been modified since you retrieved the data.\n"
                                + "If you would still like to apply these changes please submit again, "
                                + "otherwise refresh the page to update the data.");
                return;
            } catch (NotFoundException e) { //404
                LOGGER.log(Level.WARNING, "Client not found");
                PageNavigator.showAlert(AlertType.WARNING, "Client not found", "The client could not be found on the "
                        + "server, it may have been deleted");
                return;
            }
            // Not caught, as they should not happen:
            // 401 - Access token is missing or invalid
            // 403 - You do not have permission to perform that action
            // 428 - ETag header was missing and is required to modify a resource

            // Update the client's transplant requests based on the server's response
            client.setTransplantRequests(updatedTransplantRequests);

            // Refresh the page
            PageNavigator.refreshAllWindows();
        }
    }

    /**
     * Resolves the selected transplant request in the current requests table, by either cancelling or completing the
     * request depending on the reason given in the input pane.
     */
    @FXML
    private void resolveRequest() {
        TransplantRequest selectedRequest = currentRequestsTable.getSelectionModel().getSelectedItem();

        if (selectedRequest != null) {

            // Create a request

            // Get data from existing request
            Organ requestedOrgan = selectedRequest.getRequestedOrgan();
            LocalDateTime requestDate = selectedRequest.getRequestDate();

            // Get resolved reason and the request's new status
            ResolveReason resolvedReasonDropdownChoice = cancelTransplantOptions.getValue();
            String resolvedReason;
            TransplantRequestStatus status;

            switch (resolvedReasonDropdownChoice) {
                case COMPLETED:  // "Transplant completed"
                    resolvedReason = "Transplant took place.";
                    status = TransplantRequestStatus.COMPLETED;
                    break;
                case DECEASED:  // "Client is deceased"
                    // A datepicker appears, for choosing the date of death
                    LocalDate deathDate = deathDatePicker.getValue();
                    if (deathDate.isBefore(client.getDateOfBirth()) || deathDate.isAfter(LocalDate.now())) {
                        PageNavigator.showAlert(AlertType.ERROR,
                                "Date of Death Invalid",
                                "Date of death must be between client's birth date and the current date.");
                    } else { // valid date of death
                        Optional<ButtonType> buttonOpt = PageNavigator.showAlert(AlertType.CONFIRMATION,
                                "Are you sure you want to mark this client as dead?",
                                "This will cancel all waiting transplant requests for this client.");
                        if (buttonOpt.isPresent() && buttonOpt.get() == ButtonType.OK) {
                            // todo send a request to the server to mark the client as dead
                            // todo this should result in a `MarkClientAsDeadAction` using `client` and `deathDate`
                        }
                        if (buttonOpt.isPresent()) { // if they chose OK or Cancel
                            deathDatePicker.setValue(LocalDate.now()); //reset datepicker
                        }
                    }
                    return;
                case CURED:  // "Disease was cured"
                    resolvedReason = "The disease was cured.";
                    status = TransplantRequestStatus.CANCELLED;
                    break;
                case ERROR:  // "Input error"
                    resolvedReason = "Request was a mistake.";
                    status = TransplantRequestStatus.CANCELLED;
                    break;
                case CUSTOM:  // "Custom reason..."
                    resolvedReason = customReason.getText();
                    status = TransplantRequestStatus.CANCELLED;
                    customReason.clear();
                    break;
                default:
                    throw new UnsupportedOperationException("Transplant request status that wasn't covered in if-else "
                            + "statements.");
            }

            ResolveTransplantRequestView request = new ResolveTransplantRequestView(client, requestedOrgan, requestDate,
                    LocalDateTime.now(), status, resolvedReason);

            // Resolve the request
            // TODO: Should we really use the index?
            int transplantRequestIndex = client.getTransplantRequests().indexOf(selectedRequest);
            TransplantRequest updatedTransplantRequest;
            try {
                updatedTransplantRequest = State.getClientResolver().resolveTransplantRequest(
                        client,
                        request,
                        transplantRequestIndex);
            } catch (ServerRestException e) { //500
                LOGGER.severe(e.getMessage());
                PageNavigator.showAlert(AlertType.ERROR,
                        "Server Error",
                        "An error occurred on the server while trying to create the transplant request.\n"
                                + "Please try again later.");
                return;
            } catch (IfMatchFailedException e) { //412
                PageNavigator.showAlert(
                        AlertType.WARNING,
                        "Outdated Data",
                        "The client has been modified since you retrieved the data.\n"
                                + "If you would still like to apply these changes please submit again, "
                                + "otherwise refresh the page to update the data.");
                return;
            } catch (NotFoundException e) { //404
                LOGGER.log(Level.WARNING, "Client not found");
                PageNavigator.showAlert(AlertType.WARNING, "Client not found", "The client could not be found on the "
                        + "server, it may have been deleted");
                return;
            }
            // Not caught, as they should not happen:
            // 401 - Access token is missing or invalid
            // 403 - You do not have permission to perform that action
            // 428 - ETag header was missing and is required to modify a resource

            // Update the client's transplant request based on the server's response
            client.getTransplantRequests().remove(transplantRequestIndex);
            client.getTransplantRequests().add(updatedTransplantRequest);

            // Refresh the page
            PageNavigator.refreshAllWindows();

            // Offer to go to medical history page if they said a disease was cured
            if (resolvedReasonDropdownChoice == ResolveReason.CURED) { // "Disease was cured"
                Optional<ButtonType> buttonOpt = PageNavigator.showAlert(AlertType.CONFIRMATION,
                        "Go to Medical History Page",
                        "Do you want to go to the medical history page to mark the disease that was cured?");
                if (buttonOpt.isPresent() && buttonOpt.get() == ButtonType.OK) {
                    PageNavigator.loadPage(Page.VIEW_MEDICAL_HISTORY, mainController);
                }
            }
        }
    }

    /**
     * Returns to the view client details page.
     */
    @FXML
    private void returnToViewClient() {
        PageNavigator.loadPage(Page.VIEW_CLIENT, mainController);
    }
}
