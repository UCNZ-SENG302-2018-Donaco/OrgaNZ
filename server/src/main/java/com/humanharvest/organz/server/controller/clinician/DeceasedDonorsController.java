package com.humanharvest.organz.server.controller.clinician;

import java.util.List;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides handlers for requests to these endpoints:
 * - GET /viableDeceasedDonors
 */
@RestController
public class DeceasedDonorsController {

    /**
     * Retrieves a list of all deceased donors that have available organs that have not yet been expired or overridden
     * These donors are sorted by time of death (most recent first)
     *
     * @param authToken The authentication token for the request.
     * @return HTTP response with a JSON body representing the viable deceased donors
     * @throws AuthenticationException If the auth token does not belong to a clinician/admin.
     */
    @GetMapping("/viableDeceasedDonors")
    public ResponseEntity<List<Client>> getDeceasedDonors(
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException {

        // Verify that request has clinician/admin authorization - otherwise 401 Unauthorised
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        List<Client> deceasedDonors = State.getClientManager().getViableDeceasedDonors();

        return new ResponseEntity<>(deceasedDonors, HttpStatus.OK);
    }


}
