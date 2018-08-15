package com.humanharvest.organz.server.controller.clinician;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.utilities.algorithms.MatchOrganToRecipients;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Provides handlers for requests to these endpoints:
 * - GET /matchOrganToRecipients
 */
public class MatchOrgansToRecipientsController {

    /**
     * Retrieves a list of all clients who are potential recipients of the organ passed in.
     * They are sorted from most to least eligble, with most eligible being the first item in the list.
     *
     * @param donatedOrgan The organ being donated.
     * @param authToken    The authentication token for the request.
     * @return HTTP response with a JSON body representing the potential recipients.
     * @throws AuthenticationException If the auth token does not belong to a clinician/admin.
     */
    @GetMapping("/matchOrganToRecipients")
    public ResponseEntity<List<Client>> getMatchesForOrgan(
            @RequestParam(value = "donatedOrgan", required = false) DonatedOrgan donatedOrgan,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException {
        System.out.println("here");
/*
        // Verify that request has clinician/admin authorization - otherwise 401 Unauthorised
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        // Check that the donated organ is valid - otherwise 400 bad request
        if (!DonatedOrganValidator.isValid(donatedOrgan)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
*/
        List<Client> potentialMatches = MatchOrganToRecipients.getListOfPotentialRecipients(donatedOrgan);

        return new ResponseEntity<>(potentialMatches, HttpStatus.OK);

    }

}
