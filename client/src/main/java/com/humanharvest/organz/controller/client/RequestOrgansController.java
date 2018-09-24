package com.humanharvest.organz.controller.client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.controller.AlertHelper;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.resolvers.client.ClientResolver;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.ResolveReason;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext;
import com.humanharvest.organz.views.client.ResolveTransplantRequestObject;

/**
 * Controller for the Request Organs page. Handles the viewing of current and past organ transplant requests. If the
 * logged in user is a clinician, they also have the ability to create new transplant requests and resolve current ones.
 */
public class RequestOrgansController extends SubController {

    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("d MMM yyyy hh:mm a");
    private static final Logger LOGGER = Logger.getLogger(RequestOrgansController.class.getName());

    private final Session session;
    private final ClientResolver resolver;
    private Client client;

    @FXML
    private Pane sidebarPane;
    @FXML
    private Pane menuBarPane;
    @FXML
    private HBox newRequestForm;
    @FXML
    private HBox resolveRequestBar;
    @FXML
    private ChoiceBox<Organ> newOrganChoiceBox;
    @FXML
    private TableView<TransplantRequest> currentRequestsTable;
    @FXML
    private TableView<TransplantRequest> pastRequestsTable;
    @FXML
    private TableColumn<TransplantRequest, Organ> organCurrCol;
    @FXML
    private TableColumn<TransplantRequest, Organ> organPastCol;
    @FXML
    private TableColumn<TransplantRequest, LocalDateTime> requestDateTimeCurrCol;
    @FXML
    private TableColumn<TransplantRequest, LocalDateTime> requestDateTimePastCol;
    @FXML
    private TableColumn<TransplantRequest, LocalDateTime> resolvedDateTimePastCol;
    @FXML
    private TableColumn<TransplantRequest, TransplantRequestStatus> requestStatusPastCol;
    @FXML
    private TableColumn<TransplantRequest, String> resolvedReasonPastCol;
    @FXML
    private ComboBox<ResolveReason> cancelTransplantOptions;
    @FXML
    private TextField customReason;

    /**
     * Creates a new controller for this page, getting the current session and invoker from the global state.
     */
    public RequestOrgansController() {
        session = State.getSession();
        resolver = State.getClientResolver();
    }

    /**
     * Formats a table cell that holds a {@link LocalDateTime} value to display that value in the date time format.
     *
     * @return The cell with the date time formatter set.
     */
    private static TableCell<TransplantRequest, LocalDateTime> formatDateTimeCell() {
        return new TableCell<TransplantRequest, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
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
     *
     * @return The row with the colouring callback set.
     */
    private TableRow<TransplantRequest> colourIfDonatedAndRequested() {
        return new TableRow<TransplantRequest>() {
            @Override
            protected void updateItem(TransplantRequest item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle(null);
                    setTooltip(null);
                } else if (client.getOrganDonationStatus().get(item.getRequestedOrgan())) {
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
     * Sets up the two tables.
     */
    @FXML
    private void initialize() {
        // Populate organ and resolve boxes with all values
        newOrganChoiceBox.setItems(FXCollections.observableArrayList(Organ.values()));
        cancelTransplantOptions.setItems(FXCollections.observableArrayList(ResolveReason.values()));
        cancelTransplantOptions.setValue(ResolveReason.ERROR);

        customReason.setManaged(false);

        // Setup all cell value factories
        organCurrCol.setCellValueFactory(new PropertyValueFactory<>("requestedOrgan"));
        requestDateTimeCurrCol.setCellValueFactory(new PropertyValueFactory<>("requestDateTime"));
        organPastCol.setCellValueFactory(new PropertyValueFactory<>("requestedOrgan"));
        requestDateTimePastCol.setCellValueFactory(new PropertyValueFactory<>("requestDateTime"));
        requestStatusPastCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        resolvedDateTimePastCol.setCellValueFactory(new PropertyValueFactory<>("resolvedDateTime"));
        resolvedReasonPastCol.setCellValueFactory(new PropertyValueFactory<>("resolvedReason"));

        // Format all the datetime cells
        requestDateTimeCurrCol.setCellFactory(cell -> formatDateTimeCell());
        requestDateTimePastCol.setCellFactory(cell -> formatDateTimeCell());
        resolvedDateTimePastCol.setCellFactory(cell -> formatDateTimeCell());

        // Colour each row if it is a request for an organ that the client is also registered to donate.
        currentRequestsTable.setRowFactory(row -> colourIfDonatedAndRequested());
        pastRequestsTable.setRowFactory(row -> colourIfDonatedAndRequested());

        // Add listeners to clear the other table when anything is selected in each table (and enable/disable buttons).
        currentRequestsTable.getSelectionModel().selectedItemProperty().addListener(
                observable -> enableAppropriateButtons());
        currentRequestsTable.setOnMouseClicked(
                observable -> pastRequestsTable.getSelectionModel().clearSelection());
        pastRequestsTable.setOnMouseClicked(
                observable -> currentRequestsTable.getSelectionModel().clearSelection());

        cancelTransplantOptions.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    customReason.setManaged(false);
                    customReason.setVisible(false);

                    if (newValue == ResolveReason.CUSTOM) {
                        customReason.setManaged(true);
                        customReason.setVisible(true);
                    }
                }
        );
    }

    /**
     * Loads the sidebar, hides sections of the page according to which type of user is logged in, and sets the
     * window's title.
     *
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
        // Reload the client's transplant requests
        Collection<TransplantRequest> allRequests;
        try {
            allRequests = resolver.getTransplantRequests(client);
        } catch (NotFoundException e) {
            LOGGER.log(Level.WARNING, "Client not found", e);
            PageNavigator.showAlert(AlertType.ERROR,
                    "Client not found",
                    "The client could not be found on the server, it may have been deleted", mainController.getStage());
            return;
        } catch (ServerRestException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            PageNavigator.showAlert(AlertType.ERROR,
                    "Server error",
                    "Could not apply changes on the server, please try again later", mainController.getStage());
            return;
        }

        ObservableList<TransplantRequest> currentRequests = new FilteredList<>(
                FXCollections.observableArrayList(allRequests),
                request -> request.getStatus() == TransplantRequestStatus.WAITING
        );
        ObservableList<TransplantRequest> pastRequests = new FilteredList<>(
                FXCollections.observableArrayList(allRequests),
                request -> request.getStatus() != TransplantRequestStatus.WAITING
        );

        currentRequestsTable.setItems(currentRequests);
        pastRequestsTable.setItems(pastRequests);

        if (session.getLoggedInUserType() == UserType.CLIENT) {
            mainController.setTitle("Request Organs: " + client.getPreferredNameFormatted());
        } else if (windowContext.isClinViewClientWindow()) {
            mainController.setTitle("Request Organs: " + client.getFullName());

        }
    }

    private void enableAppropriateButtons() {
        // The "Resolve Request" button
        if (windowContext.isClinViewClientWindow()
                && currentRequestsTable.getSelectionModel().getSelectedItem() != null) {
            resolveRequestBar.setDisable(false);
        } else {
            resolveRequestBar.setDisable(true);
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
                    "You must select an organ to make a transplant request for.", mainController.getStage());
        } else if (client.getCurrentlyRequestedOrgans().contains(selectedOrgan)) { // Already requested organ
            PageNavigator.showAlert(
                    AlertType.ERROR,
                    "Request already exists",
                    "Client already has a waiting request for this organ.", mainController.getStage());
        } else if (client.getTransplantRequests().stream()
                .anyMatch(request -> request.getRequestedOrgan() == selectedOrgan &&
                        request.getStatus() == TransplantRequestStatus.SCHEDULED)) {
            PageNavigator.showAlert(
                    AlertType.ERROR,
                    "Transplant already scheduled",
                    "Client already has a scheduled transplant for this organ.", mainController.getStage());
        } else if (client.isDead()) { // Client is dead, they can't request an organ
            PageNavigator.showAlert(
                    AlertType.ERROR,
                    "Client is dead",
                    "Client is marked as dead, so can't request an organ transplant.", mainController.getStage());
        } else { // Bluesky scenario
            // Create a request
            TransplantRequest newRequest = new TransplantRequest(client, selectedOrgan, LocalDateTime.now());

            // Resolve the request
            try {
                resolver.createTransplantRequest(client, newRequest);
            } catch (ServerRestException e) { //500
                AlertHelper.showRestAlert(LOGGER, e, mainController);
                return;
            } catch (IfMatchFailedException e) { //412
                AlertHelper.showIfMatchAlert(LOGGER, e, mainController);
                return;
            } catch (NotFoundException e) { //404
                AlertHelper.showNotFoundAlert(LOGGER, e, mainController);
                return;
            }
            // Not caught, as they should not happen:
            // 401 - Access token is missing or invalid
            // 403 - You do not have permission to perform that action
            // 428 - ETag header was missing and is required to modify a resource

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

            // Get resolved reason and the request's new status
            ResolveReason resolvedReasonDropdownChoice = cancelTransplantOptions.getValue();
            String resolvedReason;
            TransplantRequestStatus status;

            switch (resolvedReasonDropdownChoice) {
                case COMPLETED:  // "Transplant completed"
                    resolvedReason = "Transplant took place.";
                    status = TransplantRequestStatus.COMPLETED;
                    break;
                case DECEASED:
                    resolveDeceasedRequest();
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

            ResolveTransplantRequestObject request = new ResolveTransplantRequestObject(LocalDateTime.now(), status,
                    resolvedReason);

            try {
                resolver.resolveTransplantRequest(
                        client,
                        selectedRequest,
                        request);
            } catch (ServerRestException e) { //500
                AlertHelper.showRestAlert(LOGGER, e, mainController);
                return;
            } catch (IfMatchFailedException e) { //412
                AlertHelper.showIfMatchAlert(LOGGER, e, mainController);
                return;
            } catch (NotFoundException e) { //404
                AlertHelper.showNotFoundAlert(LOGGER, e, mainController);
                return;
            }
            // Not caught, as they should not happen:
            // 401 - Access token is missing or invalid
            // 403 - You do not have permission to perform that action
            // 428 - ETag header was missing and is required to modify a resource

            // Refresh the page
            PageNavigator.refreshAllWindows();

            // Offer to go to medical history page if they said a disease was cured
            if (resolvedReasonDropdownChoice == ResolveReason.CURED) { // "Disease was cured"
                PageNavigator.showAlert(AlertType.CONFIRMATION,
                        "Go to Medical History Page",
                        "Do you want to go to the medical history page to mark the disease that was cured?",
                        mainController.getStage(),
                        isOk -> {
                            if (isOk) {
                                PageNavigator.loadPage(Page.VIEW_MEDICAL_HISTORY, mainController);
                            }
                        });
            }
        }
    }

    private void resolveDeceasedRequest() {
        MainController newMain = PageNavigator.openNewWindow(400, 400);
        assert newMain != null;
        newMain.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinicianViewClientWindow()
                .viewClient(client)
                .build());

        PageNavigator.loadPage(Page.SUBMIT_DEATH_DETAILS, newMain);
    }
}
