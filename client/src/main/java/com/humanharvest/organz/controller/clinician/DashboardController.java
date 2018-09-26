package com.humanharvest.organz.controller.clinician;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DashboardStatistics;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.controller.components.DeceasedDonorCell;
import com.humanharvest.organz.controller.components.DonatedOrganCell;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext;

import org.apache.commons.io.IOUtils;

public class DashboardController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());

    private final Session session;
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

    private ObservableList<DonatedOrgan> observableOrgansToDonate = FXCollections.observableArrayList();
    private Map<Client, Image> profilePictureStore = new HashMap<>();
    private Map<Organ, Image> organImageMap = generateOrganPictureStore();

    public DashboardController() {
        session = State.getSession();
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
                new Data("Donors", statistics.getDonorCount()),
                new Data("Receivers", statistics.getReceiverCount()),
                new Data("Both", statistics.getDonorReceiverCount()),
                new Data("Neither", statistics.getNeitherCount())

        );
        pieChart.setData(pieChartData);
    }

    @Override
    public void refresh() {
        statistics = manager.getStatistics();
        totalClientsNum.setText(String.valueOf(statistics.getClientCount()));
        organsNum.setText(String.valueOf(statistics.getOrganCount()));
        requestNum.setText(String.valueOf(statistics.getRequestCount()));

        deceasedDonorsList.getItems().setAll(manager.getViableDeceasedDonors());

        generatePieChartData();
        updateOrgansToDonateList();

        profilePictureStore.clear();
    }

    /**
     * Initialize the page.
     */
    @FXML
    private void initialize() {

        updateOrgansToDonateList();

        deceasedDonorsList.setItems((FXCollections.observableArrayList(manager.getViableDeceasedDonors())));

        deceasedDonorsList.setCellFactory(param -> {
            DeceasedDonorCell item = new DeceasedDonorCell(profilePictureStore);
            item.setMaxWidth(deceasedDonorsList.getWidth());

            return item;
        });

        deceasedDonorsList.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                Client client = deceasedDonorsList.getSelectionModel().getSelectedItem();
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

        expiringOrgansList.setItems(observableOrgansToDonate);

        expiringOrgansList.setCellFactory(param -> {
            DonatedOrganCell item = new DonatedOrganCell(organImageMap);
            item.setMaxWidth(expiringOrgansList.getWidth());

            return item;
        });

        // Attach timer to update table each second (for time until expiration)
//        Timeline clock = new Timeline(new KeyFrame(
//                javafx.util.Duration.millis(1000),
//                event -> {
////                    expiringOrgansList.refresh();
//                    observableOrgansToDonate.removeIf(donatedOrgan ->
//                            donatedOrgan.getOverrideReason() != null ||
//                                    donatedOrgan.getDurationUntilExpiry() != null &&
//                                            donatedOrgan.getDurationUntilExpiry().minusSeconds(1).isNegative());
//                }));
//        clock.setCycleCount(Animation.INDEFINITE);
//        clock.play();
        updateOrgansToDonateList();
    }

    private void updateOrgansToDonateList() {
        observableOrgansToDonate = FXCollections.observableArrayList(manager.getAllOrgansToDonate
                ().stream().filter(o -> o.getDurationUntilExpiry() != null).collect(Collectors.toList()));
        expiringOrgansList.getItems().setAll(observableOrgansToDonate);

    }

    private Map<Organ, Image> generateOrganPictureStore() {
        Map<Organ, Image> organPictureStore = new HashMap<>();

        for (Organ organ : Organ.values()) {
            organPictureStore.put(organ, getOrganImage(organ));
        }

        return organPictureStore;
    }

    private Image getOrganImage(Organ organ) {
        byte[] bytes;

        try (InputStream in = getClass().getResourceAsStream("/images/" + organ.toString() + ".png")) {
            bytes = IOUtils.toByteArray(in);
            return new Image(new ByteArrayInputStream(bytes));

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Organ image failed to load");
            return null;
        }
    }
}
