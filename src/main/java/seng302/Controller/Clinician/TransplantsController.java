package seng302.Controller.Clinician;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import seng302.Client;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.State.ClientManager;
import seng302.State.State;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;
import seng302.Utilities.View.WindowContext.WindowContextBuilder;

/**
 * Controller for the transplants waiting list page. Contains a table that shows all the current waiting requests for
 * an organ transplant.
 */
public class TransplantsController extends SubController {

    private static final int ROWS_PER_PAGE = 30;
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("d MMM yyyy hh:mm a");

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

    private ClientManager manager;
    private ObservableList<TransplantRequest> observableTransplantList = FXCollections.observableArrayList();
    private SortedList<TransplantRequest> sortedTransplants;

    /**
     * Formats a table cell that holds a {@link LocalDateTime} value to display that value in the date time format.
     * @return The cell with the date time formatter set.
     */
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
                } else if (request.getClient().getOrganDonationStatus().get(request.getRequestedOrgan())) {
                    setStyle("-fx-background-color: lightcoral");
                    setTooltip(new Tooltip("The client also currently has this organ registered for donation."));
                } else {
                    setStyle(null);
                    setTooltip(null);
                }
            }
        };
    }

    /**
     * Gets the client manager from the global state.
     */
    public TransplantsController() {
        manager = State.getClientManager();
    }

    /**
     * Sets up the page, setting its title, loading the sidebar and doing the first refresh of the data.
     * @param mainController The main controller that defines which window this subcontroller belongs to.
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Transplant requests");
        mainController.loadSidebar(sidebarPane);
        refresh();
    }

    /**
     * Refreshes the data in the transplants waiting list table. Should be called whenever any page calls a global
     * refresh.
     */
    @Override
    public void refresh() {
        sortedTransplants = new SortedList<>(FXCollections.observableArrayList(
                manager.getAllCurrentTransplantRequests()));

        //Link the sorted list sort to the tableView sort
        sortedTransplants.comparatorProperty().bind(tableView.comparatorProperty());

        //Set initial pagination
        int numberOfPages = Math.max(1, (sortedTransplants.size() + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE);
        pagination.setPageCount(numberOfPages);

        //Initialize the observable list to all clients
        observableTransplantList.setAll(sortedTransplants);

        //Bind the tableView to the observable list
        tableView.setItems(observableTransplantList);

        createPage(pagination.getCurrentPageIndex());
    }

    /**
     * Initializes the page and the table/pagination properties.
     */
    @FXML
    private void initialize() {
        setupTable();

        tableView.setOnSort((o) -> createPage(pagination.getCurrentPageIndex()));

        //On pagination update call createPage
        pagination.setPageFactory(this::createPage);
    }

    /**
     * Sets up the table columns with their respective value factories and representation factories. Also registers a
     * mouse event handler for double-clicking on a record in the table to open up the appropriate client profile.
     */
    private void setupTable() {
        clientCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getClient().getFullName()));
        organCol.setCellValueFactory(new PropertyValueFactory<>("requestedOrgan"));
        regionCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().getClient().getRegion()));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));

        // Format all the datetime cells
        dateCol.setCellFactory(cell -> formatDateTimeCell());

        // Colour each row if it is a request for an organ that the client is also registered to donate.
        tableView.setRowFactory(row -> colourIfDonatedAndRequested());

        // Register the mouse event for double-clicking on a record to open the client profile.
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

        // Sets the comparator for sorting by organ column.
        organCol.setComparator(new Comparator<Organ>() {
            /**
             * Alphabetical order of the organ name.
             */
            @Override
            public int compare(Organ o1, Organ o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });

        // Sets the comparator for sorting by region column.
        regionCol.setComparator(new Comparator<Region>() {
            /**
             * Nulls are ordered first, then alphabetical order of the region name.
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
            displayingXToYOfZText.setText(
                    String.format("Displaying %d of %d", sortedTransplants.size(), sortedTransplants.size()));
        } else {
            displayingXToYOfZText.setText(String.format("Displaying %d-%d of %d", fromIndex + 1, toIndex,
                    sortedTransplants.size()));
        }
        return new Pane();
    }
}
