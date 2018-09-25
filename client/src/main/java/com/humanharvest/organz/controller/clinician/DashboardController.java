package com.humanharvest.organz.controller.clinician;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.DashboardStatistics;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.controller.components.DeceasedDonorCell;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.State;

public class DashboardController extends SubController {

    private final Session session;
    private Clinician clinician;
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

    public DashboardController() {
        session = State.getSession();
        manager = State.getClientManager();
        // TODO make work with either admin or clinician
        clinician = session.getLoggedInClinician();
    }



    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Dashboard");
        mainController.loadNavigation(menuBarPane);


        refresh();
    }

    public void generatePieChartData(){
        pieChart.setTitle("User Statistics");
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new Data("Donors",statistics.getDonorCount()),
            new Data("Receivers",statistics.getReceiverCount()),
            new Data("Both",statistics.getDonorReceiverCount()),
            new Data("Neither",statistics.getNeitherCount())

        );
        pieChart.setData(pieChartData);
    }

    @Override
    public void refresh() {
        statistics = State.getClientManager().getStatistics();
        totalClientsNum.setText(String.valueOf(statistics.getClientCount()));
        organsNum.setText(String.valueOf(statistics.getOrganCount()));
        requestNum.setText(String.valueOf(statistics.getRequestCount()));

        deceasedDonorsList.getItems().setAll(State.getClientManager().getViableDeceasedDonors());



        generatePieChartData();
    }

    /**
     * Initialize the page.
     */
    @FXML
    private void initialize() {
        deceasedDonorsList.setItems((FXCollections.observableArrayList(State.getClientManager().getViableDeceasedDonors())));

        deceasedDonorsList.setCellFactory(param -> {
            DeceasedDonorCell item = new DeceasedDonorCell();
            item.setMaxWidth(deceasedDonorsList.getWidth());
            System.out.println(deceasedDonorsList.getWidth());

            return item;
        });

       // deceasedDonorsList.wi(450);



    }
}
