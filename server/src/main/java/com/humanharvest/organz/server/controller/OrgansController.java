package com.humanharvest.organz.server.controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.client.CancelManualOverrideAction;
import com.humanharvest.organz.actions.client.EditManualOverrideAction;
import com.humanharvest.organz.actions.client.ManuallyOverrideOrganAction;
import com.humanharvest.organz.server.exceptions.GlobalControllerExceptionHandler;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.DonatedOrganSortOptionsEnum;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.views.SingleStringView;
import com.humanharvest.organz.views.client.DonatedOrganView;
import com.humanharvest.organz.views.client.PaginatedDonatedOrgansList;
import com.humanharvest.organz.views.client.Views;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrgansController {

    /**
     * The GET endpoint for getting all organs currently available to be donated
     * @param authToken authentication token - only clinicians and administrators can access donatable organs
     * @return response entity containing all organs that are available for donation
     * @throws GlobalControllerExceptionHandler.InvalidRequestException
     */
    @JsonView(Views.Overview.class)
    @GetMapping("/clients/organs")
    public ResponseEntity<PaginatedDonatedOrgansList> getOrgansToDonate(
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer count,
            @RequestParam(required = false) Set<String> regions,
            @RequestParam(value = "organType", required = false) Set<Organ> organsToFilter,
            @RequestParam(required = false) DonatedOrganSortOptionsEnum sortOption,
            @RequestParam(required = false) Boolean reversed)
            throws GlobalControllerExceptionHandler.InvalidRequestException {

        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        final Set<String> regionsToFilter = new HashSet<>();
        if (regions != null) {
            for (String region : regions) {
                regionsToFilter.add(region.replaceAll("%20", " "));
            }
        }

        PaginatedDonatedOrgansList paginatedDonatedOrgansList = State.getClientManager().getAllOrgansToDonate(
                offset, count,
                regionsToFilter,
                organsToFilter,
                sortOption, reversed);

        return new ResponseEntity<>(paginatedDonatedOrgansList, HttpStatus.OK);
    }

    /**
     * GET endpoint for getting a clients donated organs
     * @param uid the uid of the client
     * @param authToken authorization token
     * @return response entity containing the clients donated organs
     */
    @JsonView(Views.Overview.class)
    @GetMapping("/clients/{uid}/donatedOrgans")
    public ResponseEntity<Collection<DonatedOrganView>> getClientDonatedOrgans(
            @PathVariable int uid,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        Optional<Client> optionalClient = State.getClientManager().getClientByID(uid);

        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();

            //Auth check
            State.getAuthenticationManager().verifyClientAccess(authToken, client);

            //Add the ETag to the headers
            HttpHeaders headers = new HttpHeaders();
            headers.add("ETag", client.getETag());

            Collection<DonatedOrganView> donatedOrgans = client.getDonatedOrgans().stream()
                    .map(DonatedOrganView::new)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(donatedOrgans, headers, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * POST endpoint for manually overriding an available organ.
     * @param uid The UID of the client the organ was donated by.
     * @param id The ID of the available organ.
     * @param overrideReason The reason to override this organ.
     * @param authToken Authentication token - only clinicians and administrators can override available organs.
     * @return Response entity containing the overriden organ.
     * @throws GlobalControllerExceptionHandler.InvalidRequestException If the organ has already been overriden, or if
     * the reason given is blank.
     */
    @JsonView(Views.Details.class)
    @PostMapping("/clients/{uid}/donatedOrgans/{id}/override")
    public ResponseEntity<DonatedOrgan> manuallyExpireOrgan(
            @PathVariable int uid,
            @PathVariable int id,
            @RequestBody SingleStringView overrideReason,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws GlobalControllerExceptionHandler.InvalidRequestException {

        // Check that the request is by an authorised user.
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        // Check that the UID corresponds to an existing client.
        Client client = State.getClientManager().getClientByID(uid).orElse(null);
        if (client == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Check that the UID corresponds to an existing donated organ.
        DonatedOrgan donatedOrgan = client.getDonatedOrganById(id);
        if (donatedOrgan == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // The organ in question cannot already have been overriden, and the reason given must not be blank.
        if (donatedOrgan.getOverrideReason() != null || overrideReason.getValue().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Create the action and execute it
        Action action = new ManuallyOverrideOrganAction(donatedOrgan, overrideReason.getValue(),
                State.getClientManager());
        State.getActionInvoker(authToken).execute(action);

        // Retrieve client again (has to be done due to some weird caching issues).
        client = State.getClientManager()
                .getClientByID(client.getUid())
                .orElseThrow(IllegalStateException::new);

        // Return the now overriden version of the donated organ along with the client's new ETag.
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.getETag());
        return new ResponseEntity<>(donatedOrgan, headers, HttpStatus.OK);
    }

    /**
     * DELETE endpoint for cancelling a manual override on an available organ.
     * @param uid The UID of the client the organ was donated by.
     * @param id The ID of the available organ.
     * @param authToken Authentication token - only clinicians and administrators can override available organs.
     * @return Response entity containing the overriden organ.
     * @throws GlobalControllerExceptionHandler.InvalidRequestException If the organ has not yet been overriden.
     */
    @JsonView(Views.Details.class)
    @DeleteMapping("/clients/{uid}/donatedOrgans/{id}/override")
    public ResponseEntity<DonatedOrgan> cancelManualOverride(
            @PathVariable int uid,
            @PathVariable int id,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws GlobalControllerExceptionHandler.InvalidRequestException {

        // Check that the request is by an authorised user.
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        // Check that the UID corresponds to an existing client.
        Client client = State.getClientManager().getClientByID(uid).orElse(null);
        if (client == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Check that the UID corresponds to an existing donated organ.
        DonatedOrgan donatedOrgan = client.getDonatedOrganById(id);
        if (donatedOrgan == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // The organ in question must have been overriden.
        if (donatedOrgan.getOverrideReason() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Create the action and execute it
        Action action = new CancelManualOverrideAction(donatedOrgan, State.getClientManager());
        State.getActionInvoker(authToken).execute(action);

        // Retrieve client again (has to be done due to some weird caching issues).
        client = State.getClientManager()
                .getClientByID(client.getUid())
                .orElseThrow(IllegalStateException::new);

        // Return the no longer overriden version of the donated organ along with the client's new ETag.
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.getETag());
        return new ResponseEntity<>(donatedOrgan, headers, HttpStatus.OK);
    }

    /**
     * PATCH endpoint for manually overriding an available organ.
     * @param uid The UID of the client the organ was donated by.
     * @param id The ID of the available organ.
     * @param newOverrideReason The new reason to override this organ.
     * @param authToken Authentication token - only clinicians and administrators can override available organs.
     * @return Response entity containing the overriden organ.
     * @throws GlobalControllerExceptionHandler.InvalidRequestException If the organ has already been overriden, or if
     * the reason given is blank.
     */
    @JsonView(Views.Details.class)
    @PatchMapping("/clients/{uid}/donatedOrgans/{id}/override")
    public ResponseEntity<DonatedOrgan> editManualOverride(
            @PathVariable int uid,
            @PathVariable int id,
            @RequestBody SingleStringView newOverrideReason,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws GlobalControllerExceptionHandler.InvalidRequestException {

        // Check that the request is by an authorised user.
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        // Check that the UID corresponds to an existing client.
        Client client = State.getClientManager().getClientByID(uid).orElse(null);
        if (client == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Check that the UID corresponds to an existing donated organ.
        DonatedOrgan donatedOrgan = client.getDonatedOrganById(id);
        if (donatedOrgan == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // The organ in question must have been overriden.
        if (donatedOrgan.getOverrideReason() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Create the action and execute it
        Action action = new EditManualOverrideAction(donatedOrgan, newOverrideReason.getValue(),
                State.getClientManager());
        State.getActionInvoker(authToken).execute(action);

        // Retrieve client again (has to be done due to some weird caching issues).
        client = State.getClientManager()
                .getClientByID(client.getUid())
                .orElseThrow(IllegalStateException::new);

        // Return the new version of the overriden donated organ along with the client's new ETag.
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.getETag());
        return new ResponseEntity<>(donatedOrgan, headers, HttpStatus.OK);
    }
}
