package seng302.Controller.Client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

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

public class OrganRequestHistoryController extends SubController {

    @FXML
    private TextField searchBox;

    @FXML
    private TableView<TransplantRequest> tableView;

    @FXML
    private TableColumn<TransplantRequest, Organ> organCol;
    @FXML
    private TableColumn<TransplantRequest, String> currentRequestCol;
    @FXML
    private TableColumn<TransplantRequest, String> dateCol;

    private ObservableList<TransplantRequest> observableTransplantRequestList;// = FXCollections.observableArrayList();

    private Session session;
    private ActionInvoker invoker;
    private Client client;

    public OrganRequestHistoryController() {
        invoker = State.getInvoker();
        session = State.getSession();
    }

    @FXML
    private void initialize() {

        setupTable();

        //observableTransplantRequestList = FXCollections.observableArrayList(client.getTransplantRequests());

        //tableView.setItems(observableTransplantRequestList);
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        // mainController.loadSidebar(sidebarPane);

        if (session.getLoggedInUserType() == Session.UserType.CLIENT) {
            client = session.getLoggedInClient();
        } else if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
        }

        searchBox.setText(Integer.toString(client.getUid()));
        // updateUserID(null);
    }

    private void setupTable() {
        organCol.setCellValueFactory(new PropertyValueFactory<>("requestedOrgan"));
        currentRequestCol.setCellValueFactory(new PropertyValueFactory<>("currentRequest"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));

        tableView.getColumns().setAll(organCol, currentRequestCol, dateCol);
    }

    @FXML
    private void backToRequests(ActionEvent event) {
        PageNavigator.loadPage(Page.REQUEST_ORGAN, mainController);
    }

}
