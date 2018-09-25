package com.humanharvest.organz.controller.clinician;

import javafx.fxml.FXML;
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
    }

    /**
     * Initialize the page.
     */
    @FXML
    private void initialize() {

    }
}
