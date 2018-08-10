package com.humanharvest.organz.server.controller.clinician;

import java.util.ArrayList;
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

/**
 * Provides handlers for requests to these endpoints:
 * - GET /matchOrganToRecipients
 */
public class MatchOrgansToRecipientsController {

    public boolean agesMatch(int donorAge, int recipientAge) {
        // If one is under 12, they must both be under 12
        if (donorAge < 12 || recipientAge < 12) {
            return donorAge < 12 && recipientAge < 12;
        }

        // Otherwise (aged 12+), they must have a max age diff of 15
        return Math.abs(donorAge - recipientAge) <= 15;
    }

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

        List<TransplantRequest> potentialTransplantRequests = new ArrayList<>();
        List<Client> potentialMatches = new ArrayList<>();

        // If the organ trying to be matched has expired, then return an empty list
        if (donatedOrgan.hasExpired()) {
            return new ResponseEntity<>(potentialMatches, HttpStatus.OK);
        }

        // Create a list of eligible transplant requests
        for (TransplantRequest transplantRequest : State.getClientManager().getAllCurrentTransplantRequests()) {
            Client donor = donatedOrgan.getDonor();
            Client recipient = transplantRequest.getClient();

            if (donatedOrgan.getOrganType().equals(transplantRequest.getRequestedOrgan())
                    && donor.getBloodType().equals(recipient.getBloodType())
                    && agesMatch(donor.getAge(), recipient.getAge())) {
                potentialTransplantRequests.add(transplantRequest);
            }
        }

        return new ResponseEntity<>(potentialMatches, HttpStatus.OK);

    }

}
