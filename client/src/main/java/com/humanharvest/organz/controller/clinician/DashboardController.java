package com.humanharvest.organz.controller.clinician;

import java.util.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.DashboardStatistics;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.State;

public class DashboardController extends SubController {

    private final Session session;
    private Clinician clinician;
    private DashboardStatistics statistics;





    @FXML
    private Label totalClientsNum, organsNum, matchesNum;

    @FXML
    private PieChart pieChart;

    public DashboardController() {
        session = State.getSession();

        // TODO make work with either admin or clinician
        clinician = session.getLoggedInClinician();
    }


    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Dashboard");

        statistics = State.getClientManager().getStatistics();
        totalClientsNum.setText(String.valueOf(statistics.getClientCount()));
        organsNum.setText(String.valueOf(statistics.getOrganCount()));
        matchesNum.setText(String.valueOf(statistics.getRequestCount()));

        generatePieChartData();
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

    /**
     * Initialize the page.
     */
    @FXML
    private void initialize() {

    }
}
