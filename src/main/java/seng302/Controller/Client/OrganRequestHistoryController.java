package seng302.Controller.Client;

import java.time.LocalDateTime;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import seng302.Actions.ActionInvoker;
import seng302.Client;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.State.ClientManager;
import seng302.State.Session;
import seng302.State.State;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;

/**
 * Presents the history of a particular receivers transplant requests in a table.
 */
public class OrganRequestHistoryController extends SubController {

    @FXML
    private TextField searchBox;
    @FXML
    private TableView<TransplantRequest> tableView;
    @FXML
    private TableColumn<TransplantRequest, Organ> organCol;
    @FXML
    private TableColumn<TransplantRequest, Boolean> currentRequestCol;
    @FXML
    private TableColumn<TransplantRequest, LocalDateTime> dateCol;
    @FXML
    private Pane sidebarPane;

    private ObservableList<TransplantRequest> observableTransplantRequestList;// = FXCollections.observableArrayList();
    private Session session;
    private ActionInvoker invoker;
    private Client client;

    public OrganRequestHistoryController() {
        invoker = State.getInvoker();
        session = State.getSession();
    }

    /**
     * Setup the table view.
     */
    @FXML
    private void initialize() {
        organCol.setCellValueFactory(new PropertyValueFactory<>("requestedOrgan"));
        currentRequestCol.setCellValueFactory(new PropertyValueFactory<>("currentRequest"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));

        tableView.getColumns().setAll(organCol, currentRequestCol, dateCol);
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

        searchBox.setText(Integer.toString(client.getUid()));

        observableTransplantRequestList = FXCollections.observableArrayList(client.getTransplantRequests());
        tableView.setItems(observableTransplantRequestList);
        // updateUserID(null);
    }

    /**
     * Navigate back to the request_organ page.
     */
    @FXML
    private void backToRequests(ActionEvent event) {
        PageNavigator.loadPage(Page.REQUEST_ORGAN, mainController);
    }

}
