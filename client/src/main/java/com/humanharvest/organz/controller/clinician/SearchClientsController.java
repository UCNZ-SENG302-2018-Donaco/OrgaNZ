package com.humanharvest.organz.controller.clinician;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.RangeSlider;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.ClientSortOptionsEnum;
import com.humanharvest.organz.utilities.enums.ClientType;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext;
import com.humanharvest.organz.views.client.ClientSortPolicy;
import com.humanharvest.organz.views.client.PaginatedClientList;

public class SearchClientsController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(SearchClientsController.class.getName());

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
    private ChoiceBox<ClientType> clientTypeFilter;

    @FXML
    private CheckComboBox<Organ> organsDonatingFilter, organsRequestingFilter;

    @FXML
    private HBox donatingFilterBox, requestingFilterBox;

    @FXML
    private HBox menuBarPane;

    @FXML
    private TableView<Client> tableView;

    @FXML
    private TableColumn<Client, Integer> idCol;
    @FXML
    private TableColumn<Client, Client> nameCol;
    @FXML
    private TableColumn<Client, Integer> ageCol;
    @FXML
    private TableColumn<Client, Gender> genderCol;
    @FXML
    private TableColumn<Client, Region> regionCol;
    @FXML
    private TableColumn<Client, Boolean> donorCol;
    @FXML
    private TableColumn<Client, Boolean> receiverCol;

    @FXML
    private Pagination pagination;

    @FXML
    private Text displayingXToYOfZText;

    private ObservableList<Client> observableClientList = FXCollections.observableArrayList();

    private static <T extends Enum<T>> EnumSet<T> filterToSet(CheckComboBox<T> filter, Class<T> enumType) {
        EnumSet<T> enumSet = EnumSet.noneOf(enumType);
        enumSet.addAll(filter.getCheckModel().getCheckedItems());
        return enumSet;
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Client search");
        mainController.loadMenuBar(menuBarPane);
    }

    @FXML
    private void initialize() {

        setupTable();

        tableView.setOnSort(o -> updateClientList());

        // Match values in text boxes beside age slider to the values on the slider
        ageMinField.setOnAction(event -> {
            int newMin;
            try {
                newMin = Integer.max(Integer.parseInt(ageMinField.getText()), AGE_LOWER_BOUND);
            } catch (NumberFormatException ignored) {
                newMin = AGE_LOWER_BOUND;
            }
            ageSlider.setLowValue(newMin);
            ageMinField.setText("" + newMin);
        });
        ageMaxField.setOnAction(event -> {
            int newMax;
            try {
                newMax = Integer.min(Integer.parseInt(ageMaxField.getText()), AGE_UPPER_BOUND);
            } catch (NumberFormatException ignored) {
                newMax = AGE_UPPER_BOUND;
            }
            ageSlider.setHighValue(newMax);
            ageMaxField.setText("" + newMax);
        });

        // Set options for choice boxes and check combo boxes
        birthGenderFilter.getItems().setAll(Gender.values());
        regionFilter.getItems().setAll(Region.values());
        clientTypeFilter.getItems().setAll(ClientType.values());
        clientTypeFilter.getSelectionModel().select(0);
        organsDonatingFilter.getItems().setAll(Organ.values());
        organsRequestingFilter.getItems().setAll(Organ.values());

        // Refresh table when any filter controls change
        ageSlider.lowValueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.intValue() != newValue.intValue()) {
                ageMinField.setText(Integer.toString(newValue.intValue()));
                updateClientList();
            }
        });
        ageSlider.highValueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.intValue() != newValue.intValue()) {
                ageMaxField.setText(Integer.toString(newValue.intValue()));
                updateClientList();
            }
        });

        //Set the filters to fire upon any filter change
        birthGenderFilter.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<Gender>) change -> updateClientList());

        regionFilter.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<Region>) change -> updateClientList());

        clientTypeFilter.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> changeClientType(newValue));

        organsDonatingFilter.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<Organ>) change -> updateClientList());

        organsRequestingFilter.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<Organ>) change -> updateClientList());

        searchBox.textProperty().addListener(o -> updateClientList());

        //On pagination update call createPage
        pagination.setPageFactory(this::createPage);

        //Make the comparators always return zero as the list is already ordered by the server using custom sort
        for (TableColumn<?, ?> column : tableView.getColumns()) {
            column.setComparator((c1, c2) -> 0);
        }

        //Bind the tableView to the observable list
        tableView.setItems(observableClientList);

        if (State.getSession().getLoggedInUserType() == UserType.ADMINISTRATOR) {

            tableView.setRowFactory(tableView -> {
                //Enable the tooltip to show organ donation status
                TableRow<Client> row = new ClientTableRow();

                //Enable right click to delete
                MenuItem removeItem = new MenuItem("Delete");
                removeItem.setOnAction(event -> {
                    deleteClient(row.getItem());
                    PageNavigator.refreshAllWindows();
                });
                ContextMenu rowMenu = new ContextMenu(removeItem);

                // Only display context menu for non-null items
                row.contextMenuProperty().bind(
                        Bindings.when(Bindings.isNotNull(row.itemProperty()))
                                .then(rowMenu)
                                .otherwise((ContextMenu) null));
                return row;
            });
        }
    }

    private void deleteClient(Client client) {
        try {
            State.getClientManager().removeClient(client);
        } catch (NotFoundException e) {
            LOGGER.log(Level.WARNING, "Client not found", e);
            PageNavigator.showAlert(AlertType.WARNING, "Client not found", "The client could not be found on the "
                    + "server, it may have been deleted", mainController.getStage());
        } catch (ServerRestException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            PageNavigator.showAlert(AlertType.WARNING, "Server error", "Could not apply changes on the server, "
                    + "please try again later", mainController.getStage());
        } catch (IfMatchFailedException e) {
            LOGGER.log(Level.INFO, "If-Match did not match", e);
            PageNavigator.showAlert(AlertType.WARNING, "Outdated Data",
                    "The client has been modified since you retrieved the data.\nIf you would still like to "
                            + "apply these changes please submit again, otherwise refresh the page to update the data.",
                    mainController.getStage());
        }
    }

    @Override
    public void refresh() {
        super.refresh();
        // Refresh the client list to ensure any additions or removals are updated
        updateClientList();
    }

    /**
     * Initialize the table columns.
     * The client must have getters for these specific names specified in the PV Factories.
     */
    private void setupTable() {
        //Link the nameCol to that row's client
        nameCol.setCellValueFactory(cellData -> new ObservableValueBase<Client>() {
            @Override
            public Client getValue() {
                return cellData.getValue();
            }
        });
        //Link the nameCol text to the client fullName
        nameCol.setCellFactory(c -> new TableCell<Client, Client>() {
            @Override
            protected void updateItem(Client client, boolean empty) {
                super.updateItem(client, empty);

                if (client == null || empty) {
                    setText(null);
                } else {
                    setText(client.getFullName());
                }
            }
        });

        //Setup the age slider
        ageSlider.setLowValue(AGE_LOWER_BOUND);
        ageSlider.setHighValue(AGE_UPPER_BOUND);

        //Set up the basic columns and map them to the respective values
        idCol.setCellValueFactory(new PropertyValueFactory<>("uid"));
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        regionCol.setCellValueFactory(new PropertyValueFactory<>("region"));
        donorCol.setCellValueFactory(new PropertyValueFactory<>("donor"));
        receiverCol.setCellValueFactory(new PropertyValueFactory<>("receiver"));

        // Setting the donor and receiver columns to have ticks if the client is a donor or receiver
        donorCol.setCellFactory(tc -> new TableCell<Client, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null :
                        (item ? "✓" : ""));
            }
        });
        receiverCol.setCellFactory(tc -> new TableCell<Client, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null :
                        (item ? "✓" : ""));
            }
        });

        //Enable the tooltip to show organ donation status
        tableView.setRowFactory(tv -> new TableRow<Client>() {
            private Tooltip tooltip = new Tooltip();

            @Override
            public void updateItem(Client client, boolean empty) {
                super.updateItem(client, empty);
                if (client == null) {
                    setTooltip(null);
                } else {
                    tooltip.setText(String.format("%s with blood type %s. Donating: %s",
                            client.getFullName(),
                            client.getBloodType(),
                            client.getOrganStatusString("donations")));
                    setTooltip(tooltip);
                }
            }
        });

        //When double clicking on a client, open their account on a new page
        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                Client client = tableView.getSelectionModel().getSelectedItem();
                if (client != null) {
                    MainController newMain = PageNavigator.openNewWindow();
                    if (newMain != null) {
                        newMain.setWindowContext(new WindowContext.WindowContextBuilder()
                                .setAsClinicianViewClientWindow()
                                .viewClient(client)
                                .build());
                        PageNavigator.loadPage(Page.VIEW_CLIENT, newMain);
                    }
                }
            }
        });
    }

    /**
     * Makes an HBox managed and visible.
     *
     * @param hBox the HBox to enable.
     */
    private void enableHBox(HBox hBox) {
        hBox.setManaged(true);
        hBox.setVisible(true);
    }

    /**
     * Makes an HBox not managed and invisible.
     *
     * @param hBox the HBox to disable.
     */
    private void disableHBox(HBox hBox) {
        hBox.setManaged(false);
        hBox.setVisible(false);
    }

    private void changeClientType(ClientType newClientType) {
        switch (newClientType) {
            case ONLY_DONOR:
                organsRequestingFilter.getCheckModel().clearChecks();
                enableHBox(donatingFilterBox);
                disableHBox(requestingFilterBox);
                break;
            case ONLY_RECEIVER:
                organsDonatingFilter.getCheckModel().clearChecks();
                disableHBox(donatingFilterBox);
                enableHBox(requestingFilterBox);
                break;
            case NEITHER:
                organsRequestingFilter.getCheckModel().clearChecks();
                organsDonatingFilter.getCheckModel().clearChecks();
                disableHBox(donatingFilterBox);
                disableHBox(requestingFilterBox);
                break;
            default: // both
                enableHBox(donatingFilterBox);
                enableHBox(requestingFilterBox);
                break;
        }
        updateClientList();
    }

    private void updateClientList() {
        ClientSortPolicy sortPolicy = getSortPolicy();
        PaginatedClientList newClients = State.getClientManager().getClients(
                searchBox.getText(),
                pagination.getCurrentPageIndex() * ROWS_PER_PAGE,
                ROWS_PER_PAGE,
                (int) ageSlider.getLowValue(),
                (int) ageSlider.getHighValue(),
                regionFilter.getCheckModel().getCheckedItems().stream().map(Enum::toString).collect(Collectors.toSet()),
                filterToSet(birthGenderFilter, Gender.class),
                clientTypeFilter.getValue(),
                filterToSet(organsDonatingFilter, Organ.class),
                filterToSet(organsRequestingFilter, Organ.class),
                sortPolicy.getSortOption(),
                sortPolicy.isReversed());

        observableClientList.setAll(newClients.getClients());

        int newPageCount = Math.max(1, (newClients.getTotalResults() + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE);
        if (pagination.getPageCount() != newPageCount) {
            pagination.setPageCount(newPageCount);
        }

        setupDisplayingXToYOfZText(newClients.getTotalResults());
    }

    /**
     * Used to detect the current sort policy of the table and convert it to a value that the server will understand
     *
     * @return A ClientSortPolicy that maps to one of the SortOptions and a boolean if the sort should be reversed
     */
    private ClientSortPolicy getSortPolicy() {
        ObservableList<TableColumn<Client, ?>> sortOrder = tableView.getSortOrder();
        if (sortOrder.size() == 0) {
            return new ClientSortPolicy(ClientSortOptionsEnum.NAME, false);
        }
        TableColumn<Client, ?> sortColumn = tableView.getSortOrder().get(0);

        Map<String, ClientSortOptionsEnum> columnToSortOptionCoverter = new HashMap<>();
        columnToSortOptionCoverter.put("nameCol", ClientSortOptionsEnum.NAME);
        columnToSortOptionCoverter.put("idCol", ClientSortOptionsEnum.ID);
        columnToSortOptionCoverter.put("ageCol", ClientSortOptionsEnum.AGE);
        columnToSortOptionCoverter.put("genderCol", ClientSortOptionsEnum.BIRTH_GENDER);
        columnToSortOptionCoverter.put("regionCol", ClientSortOptionsEnum.REGION);
        columnToSortOptionCoverter.put("donorCol", ClientSortOptionsEnum.DONOR);
        columnToSortOptionCoverter.put("receiverCol", ClientSortOptionsEnum.RECEIVER);

        return new ClientSortPolicy(columnToSortOptionCoverter.get(sortColumn.getId()),
                sortColumn.getSortType().equals(SortType.DESCENDING));
    }

    /**
     * Upon pagination, update the table to show the correct items
     *
     * @param pageIndex The page we're now on (starts at 0)
     * @return An empty pane as pagination requires a non null return. Not used.
     */
    private Node createPage(int pageIndex) {
        updateClientList();
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
            displayingXToYOfZText.setText(String.format("Displaying %d-%d of %d", fromIndex + 1, toIndex,
                    totalCount));
        }
    }

    private static class ClientTableRow extends TableRow<Client> {

        private final Tooltip tooltip = new Tooltip();

        public ClientTableRow() {
            // This enables the context menu skin to work with multitouch
            contextMenuProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    newValue.setImpl_showRelativeToWindow(false);
                }
            });
        }

        @Override
        public void updateItem(Client item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null) {
                setTooltip(null);
            } else {
                tooltip.setText(String.format("%s with blood type %s. Donating: %s",
                        item.getFullName(),
                        item.getBloodType(),
                        item.getOrganStatusString("donations")));
                setTooltip(tooltip);
            }
        }
    }
}
