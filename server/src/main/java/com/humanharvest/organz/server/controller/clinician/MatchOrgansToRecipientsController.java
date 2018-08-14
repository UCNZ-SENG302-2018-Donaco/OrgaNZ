package com.humanharvest.organz.server.controller.clinician;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import com.humanharvest.organz.utilities.algorithms.MatchOrganToRecipients;

/**
 * Provides handlers for requests to these endpoints:
 * - GET /matchOrganToRecipients
 */
public class MatchOrgansToRecipientsController {

    /**
     * Retrieves a list of all clients who are potential recipients of the organ passed in.
     * They are sorted from most to least eligble, with most eligible being the first item in the list.
     * @param donatedOrgan The organ being donated.
     * @param authToken The authentication token for the request.
     * @return HTTP response with a JSON body representing the potential recipients.
     * @throws AuthenticationException If the auth token does not belong to a clinician/admin.
     */
    @GetMapping("/matchOrganToRecipients")
    public ResponseEntity<List<Client>> getMatchesForOrgan(
            @RequestParam(value = "donatedOrgan", required = false) DonatedOrgan donatedOrgan,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException {

        // Verify that request has clinician/admin authorization
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);
        Collection<TransplantRequest> allTransplantRequests = State.getClientManager().getAllTransplantRequests();

        List<Client> potentialMatches = MatchOrganToRecipients.getListOfPotentialRecipients(donatedOrgan, allTransplantRequests);

        return new ResponseEntity<>(potentialMatches, HttpStatus.OK);

    }

}
