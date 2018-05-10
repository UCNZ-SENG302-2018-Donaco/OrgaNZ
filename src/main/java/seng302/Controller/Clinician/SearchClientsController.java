package seng302.Controller.Clinician;

import java.util.ArrayList;
import java.util.Collection;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import seng302.Client;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.State.State;
import seng302.Utilities.Enums.Gender;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;
import seng302.Utilities.View.WindowContext;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.RangeSlider;

public class SearchClientsController extends SubController {

    private static final int ROWS_PER_PAGE = 30;
    private static final int AGE_LOWER_BOUND = 0;
    private static final int AGE_UPPER_BOUND = 120;

    @FXML
    private TextField searchBox, ageMinField, ageMaxField;

    @FXML
    private RangeSlider ageSlider;

    @FXML
    private CheckComboBox<Gender> birthGenderFilter;

    @FXML
    private CheckComboBox<Region> regionFilter;

    @FXML
    private CheckComboBox<String> clientTypeFilter;

    @FXML
    private CheckComboBox<Organ> organsDonatingFilter, organsRequestingFilter;

    @FXML
    private HBox sidebarPane;

    @FXML
    private TableView<Client> tableView;

    @FXML
    private TableColumn<Client, Integer> idCol;
    @FXML
    private TableColumn<Client, String> nameCol;
    @FXML
    private TableColumn<Client, Integer> ageCol;
    @FXML
    private TableColumn<Client, Gender> genderCol;
    @FXML
    private TableColumn<Client, Region> regionCol;

    @FXML
    private Pagination pagination;

    private ObservableList<Client> observableClientList = FXCollections.observableArrayList();
    private FilteredList<Client> filteredClients;
    private SortedList<Client> sortedClients;

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Client search");
        mainController.loadSidebar(sidebarPane);
        ageSlider.setLowValue(AGE_LOWER_BOUND);
        ageSlider.setHighValue(AGE_UPPER_BOUND);
    }

    @FXML
    private void initialize() {
        ArrayList<Client> allClients = State.getClientManager().getClients();

        setupTable();

        tableView.setOnSort((o) -> createPage(pagination.getCurrentPageIndex()));

        // Match values in text boxes beside age slider to the values on the slider
        ageMinField.setOnAction(event -> {
            int newMin;
            try {
                newMin = Integer.max(Integer.parseInt(ageMinField.getText()), AGE_LOWER_BOUND);
            } catch (NumberFormatException ignored) {
                newMin = AGE_LOWER_BOUND;
            }
            ageSlider.setLowValue(newMin);
        });
        ageMaxField.setOnAction(event -> {
            int newMax;
            try {
                newMax = Integer.min(Integer.parseInt(ageMaxField.getText()), AGE_UPPER_BOUND);
            } catch (NumberFormatException exc) {
                newMax = 0;
            }
            ageSlider.setHighValue(newMax);
        });

        // Refresh table when any filter controls change
        ageSlider.lowValueProperty().addListener((observable, oldValue, newValue) -> {
            ageMinField.setText(Integer.toString(newValue.intValue()));
            refresh();
        });
        ageSlider.highValueProperty().addListener((observable, oldValue, newValue) -> {
            ageMaxField.setText(Integer.toString(newValue.intValue()));
            refresh();
        });
        birthGenderFilter.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<Gender>) change -> refresh());
        regionFilter.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<Region>) change -> refresh());
        clientTypeFilter.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<String>) change -> refresh());
        organsDonatingFilter.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<Organ>) change -> refresh());
        organsRequestingFilter.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<Organ>) change -> refresh());

        birthGenderFilter.getItems().setAll(Gender.values());
        regionFilter.getItems().setAll(Region.values());
        clientTypeFilter.getItems().setAll("Donor", "Receiver");
        organsDonatingFilter.getItems().setAll(Organ.values());
        organsRequestingFilter.getItems().setAll(Organ.values());
        searchBox.textProperty().addListener(((o) -> refresh()));

        //Create a filtered list, that defaults to allow all using lambda function
        filteredClients = new FilteredList<>(FXCollections.observableArrayList(allClients), client -> true);
        //Create a sorted list that links to the filtered list
        sortedClients = new SortedList<>(filteredClients);
        //Link the sorted list sort to the tableView sort
        sortedClients.comparatorProperty().bind(tableView.comparatorProperty());

        //Set initial pagination
        int numberOfPages = Math.max(1, (sortedClients.size() + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE);
        pagination.setPageCount(numberOfPages);
        //On pagination update call createPage
        pagination.setPageFactory(this::createPage);

        //Initialize the observable list to all clients
        observableClientList.setAll(sortedClients);
        //Bind the tableView to the observable list
        tableView.setItems(observableClientList);
    }

    /**
     * Initialize the table columns.
     * The client must have getters for these specific names specified in the PV Factories.
     */
    private void setupTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("uid"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        regionCol.setCellValueFactory(new PropertyValueFactory<>("region"));

        tableView.setRowFactory(tv -> new TableRow<Client>() {
            private Tooltip tooltip = new Tooltip();

            @Override
            public void updateItem(Client client, boolean empty) {
                super.updateItem(client, empty);
                if (client == null) {
                    setTooltip(null);
                } else {
                    tooltip.setText(String.format("%s %s with blood type %s. Donating: %s",
                            client.getFirstName(),
                            client.getLastName(),
                            client.getBloodType(),
                            client.getOrganStatusString("donations")));
                    setTooltip(tooltip);
                }
            }
        });

        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                Client client = tableView.getSelectionModel().getSelectedItem();
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
     * Checks if a client is from a region in the regions CheckComboBox. If so, returns true, otherwise false.
     * @param client the client whos region is being checked
     * @return true if the client is from a region selected. False otherwise.
     */
    private boolean regionFilter(Client client) {
        Collection<Region> regionsToFilter = new ArrayList<>();

        for (Region region : Region.values()) {
            if (regionFilter.getCheckModel().isChecked(region)) {
                regionsToFilter.add(region);
            }
        }
        return regionsToFilter.size() == 0 || regionsToFilter.contains(client.getRegion());
    }


    /**
     * Checks all filters in the controller and returns whether or not the client matches all the filters.
     * @param client the client who is being compared for each filter.
     * @return true if the client matches all the filters. False otherwise.
     */
    private boolean filter(Client client) {
        String searchText = searchBox.getText();
        boolean searchTextNull = searchText == null || searchText.length() == 0;
        if (searchTextNull) {
            return regionFilter(client);
        } else {
            return client.nameContains(searchText) && regionFilter(client);
        }
    }

    /**
     * Upon filtering update, refresh the filters to the new string and update pagination
     * Every refresh triggers the pagination to update and go to page zero
     */
    @Override
    public void refresh() {
        filteredClients.setPredicate(this::filter);

        //If the pagination count wont change, force a refresh of the page, if it will, change it and that will trigger the update.
        int newPageCount = Math.max(1, (filteredClients.size() + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE);
        if (pagination.getPageCount() == newPageCount) {
            createPage(pagination.getCurrentPageIndex());
        } else {
            pagination.setPageCount(newPageCount);
        }
    }

    /**
     * Upon pagination, update the table to show the correct items
     * @param pageIndex The page we're now on (starts at 0)
     * @return An empty pane as pagination requires a non null return. Not used.
     */
    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredClients.size());
        observableClientList.setAll(sortedClients.subList(fromIndex, toIndex));
        return new Pane();
    }
}
