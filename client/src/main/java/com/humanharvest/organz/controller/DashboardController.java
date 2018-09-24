package com.humanharvest.organz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.DashboardStatistics;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.State;

public class DashboardController {

    private final Session session;
    private Clinician clinician;
    private DashboardStatistics statistics;

    @FXML
    private Label totalClientsNum, organsNum, matchesNum, clinicianName;

    public DashboardController() {
        session = State.getSession();

        // TODO make work with either admin or clinician
        clinician = session.getLoggedInClinician();
    }

    /**
     * Initialize the page.
     */
    @FXML
    private void initialize() {
        clinicianName.setText(clinician.getFullName());
    }
}
