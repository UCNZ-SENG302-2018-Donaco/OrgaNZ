package seng302.Controller.Client;

import static seng302.TransplantRequest.RequestStatus.CANCELLED;
import static seng302.TransplantRequest.RequestStatus.COMPLETED;
import static seng302.TransplantRequest.RequestStatus.WAITING;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;

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

import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.Client.AddTransplantRequestAction;
import seng302.Actions.Client.MarkClientAsDeadAction;
import seng302.Actions.Client.ResolveTransplantRequestAction;
import seng302.Client;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.State.Session;
import seng302.State.State;
import seng302.TransplantRequest;
import seng302.TransplantRequest.RequestStatus;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;

/**
 * Controller for the Request Organs page. Handles the viewing of current and past organ transplant requests. If the
 * logged in user is a clinician, they also have the ability to create new transplant requests and resolve current ones.
 */
public class RequestOrgansController extends SubController {

    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("d MMM yyyy hh:mm a");

    private Session session;
    private ActionInvoker invoker;
    private Client client;

    private Collection<TransplantRequest> allRequests;
    private FilteredList<TransplantRequest> currentRequests;
    private FilteredList<TransplantRequest> pastRequests;

    @FXML
    private Pane sidebarPane;
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
    private TableColumn<TransplantRequest, RequestStatus> requestStatusPastCol;
    @FXML
    private TableColumn<TransplantRequest, String> resolvedReasonPastCol;
    @FXML
    private ComboBox<ResolveReason> cancelTransplantOptions;
    @FXML
    private TextField customReason;
    @FXML
    private DatePicker deathDatePicker;

    private enum ResolveReason {
        ERROR("Input error"),
        COMPLETED("Transplant completed"),
        CURED("Disease was cured"),
        DECEASED("Client is deceased"),
        CUSTOM("Custom reason...");

        private final String text;

        ResolveReason(String text) {
            this.text = text;
        }

        public String toString() {
            return text;
        }
    }

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
        mainController.loadSidebar(sidebarPane);

        if (session.getLoggedInUserType() == Session.UserType.CLIENT) {
            client = session.getLoggedInClient();
            newRequestForm.setManaged(false);
            newRequestForm.setVisible(false);
            resolveRequestBar.setManaged(false);
            resolveRequestBar.setVisible(false);
        } else if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
        }

        mainController.setTitle("Receive Organs: " + client.getFullName());
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
     */
    @FXML
    private void submitNewRequest() {
        TransplantRequest newRequest = new TransplantRequest(client, newOrganChoiceBox.getValue());
        if (client.getCurrentlyRequestedOrgans().contains(newRequest.getRequestedOrgan())) {
            PageNavigator.showAlert(
                    AlertType.ERROR,
                    "Request already exists",
                    "Client already has a waiting request for this organ.");
        } else {
            Action action = new AddTransplantRequestAction(client, newRequest);
            invoker.execute(action);
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
        ResolveReason resolveReason = cancelTransplantOptions.getValue();

        if (selectedRequest != null) {
            Action action = null;

            if (resolveReason == ResolveReason.COMPLETED) {
                action = new ResolveTransplantRequestAction(selectedRequest, COMPLETED, "Transplant took place.");

            } else if (resolveReason == ResolveReason.DECEASED) {
                LocalDate deathDate = deathDatePicker.getValue();
                if (deathDate.isBefore(client.getDateOfBirth()) || deathDate.isAfter(LocalDate.now())) {
                    PageNavigator.showAlert(AlertType.ERROR,
                            "Date of Death Invalid",
                            "Date of death must be between client's birth date and the current date.");
                } else {
                    Optional<ButtonType> buttonOpt = PageNavigator.showAlert(AlertType.CONFIRMATION,
                            "Are you sure you want to mark this client as dead?",
                            "This will cancel all waiting transplant requests for this client.");

                    if (buttonOpt.isPresent() && buttonOpt.get() == ButtonType.OK) {
                        action = new MarkClientAsDeadAction(client, deathDate);
                        deathDatePicker.setValue(LocalDate.now());
                    }
                }

            } else if (resolveReason == ResolveReason.CURED) {
                action = new ResolveTransplantRequestAction(selectedRequest, CANCELLED, "The disease was cured.");
                Optional<ButtonType> buttonOpt = PageNavigator.showAlert(AlertType.CONFIRMATION,
                        "Go to Medical History Page",
                        "Do you want to go to the medical history page to mark the disease that was cured?");
                if (buttonOpt.isPresent() && buttonOpt.get() == ButtonType.OK) {
                    PageNavigator.loadPage(Page.VIEW_MEDICAL_HISTORY, mainController);
                }

            } else if (resolveReason == ResolveReason.ERROR) {
                action = new ResolveTransplantRequestAction(selectedRequest, CANCELLED, "Request was a mistake.");

            } else if (resolveReason == ResolveReason.CUSTOM) {
                action = new ResolveTransplantRequestAction(selectedRequest, CANCELLED, customReason.getText());
                customReason.clear();
            }

            if (action != null) {
                invoker.execute(action);
                PageNavigator.refreshAllWindows();
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
