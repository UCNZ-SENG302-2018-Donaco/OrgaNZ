package seng302.Controller.Client;

import static seng302.TransplantRequest.RequestStatus.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.Client.AddTransplantRequestAction;
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
    private ChoiceBox<Organ> newOrganChoiceBox;
    @FXML
    private TableView<TransplantRequest> currentRequestsTable, pastRequestsTable;
    @FXML
    private TableColumn<TransplantRequest, Organ> organCurrCol, organPastCol;
    @FXML
    private TableColumn<TransplantRequest, LocalDateTime> requestDateCurrCol, requestDatePastCol, resolvedDatePastCol;
    @FXML
    private TableColumn<TransplantRequest, RequestStatus> requestStatusPastCol;

    private static TableCell<TransplantRequest, LocalDateTime> formatDateTimeCell() {
        return new TableCell<TransplantRequest, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.format(dateTimeFormat));
                }
            }
        };
    }

    public RequestOrgansController() {
        session = State.getSession();
        invoker = State.getInvoker();
    }

    /**
     * Map each organ to the matching checkbox.
     */
    @FXML
    private void initialize() {
        newOrganChoiceBox.setItems(FXCollections.observableArrayList(Organ.values()));

        organCurrCol.setCellValueFactory(new PropertyValueFactory<>("requestedOrgan"));
        requestDateCurrCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));

        organPastCol.setCellValueFactory(new PropertyValueFactory<>("requestedOrgan"));
        requestDatePastCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        requestStatusPastCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        resolvedDatePastCol.setCellValueFactory(new PropertyValueFactory<>("resolvedDate"));

        requestDateCurrCol.setCellFactory(cell -> formatDateTimeCell());
        requestDatePastCol.setCellFactory(cell -> formatDateTimeCell());
        resolvedDatePastCol.setCellFactory(cell -> formatDateTimeCell());
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.loadSidebar(sidebarPane);

        if (session.getLoggedInUserType() == Session.UserType.CLIENT) {
            client = session.getLoggedInClient();
        } else if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
        }

        mainController.setTitle("Receive Organs: " + client.getFullName());
        refresh();
    }

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

    public void submitNewRequest() {
        TransplantRequest newRequest = new TransplantRequest(newOrganChoiceBox.getValue());
        Action action = new AddTransplantRequestAction(client, newRequest);
        invoker.execute(action);

        PageNavigator.refreshAllWindows();
    }

    @FXML
    private void returnToViewClient() {
        PageNavigator.loadPage(Page.VIEW_CLIENT, mainController);
    }

    @FXML
    private void cancelRequest() {
        TransplantRequest selectedRequest = currentRequestsTable.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            Action action = new ResolveTransplantRequestAction(selectedRequest, CANCELLED);
            invoker.execute(action);

            PageNavigator.refreshAllWindows();
        }
    }

    @FXML
    private void completeRequest() {
        TransplantRequest selectedRequest = currentRequestsTable.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            Action action = new ResolveTransplantRequestAction(selectedRequest, COMPLETED);
            invoker.execute(action);

            PageNavigator.refreshAllWindows();
        }
    }
}
