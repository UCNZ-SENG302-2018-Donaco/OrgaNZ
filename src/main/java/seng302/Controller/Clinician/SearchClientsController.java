package seng302.Controller.Clinician;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observer;

import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
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

import seng302.Client;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.State.State;
import seng302.Utilities.Enums.Gender;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;
import seng302.Utilities.View.WindowContext;

public class SearchClientsController extends SubController {

    private static final int ROWS_PER_PAGE = 30;

    @FXML
    private TextField searchBox;

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
    private FilteredList<Client> filteredClients;
    private SortedList<Client> sortedClients;

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Client search");
        mainController.loadMenuBar(menuBarPane);
    }

    @FXML
    private void initialize() {
        ArrayList<Client> allClients = State.getClientManager().getClients();

        setupTable();

        tableView.setOnSort((o) -> createPage(pagination.getCurrentPageIndex()));
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
     * Compares the names based off the priority Last name -> Pref name -> First name -> Middle name [-> suffix/prefixes]
     * @param c1 The first client object being compared
     * @param c2 The second client object being compared
     * @return -1 if c1 is higher priority. 1 if c1 is lower priority. 0 if equal priority.
     */
    private int compareNames(Client c1, Client c2) {
        //Last name -> Pref name -> First name -> Middle name [-> suffix/prefixes]
        String searchTerm = searchBox.getText().toLowerCase();


        if (c1.getLastName().toLowerCase().startsWith(searchTerm)) {
            if (c2.getLastName().toLowerCase().startsWith(searchTerm)) {
                return c1.getLastName().compareTo(c2.getLastName());
            } else {
                return -1;
            }
        } else if (c2.getLastName().toLowerCase().startsWith(searchTerm)) {

            return 1;

        } else if (c1.getPreferredNameOnly().toLowerCase().startsWith(searchTerm)) {
            if (c2.getPreferredNameOnly().toLowerCase().startsWith(searchTerm)) {
                return c1.getPreferredNameOnly().compareTo(c2.getPreferredNameOnly());
            } else {
                return -1;
            }
        } else if (c2.getPreferredNameOnly().toLowerCase().startsWith(searchTerm)) {
            return 1;

        } else if (c1.getFirstName().toLowerCase().startsWith(searchTerm)) {
            if (c2.getFirstName().toLowerCase().startsWith(searchTerm)) {
                return c1.getFirstName().compareTo(c2.getFirstName());
            } else {
                return -1;
            }
        } else if (c2.getFirstName().toLowerCase().startsWith(searchTerm)) {
            return 1;

        } else if (c1.getMiddleName().toLowerCase().startsWith(searchTerm)) {
            if (c2.getMiddleName().toLowerCase().startsWith(searchTerm)) {
                return c1.getMiddleName().compareTo(c2.getMiddleName());
            } else {
                return -1;
            }
        } else if (c2.getMiddleName().toLowerCase().startsWith(searchTerm)) {
            return 1;
        } else {
            return 0;
        }
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

    /**
     * Upon filtering update, refresh the filters to the new string and update pagination
     * Every refresh triggers the pagination to update and go to page zero
     */
    @Override
    public void refresh() {
        String searchText = searchBox.getText();
        if (searchText == null || searchText.length() == 0) {
            filteredClients.setPredicate(client -> true);
        } else {

            filteredClients.setPredicate(client -> client.profileSearch(searchText));
        }

        //If the pagination count wont change, force a refresh of the page, if it will, change it and that will trigger the update.
        int newPageCount = Math.max(1, (filteredClients.size() + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE);
        if (pagination.getPageCount() == newPageCount) {
            createPage(pagination.getCurrentPageIndex());
        } else {
            pagination.setPageCount(newPageCount);
        }

        //Remove the current sorting, and will be reset to name.
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
