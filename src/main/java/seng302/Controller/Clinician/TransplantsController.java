package seng302.Controller.Clinician;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

import seng302.Client;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.State.State;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;
import seng302.Utilities.View.WindowContext;

import org.controlsfx.control.CheckComboBox;

public class TransplantsController extends SubController {

    private static final int ROWS_PER_PAGE = 30;

    @FXML
    private HBox sidebarPane;

    @FXML
    private TableView<TransplantRequest> tableView;

    @FXML
    private TableColumn<TransplantRequest, LocalDateTime> dateCol;

    @FXML
    private TableColumn<TransplantRequest, String> clientCol;

    @FXML
    private TableColumn<TransplantRequest, Organ> organCol;

    @FXML
    private TableColumn<TransplantRequest, Region> regionCol;

    @FXML
    private Pagination pagination;

    @FXML
    private CheckComboBox regionChoice;
    @FXML
    private CheckComboBox organChoice;
    private ObservableList<TransplantRequest> observableTransplantList = FXCollections.observableArrayList();
    private FilteredList<TransplantRequest> filteredOrgans;
    private FilteredList<TransplantRequest> filteredRegions;
    private SortedList<TransplantRequest> sortedTransplants;

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.loadSidebar(sidebarPane);
    }

    //Note: some commented-out code is just copied and pasted from SearchClientsController

    @FXML
    private void initialize() {
        List<TransplantRequest> allTransplants = State.getClientManager().getAllTransplantRequests();

        setupTable();

        //set up the search bars
        tableView.setOnSort((o) -> pagination.setPageCount(sortedTransplants.size() / ROWS_PER_PAGE + 1));

        /*filteredOrgans = new FilteredList<>(FXCollections.observableArrayList(allTransplants), transplantRequest ->
                true);
        filteredRegions = new FilteredList<>(FXCollections.observableArrayList(allTransplants), transplantRequest ->
        true);*/

        //Create a sorted list
        sortedTransplants = new SortedList<>(FXCollections.observableArrayList(allTransplants));
        //Link the sorted list sort to the tableView sort
        sortedTransplants.comparatorProperty().bind(tableView.comparatorProperty());

        //Set initial pagination
        pagination.setPageCount(sortedTransplants.size() / ROWS_PER_PAGE + 1);/*
        //On pagination update call createPage
        pagination.setPageFactory(this::createPage);*/

        //Initialize the observable list to all clients
        observableTransplantList.setAll(sortedTransplants);
        //Bind the tableView to the observable list
        tableView.setItems(observableTransplantList);

        //Sets items for the Choice boxes for region and organ
        //regionChoice.setItems(FXCollections.observableArrayList(Region.values()));
        //organChoice.setItems(FXCollections.observableArrayList(Organ.values()));
        regionChoice.getItems().addAll(Region.values());
        organChoice.getItems().addAll(Organ.values());

    }


    /**
     * Initialize the table columns.
     * The client must have getters for these specific names specified in the PV Factories.
     */
    private void setupTable() {
        dateCol.setCellValueFactory(new PropertyValueFactory<>("requestDateString"));
        clientCol.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        organCol.setCellValueFactory(new PropertyValueFactory<>("requestedOrgan"));
        regionCol.setCellValueFactory(new PropertyValueFactory<>("clientRegion"));

        tableView.getColumns().setAll(clientCol, organCol, regionCol, dateCol);


        tableView.setRowFactory(tv -> new TableRow<TransplantRequest>() {
            private Tooltip tooltip = new Tooltip();

            @Override
            public void updateItem(TransplantRequest transplantRequest, boolean empty) {
                super.updateItem(transplantRequest, empty);
                if (transplantRequest == null) {
                    setTooltip(null);
                } else {
                    tooltip.setText(String.format("%s donating organ %s from region %s",
                            transplantRequest.getClientName(),
                            transplantRequest.getRequestedOrgan(),
                            transplantRequest.getClientRegion()));
                    setTooltip(tooltip);
                }
            }
        });

        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                Client client = tableView.getSelectionModel().getSelectedItem().getClient();
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

    private void filterRegions() {
        Collection<String> regionsToFilter = new ArrayList<>();
        for (int i = 0; i < Region.values().length; i++) {
            if (regionChoice.getItemBooleanProperty(i).getValue()) {
                regionsToFilter.add(regionChoice.getItemBooleanProperty(i).getBean().toString());
                //regionsToFilter.add(Region.valueOf(regionChoice.getItemBooleanProperty(i).getBean().toString().toUpperCase()));
            }
        }
        if (regionsToFilter.size() > 0) {
            //filteredRegions.setPredicate(region -> regionsToFilter.contains(region));
        } else {
            //filteredRegions.setPredicate(Region -> true);
        }
    }

    private void filterOrgans() {
        Collection<Organ> organsToFilter = new ArrayList<>();
        for (int i = 0; i < Organ.values().length; i++) {
            if (organChoice.getItemBooleanProperty(i).getValue()) {
                System.out.println(organChoice.getItemBooleanProperty(i).getBean());
                //regionsToFilter.add(regionChoice.getItemBooleanProperty(i).getBean())
            }
        }
    }

    /**
     * Used to filter out the transplant waiting list based on what organ or region or both is chosen
     */

    @FXML
    private void filter() {
        filterRegions();
        //filterOrgans();


        /*
        String organSearchText = organSearch.getText();
        if (organSearch == null || organSearchText.length() == 0) {
            filteredOrgans.setPredicate(transplantRequest -> true);
        } else {
            filteredOrgans.setPredicate(transplantRequest -> transplantRequest.getRequestedOrgan().equals(organSearchText));
        }

        String regionSearchText = regionSearch.getText();
        if (regionSearch == null || regionSearchText.length() == 0) {
            filteredRegions.setPredicate(transplantRequest -> true);
        } else {
            filteredRegions.setPredicate(transplantRequest -> transplantRequest.getRequestedOrgan().equals(organSearchText));
        }
        */
    }




}