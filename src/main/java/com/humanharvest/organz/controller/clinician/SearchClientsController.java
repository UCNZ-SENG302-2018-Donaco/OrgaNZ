package com.humanharvest.organz.controller.clinician;

import java.util.Collection;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
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

import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.client.DeleteClientAction;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext;

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
    private ChoiceBox<String> clientTypeFilter;

    private ClientManager clientManager;
    private ActionInvoker invoker;

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

    private ObservableList<Client> allClients = FXCollections.observableArrayList();
    private ObservableList<Client> observableClientList = FXCollections.observableArrayList();
    private FilteredList<Client> filteredClients;
    private SortedList<Client> sortedClients;

    public SearchClientsController() {
        this.clientManager = State.getClientManager();
        this.invoker = State.getInvoker();
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Client search");
        mainController.loadMenuBar(menuBarPane);
        ageSlider.setLowValue(AGE_LOWER_BOUND);
        ageSlider.setHighValue(AGE_UPPER_BOUND);
    }

    @FXML
    private void initialize() {

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
            ageMinField.setText("" + newMin);
        });
        ageMaxField.setOnAction(event -> {
            int newMax;
            try {
                newMax = Integer.min(Integer.parseInt(ageMaxField.getText()), AGE_UPPER_BOUND);
            } catch (NumberFormatException exc) {
                newMax = 0;
            }
            ageSlider.setHighValue(newMax);
            ageMaxField.setText("" + newMax);
        });

        // Set options for choice boxes and check combo boxes
        birthGenderFilter.getItems().setAll(Gender.values());
        regionFilter.getItems().setAll(Region.values());
        clientTypeFilter.getItems().setAll("Any", "Only Donor", "Only Receiver", "Both", "Neither");
        clientTypeFilter.getSelectionModel().select(0);
        organsDonatingFilter.getItems().setAll(Organ.values());
        organsRequestingFilter.getItems().setAll(Organ.values());

        // Refresh table when any filter controls change
        ageSlider.lowValueProperty().addListener((observable, oldValue, newValue) -> {
            ageMinField.setText(Integer.toString(newValue.intValue()));
            repaginate();
        });
        ageSlider.highValueProperty().addListener((observable, oldValue, newValue) -> {
            ageMaxField.setText(Integer.toString(newValue.intValue()));
            repaginate();
        });
        birthGenderFilter.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<Gender>) change -> repaginate());
        regionFilter.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<Region>) change -> repaginate());
        clientTypeFilter.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> changeClientType(newValue));
        organsDonatingFilter.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<Organ>) change -> repaginate());
        organsRequestingFilter.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<Organ>) change -> repaginate());
        searchBox.textProperty().addListener(((o) -> repaginate()));

        //Link the initial observable list to the list of all clients
        allClients.setAll(clientManager.getClients());

        //Create a filtered list, that defaults to allow all using lambda function
        filteredClients = new FilteredList<>(allClients, this::filter);

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

        if (State.getSession().getLoggedInUserType() == UserType.ADMINISTRATOR) {

            tableView.setRowFactory(tableView -> {
                //Enable the tooltip to show organ donation status
                TableRow<Client> row = new TableRow<Client>() {
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
                };

                //Enable right click to delete
                MenuItem removeItem = new MenuItem("Delete");
                removeItem.setOnAction(event -> {
                    Action deleteAction = new DeleteClientAction(row.getItem(), clientManager);
                    invoker.execute(deleteAction);
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

    @Override
    public void refresh() {
        super.refresh();
        // Refresh the client list to ensure any additions or removals are updated
        allClients.setAll(clientManager.getClients());
        repaginate();
    }

    /**
     * Compare two string, and return an integer value representing which one is greater
     * Will always favor one name if it matches the search string, if both match it will compare the strings directly
     * If neither match, it will return 0 always.
     * @param searchTerm The search term to consider
     * @param name1 The first name to check
     * @param name2 The second name to check
     * @return The resulting sort integer
     */
    private int compareName(String searchTerm, String name1, String name2) {
        boolean name1Matches = name1.toLowerCase().startsWith(searchTerm);
        boolean name2Matches = name2.toLowerCase().startsWith(searchTerm);

        if (name1Matches && name2Matches) {
            return name1.compareTo(name2);
        } else if (name1Matches) {
            return -1;
        } else if (name2Matches) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Compares the names based off the priority Last name -> Pref name -> First name -> Middle name -> Client ID
     * Falls back to comparing the user id's if the names are identical to ensure a consistent order
     * @param client1 The first client object being compared
     * @param client2 The second client object being compared
     * @return -1 if client1 is higher priority. 1 if client1 is lower priority. 0 only if they have the same user ID.
     */
    private int compareNames(Client client1, Client client2) {
        //Last name -> Pref name -> First name -> Middle name -> Client ID
        String searchTerm = searchBox.getText().toLowerCase();

        int result;

        //Last name check
        result = compareName(searchTerm, client1.getLastName(), client2.getLastName());
        if (result != 0) {
            return result;
        }

        //Preferred name check
        result = compareName(searchTerm, client1.getPreferredNameOnly(), client2.getPreferredNameOnly());
        if (result != 0) {
            return result;
        }

        //First name check
        result = compareName(searchTerm, client1.getFirstName(), client2.getFirstName());
        if (result != 0) {
            return result;
        }

        //Middle name check
        result = compareName(searchTerm, client1.getMiddleName(), client2.getMiddleName());
        if (result != 0) {
            return result;
        }

        return client1.getUid() - client2.getUid();
    }

    /**
     * Initialize the table columns.
     * The client must have getters for these specific names specified in the PV Factories.
     */
    private void setupTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("uid"));
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
        //Use the custom name comparator for sorting when sorting on the name column
        nameCol.setComparator(this::compareNames);
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        regionCol.setCellValueFactory(new PropertyValueFactory<>("region"));
        donorCol.setCellValueFactory(new PropertyValueFactory<>("donor"));
        receiverCol.setCellValueFactory(new PropertyValueFactory<>("receiver"));

        //Setup a table sortOrder change listener, so that whenever another sort is removed, the table updates to
        // default sort by name ascending.
        ObservableList<TableColumn<Client, ?>> sortOrder = tableView.getSortOrder();
        sortOrder.add(nameCol);
        sortOrder.addListener((ListChangeListener<TableColumn<Client, ?>>) c -> {
            if (sortOrder.size() == 0) {
                nameCol.sortTypeProperty().setValue(SortType.ASCENDING);
                sortOrder.add(nameCol);
            }
        });

        // Setting the donor and receiver columns to have ticks if the client is a donor or receiver
        donorCol.setCellFactory(tc -> new TableCell<Client, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null :
                        item ? "\u2713" : "");
            }
        });

        receiverCol.setCellFactory(tc -> new TableCell<Client, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null :
                        item ? "\u2713" : "");
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
                                .setAsClinViewClientWindow()
                                .viewClient(client)
                                .build());
                        PageNavigator.loadPage(Page.VIEW_CLIENT, newMain);
                    }
                }
            }
        });
    }

    private void changeClientType(String newClientType) {
        switch (newClientType) {
            case "Only Donor":
                organsRequestingFilter.getCheckModel().clearChecks();
                donatingFilterBox.setManaged(true);
                donatingFilterBox.setVisible(true);
                requestingFilterBox.setManaged(false);
                requestingFilterBox.setVisible(false);
                break;
            case "Only Receiver":
                organsDonatingFilter.getCheckModel().clearChecks();
                requestingFilterBox.setManaged(true);
                requestingFilterBox.setVisible(true);
                donatingFilterBox.setManaged(false);
                donatingFilterBox.setVisible(false);
                break;
            case "Neither":
                organsRequestingFilter.getCheckModel().clearChecks();
                organsDonatingFilter.getCheckModel().clearChecks();
                requestingFilterBox.setManaged(false);
                requestingFilterBox.setVisible(false);
                donatingFilterBox.setManaged(false);
                donatingFilterBox.setVisible(false);
                break;
            default:
                requestingFilterBox.setManaged(true);
                requestingFilterBox.setVisible(true);
                donatingFilterBox.setManaged(true);
                donatingFilterBox.setVisible(true);
                break;
        }
        repaginate();
    }

    private boolean nameFilter(Client client) {
        String searchText = searchBox.getText();
        return searchText == null || searchText.length() == 0 || client.profileSearch(searchText);
    }

    /**
     * Checks if the clients age falls within the ageSlider current max and min parameters.
     * @param client the client whose age is being compared.
     * @return true if the clients age falls within the ageSlider current range.
     */
    private boolean ageFilter(Client client) {
        return ageSlider.getLowValue() <= client.getAge() && client.getAge() <= ageSlider.getHighValue();
    }


    /**
     * Checks if a client has the birth gender in the birthGender CheckComboBox. If so, returns true, otherwise false.
     * @param client the client whos birth gender is being checked
     * @return true if the client has the selected birth gender. False otherwise.
     */
    private boolean birthGenderFilter(Client client) {
        Collection<Gender> gendersToFilter = birthGenderFilter.getCheckModel().getCheckedItems();
        return gendersToFilter.size() == 0 || gendersToFilter.contains(client.getGender());
    }

    /**
     * Checks if a client is from a region in the regions CheckComboBox. If so, returns true, otherwise false.
     * @param client the client whos region is being checked
     * @return true if the client is from a region selected. False otherwise.
     */
    private boolean regionFilter(Client client) {
        Collection<Region> regionsToFilter = regionFilter.getCheckModel().getCheckedItems();
        return regionsToFilter.size() == 0 || regionsToFilter.contains(client.getRegion());
    }

    private boolean donatingFilter(Client client) {
        Collection<Organ> organsToFilter = organsDonatingFilter.getCheckModel().getCheckedItems();
        return organsToFilter.size() == 0 || organsToFilter.stream()
                .anyMatch(organ -> client.getCurrentlyDonatedOrgans().contains(organ));
    }

    private boolean requestingFilter(Client client) {
        Collection<Organ> organsToRequestFilter = organsRequestingFilter.getCheckModel().getCheckedItems();
        return organsToRequestFilter.size() == 0 || organsToRequestFilter.stream()
                .anyMatch(organ -> client.getCurrentlyRequestedOrgans().contains(organ));
    }

    /**
     * Checks all filters in the controller and returns whether or not the client matches all the filters.
     * @param client the client who is being compared for each filter.
     * @return true if the client matches all the filters. False otherwise.
     */
    private boolean filter(Client client) {
        if (nameFilter(client) && regionFilter(client) && birthGenderFilter(client) && ageFilter(client)) {
            switch (clientTypeFilter.getValue()) {
                case "Any":
                    return donatingFilter(client) && requestingFilter(client);
                case "Only Donor":
                    return client.isDonor() && donatingFilter(client);
                case "Only Receiver":
                    return client.isReceiver() && requestingFilter(client);
                case "Neither":
                    return !client.isReceiver() && !client.isDonor();
                case "Both":
                    return client.isReceiver() && client.isDonor() &&
                            donatingFilter(client) && requestingFilter(client);
            }
        }
        return false;
    }

    /**
     * Re-paginates the clients table.
     */
    private void repaginate() {
        filteredClients.setPredicate(this::filter);

        // If the pagination count wont change, force a refresh of the page, if it will, change it and that will
        // trigger the update.
        int newPageCount = Math.max(1, (filteredClients.size() + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE);
        if (pagination.getPageCount() == newPageCount) {
            createPage(pagination.getCurrentPageIndex());
        } else {
            pagination.setPageCount(newPageCount);
        }

        // Remove the current sorting, and will be reset to name.
        tableView.getSortOrder().remove(0);
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
        if (sortedClients.size() < 2 || fromIndex + 1 == toIndex) {
            // 0 or 1 items OR the last item, on its own page
            displayingXToYOfZText.setText(String.format("Displaying %d of %d",
                    sortedClients.size(),
                    sortedClients.size()));
        } else {
            displayingXToYOfZText.setText(String.format("Displaying %d-%d of %d", fromIndex + 1, toIndex,
                    sortedClients.size()));
        }
        return new Pane();
    }
}
