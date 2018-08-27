package com.humanharvest.organz.server.controller.clinician;

import java.util.List;
import java.util.Optional;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.validators.client.DonatedOrganValidator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides handlers for requests to these endpoints:
 * - GET /matchOrganToRecipients
 */
@RestController
public class MatchOrgansToRecipientsController {

    /**
     * Retrieves a list of all clients who are potential recipients of the organ passed in.
     * They are sorted from most to least eligble, with most eligible being the first item in the list.
     *
     * @param id The id of the organ being donated.
     * @param authToken The authentication token for the request.
     * @return HTTP response with a JSON body representing the potential recipients.
     * @throws AuthenticationException If the auth token does not belong to a clinician/admin.
     */
    @GetMapping("/matchOrganToRecipients/{id}")
    public ResponseEntity<List<Client>> getMatchesForOrgan(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException {

        // Check if there is a donated organ with that id - otherwise 404 not found
        Optional<DonatedOrgan> donatedOrganOptional =
                State.getClientManager().getAllOrgansToDonate().stream()
                        .filter(donatedOrgan1 -> donatedOrgan1.getId().equals(id)).findFirst();
        DonatedOrgan donatedOrgan;

        // Verify that request has clinician/admin authorization - otherwise 401 Unauthorised
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);
        //Collection<TransplantRequest> allTransplantRequests = State.getClientManager().getAllTransplantRequests();

        if (donatedOrganOptional.isPresent()) {
            donatedOrgan = donatedOrganOptional.get();
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Check that the donated organ is valid - otherwise 400 bad request
        if (!DonatedOrganValidator.isValid(donatedOrgan)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Client> potentialMatches = State.getClientManager().getOrganMatches(donatedOrgan);

        return new ResponseEntity<>(potentialMatches, HttpStatus.OK);

    }

}
