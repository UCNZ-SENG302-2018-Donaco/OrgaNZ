package com.humanharvest.organz.controller.clinician;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import org.controlsfx.control.Notifications;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DashboardStatistics;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.controller.components.DeceasedDonorCell;
import com.humanharvest.organz.controller.components.DonatedOrganCell;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext;

public class DashboardController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());

    private DashboardStatistics statistics;
    private final ClientManager manager;

    @FXML
    private Pane menuBarPane;

    @FXML
    private Label totalClientsNum, organsNum, requestNum;

    @FXML
    private PieChart pieChart;

    @FXML
    private ListView<Client> deceasedDonorsList;

    @FXML
    private ListView<DonatedOrgan> expiringOrgansList;

    private final ObservableList<DonatedOrgan> observableOrgansToDonate = FXCollections.observableArrayList();

    private Map<Client, Image> profilePictureStore = new HashMap<>();
    private Map<Organ, Image> organImageMap = new HashMap<>();

    public DashboardController() {
        manager = State.getClientManager();
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Dashboard");
        mainController.loadNavigation(menuBarPane);

        refresh();
    }

    private void generatePieChartData() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new Data("Donors (" + statistics.getDonorCount() + ")", statistics.getDonorCount()),
                new Data("Receivers (" + statistics.getReceiverCount() + ")", statistics.getReceiverCount()),
                new Data("Both (" + statistics.getDonorReceiverCount() + ")", statistics.getDonorReceiverCount()),
                new Data("Neither (" + statistics.getNeitherCount() + ")", statistics.getNeitherCount())
        );
        pieChart.setData(pieChartData);
    }

    @Override
    public void refresh() {
        updateStatistics();
        updateRecentlyDeceasedDonors();
        updateOrgansToDonateList();
        // Clear the cache of profile pictures (means they will be retrieved again)
        profilePictureStore.clear();
    }

    /**
     * Initialize the page.
     */
    @FXML
    private void initialize() {
        // Organs to donate setup
        expiringOrgansList.setItems(observableOrgansToDonate);
        expiringOrgansList.setCellFactory(param -> {
            DonatedOrganCell item = new DonatedOrganCell(organImageMap);
            item.setMaxWidth(expiringOrgansList.getWidth());
            return item;
        });

        // Recently deceased donors setup
        deceasedDonorsList.setItems(FXCollections.observableArrayList(manager.getViableDeceasedDonors()));
        deceasedDonorsList.setCellFactory(param -> {
            DeceasedDonorCell item = new DeceasedDonorCell(profilePictureStore);
            item.setMaxWidth(deceasedDonorsList.getWidth());
            return item;
        });

        // Double clicking to open a deceased donor's profile
        deceasedDonorsList.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                Client client = deceasedDonorsList.getSelectionModel().getSelectedItem();
                if (client != null) {
                    MainController newMain = PageNavigator.openNewWindow(mainController);
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

    private void updateStatistics() {
        Task<DashboardStatistics> task = new Task<DashboardStatistics>() {
            @Override
            protected DashboardStatistics call() throws ServerRestException {
                return manager.getStatistics();
            }
        };

        task.setOnSucceeded(success -> {
            statistics = manager.getStatistics();
            totalClientsNum.setText(String.valueOf(statistics.getClientCount()));
            organsNum.setText(String.valueOf(statistics.getOrganCount()));
            requestNum.setText(String.valueOf(statistics.getRequestCount()));
            generatePieChartData();
        });

        task.setOnFailed(fail -> {
            LOGGER.log(Level.SEVERE, task.getException().getMessage(), task.getException());
            Notifications.create()
                    .title("Server Error")
                    .text("Could not retrieve statistics.")
                    .showError();
        });

        new Thread(task).start();
    }

    private void updateRecentlyDeceasedDonors() {
        Task<List<Client>> task = new Task<List<Client>>() {
            @Override
            protected List<Client> call() throws ServerRestException {
                return manager.getViableDeceasedDonors();
            }
        };

        task.setOnSucceeded(success -> deceasedDonorsList.getItems().setAll(task.getValue()));

        task.setOnFailed(fail -> {
            LOGGER.log(Level.SEVERE, task.getException().getMessage(), task.getException());
            Notifications.create()
                    .title("Server Error")
                    .text("Could not retrieve recently deceased donor data.")
                    .showError();
        });

        new Thread(task).start();
    }

    private void updateOrgansToDonateList() {
        Task<Collection<DonatedOrgan>> task = new Task<Collection<DonatedOrgan>>() {
            @Override
            protected Collection<DonatedOrgan> call() throws ServerRestException {
                return manager.getAllOrgansToDonate();
            }
        };

        task.setOnSucceeded(success -> observableOrgansToDonate.setAll(task.getValue()));

        task.setOnFailed(fail -> {
            LOGGER.log(Level.SEVERE, task.getException().getMessage(), task.getException());
            Notifications.create()
                    .title("Server Error")
                    .text("Could not retrieve recently donated organs data.")
                    .showError();
        });

        new Thread(task).start();
    }
}
