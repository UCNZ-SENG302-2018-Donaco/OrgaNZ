package seng302.Controller.Clinician;

import java.util.ArrayList;

import javafx.collections.FXCollections;
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

import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.Donor;
import seng302.State.State;
import seng302.Utilities.Enums.Gender;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;
import seng302.Utilities.View.WindowContext;

public class SearchDonorsController extends SubController {

    private static final int ROWS_PER_PAGE = 30;

    @FXML
    private TextField searchBox;

    @FXML
    private HBox sidebarPane;

    @FXML
    private TableView<Donor> tableView;

    @FXML
    private TableColumn<Donor, Integer> idCol;
    @FXML
    private TableColumn<Donor, String> nameCol;
    @FXML
    private TableColumn<Donor, Integer> ageCol;
    @FXML
    private TableColumn<Donor, Gender> genderCol;
    @FXML
    private TableColumn<Donor, Region> regionCol;

    @FXML
    private Pagination pagination;

    private ObservableList<Donor> observableDonorList = FXCollections.observableArrayList();
    private FilteredList<Donor> filteredDonors;
    private SortedList<Donor> sortedDonors;

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Donor search");
        mainController.loadSidebar(sidebarPane);
    }

    @FXML
    private void initialize() {
        ArrayList<Donor> allDonors = State.getDonorManager().getDonors();

        setupTable();

        tableView.setOnSort((o) -> createPage(pagination.getCurrentPageIndex()));
        searchBox.textProperty().addListener(((o) -> refresh()));

        //Create a filtered list, that defaults to allow all using lambda function
        filteredDonors = new FilteredList<>(FXCollections.observableArrayList(allDonors), donor -> true);
        //Create a sorted list that links to the filtered list
        sortedDonors = new SortedList<>(filteredDonors);
        //Link the sorted list sort to the tableView sort
        sortedDonors.comparatorProperty().bind(tableView.comparatorProperty());

        //Set initial pagination
        pagination.setPageCount(sortedDonors.size() / ROWS_PER_PAGE + 1);
        //On pagination update call createPage
        pagination.setPageFactory(this::createPage);

        //Initialize the observable list to all donors
        observableDonorList.setAll(sortedDonors);
        //Bind the tableView to the observable list
        tableView.setItems(observableDonorList);
    }

    /**
     * Initialize the table columns.
     * The donor must have getters for these specific names specified in the PV Factories.
     */
    private void setupTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("uid"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        regionCol.setCellValueFactory(new PropertyValueFactory<>("region"));

        tableView.getColumns().setAll(idCol, nameCol, ageCol, genderCol, regionCol);

        tableView.setRowFactory(tv -> new TableRow<Donor>() {
            private Tooltip tooltip = new Tooltip();

            @Override
            public void updateItem(Donor donor, boolean empty) {
                super.updateItem(donor, empty);
                if (donor == null) {
                    setTooltip(null);
                } else {
                    tooltip.setText(String.format("%s %s with blood type %s. Donating: %s",
                            donor.getFirstName(),
                            donor.getLastName(),
                            donor.getBloodType(),
                            donor.getOrganStatusString()));
                    setTooltip(tooltip);
                }
            }
        });

        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                Donor donor = tableView.getSelectionModel().getSelectedItem();
                if (donor != null) {
                    MainController newMain = PageNavigator.openNewWindow();
                    if (newMain != null) {
                        newMain.setWindowContext(new WindowContext.WindowContextBuilder()
                                .setAsClinViewDonorWindow()
                                .viewDonor(donor)
                                .build());
                        PageNavigator.loadPage(Page.VIEW_DONOR, newMain);
                    }
                }
            }
        });
    }


    /**
     * Upon filtering update, refreshTitleAndSidebar the filters to the new string and update pagination
     * Every refreshTitleAndSidebar triggers the pagination to update and go to page zero
     */
    private void refresh() {
        String searchText = searchBox.getText();
        if (searchText == null || searchText.length() == 0) {
            filteredDonors.setPredicate(donor -> true);
        } else {
            filteredDonors.setPredicate(donor -> donor.nameContains(searchText));
        }

        //If the pagination count wont change, force a refreshTitleAndSidebar of the page, if it will, change it and that will trigger the update.
        int newPageCount = filteredDonors.size() / ROWS_PER_PAGE + 1;
        if (pagination.getPageCount() == newPageCount) {
            createPage(0);
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
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredDonors.size());
        observableDonorList.setAll(sortedDonors.subList(fromIndex, toIndex));
        return new Pane();
    }
}
