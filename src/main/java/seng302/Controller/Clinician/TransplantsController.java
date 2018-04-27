package seng302.Controller.Clinician;

import java.time.LocalDateTime;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import seng302.Client;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.State.State;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;
import seng302.Utilities.View.WindowContext;

public class TransplantsController extends SubController {

    private static final int ROWS_PER_PAGE = 30;

    @FXML
    private HBox sidebarPane;

    @FXML
    private TableView<TransplantRequest> tableView;

    @FXML
    private TableColumn<TransplantRequest, LocalDateTime> dateCol;

    @FXML
    private TableColumn<TransplantRequest, String> clientCol;

    @FXML
    private TableColumn<TransplantRequest, Organ> organCol;

    @FXML
    private TableColumn<TransplantRequest, Region> regionCol;

    @FXML
    private Pagination pagination;

    @FXML
    private Text displayingXToYOfZText;


    private ObservableList<TransplantRequest> observableTransplantList = FXCollections.observableArrayList();
    private SortedList<TransplantRequest> sortedTransplants;

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.loadSidebar(sidebarPane);
    }

    //Note: some commented-out code is just copied and pasted from SearchClientsController

    @FXML
    private void initialize() {
        List<TransplantRequest> allTransplants = State.getClientManager().getAllTransplantRequests();

        setupTable();
/*
        tableView.setOnSort((o) -> createPage(pagination.getCurrentPageIndex()));
        searchBox.textProperty().addListener(((o) -> refresh()));*/

        //Create a sorted list
        sortedTransplants = new SortedList<>(FXCollections.observableArrayList(allTransplants));
        //Link the sorted list sort to the tableView sort
        sortedTransplants.comparatorProperty().bind(tableView.comparatorProperty());

        //Set initial pagination
        double numberOfPages = Math.ceil((double) sortedTransplants.size() / (double) ROWS_PER_PAGE);
        pagination.setPageCount((int) numberOfPages);
        //On pagination update call createPage
        pagination.setPageFactory(this::createPage);

        //Initialize the observable list to all clients
        observableTransplantList.setAll(sortedTransplants);
        //Bind the tableView to the observable list
        tableView.setItems(observableTransplantList);

    }


    /**
     * Initialize the table columns.
     * The client must have getters for these specific names specified in the PV Factories.
     */
    private void setupTable() {
        dateCol.setCellValueFactory(new PropertyValueFactory<>("requestDateString"));
        clientCol.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        organCol.setCellValueFactory(new PropertyValueFactory<>("requestedOrgan"));
        regionCol.setCellValueFactory(new PropertyValueFactory<>("clientRegion"));

        tableView.getColumns().setAll(clientCol, organCol, regionCol, dateCol);

        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                Client client = tableView.getSelectionModel().getSelectedItem().getClient();
                if (client != null) {
                    MainController newMain = PageNavigator.openNewWindow();
                    if (newMain != null) {
                        newMain.setWindowContext(new WindowContext.WindowContextBuilder()
                                .setAsClinViewClientWindow()
                                .viewClient(client)
                                .build());
                        PageNavigator.loadPage(Page.VIEW_CLIENT, newMain);
                    }
                }
            }
        });

    }

    /**
     * Upon pagination, update the table to show the correct items
     * @param pageIndex The page we're now on (starts at 0)
     * @return An empty pane as pagination requires a non null return. Not used.
     */
    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, sortedTransplants.size());
        observableTransplantList.setAll(sortedTransplants.subList(fromIndex, toIndex));
        if (sortedTransplants.size() == 0) {
            displayingXToYOfZText.setText("Displaying 0 of 0");
        }
        displayingXToYOfZText.setText(String.format("Displaying %d-%d of %d", fromIndex + 1, toIndex,
                sortedTransplants.size()));
        return new Pane();
    }

}