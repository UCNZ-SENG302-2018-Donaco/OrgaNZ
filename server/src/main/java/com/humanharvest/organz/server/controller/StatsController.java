package com.humanharvest.organz.server.controller;

import com.humanharvest.organz.DashboardStatistics;
import com.humanharvest.organz.state.State;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {

    /**
     * Gets statistics about clients necessary for the dashboard
     * @param authToken authentication token - only clinicians and administrators can access statistics
     * @return DashboardStatistics - basic stats about clients and organs
     */
    @GetMapping("/statistics")
    public ResponseEntity<DashboardStatistics> getDashboardStatistics(
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        DashboardStatistics statistics = State.getClientManager().getStatistics();

        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

}
