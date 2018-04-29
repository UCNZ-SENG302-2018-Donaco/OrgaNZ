package seng302.Controller.Clinician;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

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
    private Button filterButton;

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

    private Collection<Region> regionsToFilter;
    private Collection<Organ> organsToFilter;
    private ObservableList<TransplantRequest> observableTransplantRequests = FXCollections.observableArrayList();
    private FilteredList<TransplantRequest> filteredTransplantRequests;
    private SortedList<TransplantRequest> sortedTransplantRequests;

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.loadSidebar(sidebarPane);
    }


    @FXML
    private void initialize() {
        Collection<TransplantRequest> transplantWaitingList = State.getClientManager().getTransplantWaitingList();

        setupTable();
        tableView.setOnSort((o) -> pagination.setPageCount(sortedTransplantRequests.size() / ROWS_PER_PAGE + 1));

        filteredTransplantRequests = new FilteredList<>(FXCollections.observableArrayList(transplantWaitingList), client -> true);

        sortedTransplantRequests = new SortedList<>(filteredTransplantRequests);
        sortedTransplantRequests.comparatorProperty().bind(tableView.comparatorProperty());


        pagination.setPageCount(sortedTransplantRequests.size() / ROWS_PER_PAGE + 1);

        observableTransplantRequests.setAll(sortedTransplantRequests);
        tableView.setItems(sortedTransplantRequests);

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
        regionsToFilter = new ArrayList<>();
        for (int i = 0; i < Region.values().length; i++) {
            if (regionChoice.getItemBooleanProperty(i).getValue()) {
                regionsToFilter.add(Region.fromString(regionChoice.getItemBooleanProperty(i).getBean().toString()));
            }
        }
    }

    private void filterOrgans() {
        organsToFilter = new ArrayList<>();
        for (int i = 0; i < Organ.values().length; i++) {
            if (organChoice.getItemBooleanProperty(i).getValue()) {
                organsToFilter.add(Organ.fromString(organChoice.getItemBooleanProperty(i).getBean().toString()));
            }
        }
    }



    /**
     * Used to filter out the transplant waiting list based on what organ or region or both is chosen
     */

    @FXML
    private void filter() {
        filterRegions();
        filterOrgans();

        filteredTransplantRequests.setPredicate(transplantRequest ->
                (regionsToFilter.contains(transplantRequest.getClientRegion()) || regionsToFilter.size() == 0) &&
                (organsToFilter.contains(transplantRequest.getRequestedOrgan()) || organsToFilter.size() == 0));
    }




}