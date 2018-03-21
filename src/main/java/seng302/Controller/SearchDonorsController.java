package seng302.Controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import seng302.Donor;
import seng302.State;
import seng302.Utilities.Gender;
import seng302.Utilities.Region;

import java.util.ArrayList;

public class SearchDonorsController {

    private int rowsPerPage = 30;

    @FXML
    private TextField searchBox;

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

        idCol.setCellValueFactory(new PropertyValueFactory<>("uid"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        regionCol.setCellValueFactory(new PropertyValueFactory<>("region"));

        tableView.getColumns().setAll(idCol, nameCol, ageCol, genderCol, regionCol);


        tableView.setOnSort((o) -> createPage(pagination.getCurrentPageIndex()));
        searchBox.textProperty().addListener(((o) -> refresh()));

        filteredDonors = new FilteredList<>(FXCollections.observableArrayList(allDonors), s -> true);
        sortedDonors = new SortedList<>(filteredDonors);
        sortedDonors.comparatorProperty().bind(tableView.comparatorProperty());

        pagination.setPageCount(sortedDonors.size() / rowsPerPage + 1);
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);

        observableDonorList.setAll(allDonors);
        tableView.setItems(observableDonorList);
    }

    @FXML
    private void refresh() {
        System.out.println("refresh called");
        String searchText = searchBox.getText();
        if (searchText == null || searchText.length() == 0) {
            filteredDonors.setPredicate(s -> true);
        }
        else {
            filteredDonors.setPredicate(s -> s.nameContains(searchText));
        }

        pagination.setPageCount(filteredDonors.size() / rowsPerPage + 1);
        pagination.setCurrentPageIndex(0);
    }

    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, filteredDonors.size());
        observableDonorList.setAll(sortedDonors.subList(fromIndex, toIndex));
        return new Pane();
    }
}
