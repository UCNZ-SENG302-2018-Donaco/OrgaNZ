package seng302.Controller.Clinician;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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
import seng302.Utilities.View.WindowContext.WindowContextBuilder;

public class TransplantsController extends SubController {

    private static final int ROWS_PER_PAGE = 30;

    @FXML
    private HBox sidebarPane;

    @FXML
    private TableView<TransplantRequest> tableView;

    @FXML
    private TableColumn<TransplantRequest, String> clientCol;

    @FXML
    private TableColumn<TransplantRequest, Organ> organCol;

    @FXML
    private TableColumn<TransplantRequest, Region> regionCol;

    @FXML
    private TableColumn<TransplantRequest, LocalDateTime> dateCol;

    @FXML
    private Pagination pagination;

    @FXML
    private Text displayingXToYOfZText;


    private ObservableList<TransplantRequest> observableTransplantList = FXCollections.observableArrayList();
    private SortedList<TransplantRequest> sortedTransplants;

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Transplant requests");
        mainController.loadSidebar(sidebarPane);
    }

    @FXML
    private void initialize() {
        Collection<TransplantRequest> allTransplants = State.getClientManager().getAllCurrentTransplantRequests();

        setupTable();

        tableView.setOnSort((o) -> createPage(pagination.getCurrentPageIndex()));

        //Create a sorted list
        sortedTransplants = new SortedList<>(FXCollections.observableArrayList(allTransplants));
        //Link the sorted list sort to the tableView sort
        sortedTransplants.comparatorProperty().bind(tableView.comparatorProperty());

        //Set initial pagination
        int numberOfPages = Math.max(1, (sortedTransplants.size() + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE);
        pagination.setPageCount(numberOfPages);
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
        clientCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getClient().getFullName()));
        organCol.setCellValueFactory(new PropertyValueFactory<>("requestedOrgan"));
        regionCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().getClient().getRegion()));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));

        tableView.getColumns().setAll(clientCol, organCol, regionCol, dateCol);

        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                TransplantRequest request = tableView.getSelectionModel().getSelectedItem();
                if (request != null) {
                    Client client = request.getClient();
                    MainController newMain = PageNavigator.openNewWindow();
                    if (newMain != null) {
                        newMain.setWindowContext(new WindowContextBuilder()
                                .setAsClinViewClientWindow()
                                .viewClient(client)
                                .build());
                        PageNavigator.loadPage(Page.VIEW_CLIENT, newMain);
                    }
                }
            }
        });

        organCol.setComparator(new Comparator<Organ>() {
            /**
             * Alphabetical order of the name
             */
            @Override
            public int compare(Organ o1, Organ o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });

        regionCol.setComparator(new Comparator<Region>() {
            /**
             * Nulls are ordered first, then alphabetical order of the name.
             */
            @Override
            public int compare(Region o1, Region o2) {
                if (o1 == null) {
                    if (o2 == null) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else if (o2 == null) {
                    return 1;
                }
                return o1.toString().compareTo(o2.toString());
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
        if (sortedTransplants.size() < 2 || fromIndex + 1 == toIndex) {
            // 0 or 1 items OR the last item, on its own page
            displayingXToYOfZText.setText(String.format("Displaying %d of %d", sortedTransplants.size(), sortedTransplants.size()));
        } else {
            displayingXToYOfZText.setText(String.format("Displaying %d-%d of %d", fromIndex + 1, toIndex,
                    sortedTransplants.size()));
        }
        return new Pane();
    }
}