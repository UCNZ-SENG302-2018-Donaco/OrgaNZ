package seng302.Controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import seng302.Donor;
import seng302.State;
import seng302.Utilities.Gender;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;
import seng302.Utilities.Region;

import java.io.IOException;
import java.util.ArrayList;

public class SearchDonorsController {

    private int rowsPerPage = 30;

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

    @FXML
    private void initialize() {
        ArrayList<Donor> allDonors = State.getDonorManager().getDonors();

        ClinicianSidebarController.loadSidebar(sidebarPane);

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
        pagination.setPageCount(sortedDonors.size() / rowsPerPage + 1);
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
                    tooltip.setText(
                            donor.getFirstName() + " " + donor.getLastName() + ". Donor of: " + donor.getDonorOrgans()
                                    + " with blood type " + donor.getBloodType());
                    setTooltip(tooltip);
                }
            }
        });

        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                    Donor donor = tableView.getSelectionModel().getSelectedItem();
                    try {
                        State.setPageParam("viewUserId", donor.getUid());
                        PageNavigator.openNewWindow(Page.VIEW_DONOR.getPath());
                    } catch (IOException exc) {
                        exc.printStackTrace();
                    }
                }
            }
        });
    }


    /**
     * Upon filtering update, refresh the filters to the new string and update pagination
     * Every refresh triggers the pagination to update and go to page zero
     */
    private void refresh() {
        String searchText = searchBox.getText();
        if (searchText == null || searchText.length() == 0) {
            filteredDonors.setPredicate(donor -> true);
        }
        else {
            filteredDonors.setPredicate(donor -> donor.nameContains(searchText));
        }

        //If the pagination count wont change, force a refresh of the page, if it will, change it and that will trigger the update.
        int newPageCount = filteredDonors.size() / rowsPerPage + 1;
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
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, filteredDonors.size());
        observableDonorList.setAll(sortedDonors.subList(fromIndex, toIndex));
        return new Pane();
    }
}
