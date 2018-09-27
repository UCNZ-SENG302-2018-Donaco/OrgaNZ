package com.humanharvest.organz.controller.clinician;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashSet;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.controlsfx.control.CheckComboBox;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.controller.components.FormattedLocalDateTimeCell;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext.WindowContextBuilder;
import com.humanharvest.organz.views.client.PaginatedTransplantList;

/**
 * Controller for the transplants waiting list page. Contains a table that shows all the current waiting requests for
 * an organ transplant. Clinicians can filter the requests based off the Region and Organ of the requests.
 */
public class TransplantsController extends SubController {

    private static final int ROWS_PER_PAGE = 30;
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("d MMM yyyy hh:mm a");

    private final ObservableList<TransplantRequest> observableTransplants = FXCollections.observableArrayList();
    private final SortedList<TransplantRequest> sortedTransplants = new SortedList<>(observableTransplants);

    @FXML
    private Pane menuBarPane;

    @FXML
    private TableView<TransplantRequest> tableView;

    @FXML
    private TableColumn<TransplantRequest, String> clientCol;

    @FXML
    private TableColumn<TransplantRequest, Organ> organCol;

    @FXML
    private TableColumn<TransplantRequest, String> regionCol;

    @FXML
    private TableColumn<TransplantRequest, LocalDateTime> dateTimeCol;

    @FXML
    private Pagination pagination;

    @FXML
    private Text displayingXToYOfZText;

    @FXML
    private CheckComboBox<String> regionChoice;

    @FXML
    private CheckComboBox<Organ> organChoice;

    private ClientManager manager;

    /**
     * Gets the client manager from the global state.
     */
    public TransplantsController() {
        manager = State.getClientManager();
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
     * Sets up the page, setting its title, loading the menu bar and doing the first refresh of the data.
     *
     * @param mainController The main controller that defines which window this SubController belongs to.
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Transplant requests");
        mainController.loadNavigation(menuBarPane);
        refresh();
    }

    /**
     * Initializes the page and the table/pagination properties.
     */
    @FXML
    private void initialize() {
        setupTable();
        for (Region region : Region.values()) {
            regionChoice.getItems().add(region.toString());
        }
        regionChoice.getItems().add("International");

        tableView.setOnSort(o -> createPage(pagination.getCurrentPageIndex()));

        organChoice.getItems().addAll(Organ.values());

        regionChoice.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<String>) change -> updateTransplantRequestList());

        organChoice.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<Organ>) change -> updateTransplantRequestList());

        //On pagination update call createPage
        pagination.setPageFactory(this::createPage);

        //Link the sorted list sort to the tableView sort
        sortedTransplants.comparatorProperty().bind(tableView.comparatorProperty());
        //Bind the tableView to the sorted list
        tableView.setItems(sortedTransplants);

        //Set initial pagination
        int numberOfPages = Math.max(1, (sortedTransplants.size() + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE);
        pagination.setPageCount(numberOfPages);
        createPage(pagination.getCurrentPageIndex());
    }

    /**
     * Refreshes the data in the transplants waiting list table.
     * There cannot be any changes from this page so it will always refresh
     */
    @Override
    public void refresh() {
        updateTransplantRequestList();
    }

    /**
     * Sets up the table columns with their respective value factories and representation factories. Also registers a
     * mouse event handler for double-clicking on a record in the table to open up the appropriate client profile.
     */
    private void setupTable() {
        clientCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getClient().getFullName()));
        organCol.setCellValueFactory(new PropertyValueFactory<>("requestedOrgan"));
        regionCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getClient().getRegion()));
        dateTimeCol.setCellValueFactory(new PropertyValueFactory<>("requestDateTime"));

        // Format all the datetime cells
        dateTimeCol.setCellFactory(cell -> new FormattedLocalDateTimeCell<>(dateTimeFormat));

        // Colour each row if it is a request for an organ that the client is also registered to donate.
        tableView.setRowFactory(row -> colourIfDonatedAndRequested());

        // Register the mouse event for double-clicking on a record to open the client profile.
        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                TransplantRequest request = tableView.getSelectionModel().getSelectedItem();
                if (request != null) {
                    Client client = request.getClient();
                    MainController newMain = PageNavigator.openNewWindow(mainController);
                    if (newMain != null) {
                        newMain.setWindowContext(new WindowContextBuilder()
                                .setAsClinicianViewClientWindow()
                                .viewClient(client)
                                .build());
                        PageNavigator.loadPage(Page.VIEW_CLIENT, newMain);
                    }
                }
            }
        });

        // Sets the comparator for sorting by organ column.
        organCol.setComparator(Comparator.comparing(Organ::toString));

        // Sets the comparator for sorting by region column.

        //Nulls are ordered first, then alphabetical order of the region name.
        regionCol.setComparator(Comparator.nullsFirst(Comparator.naturalOrder()));
    }

    private void updateTransplantRequestList() {
        PaginatedTransplantList newTransplantRequests = manager.getAllCurrentTransplantRequests(
                pagination.getCurrentPageIndex() * ROWS_PER_PAGE,
                ROWS_PER_PAGE,
                new HashSet<>(regionChoice.getCheckModel().getCheckedItems()),
                new HashSet<>(organChoice.getCheckModel().getCheckedItems()));

        observableTransplants.setAll(newTransplantRequests.getTransplantRequests());

        int newPageCount = Math.max(1, (newTransplantRequests.getTotalResults() + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE);
        if (pagination.getPageCount() != newPageCount) {
            pagination.setPageCount(newPageCount);
        }

        setupDisplayingXToYOfZText(newTransplantRequests.getTotalResults());
    }

    /**
     * Upon pagination, update the table to show the correct items
     *
     * @param pageIndex The page we're now on (starts at 0)
     * @return An empty pane as pagination requires a non null return. Not used.
     */
    private Node createPage(int pageIndex) {
        updateTransplantRequestList();
        return new Pane();
    }

    /**
     * Set the text that advises the currently viewed and pending amount of results
     *
     * @param totalCount The total amount of current results matching filter options
     */
    private void setupDisplayingXToYOfZText(int totalCount) {
        int fromIndex = pagination.getCurrentPageIndex() * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, totalCount);
        if (totalCount < 2 || fromIndex + 1 == toIndex) {
            // 0 or 1 items OR the last item, on its own page
            displayingXToYOfZText.setText(String.format("Displaying %d of %d",
                    totalCount,
                    totalCount));
        } else {
            displayingXToYOfZText.setText(String.format("Displaying %d-%d of %d",
                    fromIndex + 1, toIndex,
                    totalCount));
        }
    }
}
