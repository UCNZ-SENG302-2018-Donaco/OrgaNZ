package com.humanharvest.organz.server.controller.client;

import static com.humanharvest.organz.utilities.validators.ClientValidator.checkETag;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.client.AddTransplantRequestAction;
import com.humanharvest.organz.actions.client.ResolveTransplantRequestAction;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.validators.NotEmptyStringValidator;
import com.humanharvest.organz.utilities.validators.client.TransplantRequestValidator;
import com.humanharvest.organz.views.client.PaginatedTransplantList;
import com.humanharvest.organz.views.client.ResolveTransplantRequestObject;
import com.humanharvest.organz.views.client.TransplantRequestView;
import com.humanharvest.organz.views.client.Views;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides handlers for requests to these endpoints:
 * - GET /clients/transplantRequests
 * - GET /clients/{id}/transplantRequests
 * - POST /clients/{id}/transplantRequests
 */
@RestController
public class ClientTransplantRequestsController {

    private static final Logger LOGGER = Logger.getLogger(ClientController.class.getName());

    /**
     * Retrieves all transplant requests stored in the system matching the given criteria.
     *
     * @param offset The number of requests to skip.
     * @param count The number of requests to retrieve.
     * @param regions The list of regions to filter by. Only requests by a client that lives in one of these regions
     * will be retrieved.
     * @param organs The list of organs to filter by. Only requests for one of these organs will be retrieved.
     * @param authToken The authentication token for the request.
     * @return An HTTP response with a JSON body representing all the requests matching these criteria.
     * @throws AuthenticationException If the auth token does not belong to a clinician/admin.
     */
    @GetMapping("/clients/transplantRequests")
    @JsonView(Views.Overview.class)
    public ResponseEntity<PaginatedTransplantList> getAllTransplantRequests(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "count", required = false) Integer count,
            @RequestParam(value = "regions", required = false) Set<String> regions,
            @RequestParam(value = "organs", required = false) Set<Organ> organs,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException {

        // Verify that request has clinician/admin authorization
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        final Set<String> regionsToFilter = new HashSet<>();
        if (regions != null) {
            for (String region : regions) {
                regionsToFilter.add(region.replace("%20", " "));
            }
        }

        // Get all requests that match region/organ filters
        List<TransplantRequestView> matchingRequests = State.getClientManager().getAllTransplantRequests().stream()
                .filter(request -> regionsToFilter.isEmpty()
                        || regionsToFilter.contains(request.getClient().getRegion())
                        || (regionsToFilter.contains("International")
                        && request.getClient().getCountry() != Country.NZ))
                .filter(request -> organs == null || organs.isEmpty() || organs.contains(request.getRequestedOrgan()))
                .map(TransplantRequestView::new)
                .collect(Collectors.toList());

        // Return subset for given offset/count parameters (used for pagination)
        if (offset == null) {
            offset = 0;
        }
        if (count == null) {
            return new ResponseEntity<>(new PaginatedTransplantList(
                    matchingRequests.subList(
                            Math.min(offset, matchingRequests.size()),
                            matchingRequests.size()),
                    matchingRequests.size()),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new PaginatedTransplantList(
                    matchingRequests.subList(
                            Math.min(offset, matchingRequests.size()),
                            Math.min(offset + count, matchingRequests.size())),
                    matchingRequests.size()),
                    HttpStatus.OK);
        }
    }

    /**
     * Retrieves all transplant requests belonging to a client with a given id.
     *
     * @param id The id of the client whose requests to retrieve.
     * @param authToken The authentication token for the request.
     * @return An HTTP response with a JSON body representing all transplant requests belonging to the client.
     * @throws AuthenticationException If the auth token is not valid or does not belong to a user with view access
     * to this client.
     */
    @GetMapping("/clients/{id}/transplantRequests")
    public ResponseEntity<Collection<TransplantRequest>> getClientTransplantRequests(
            @PathVariable int id,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException {

        // Find the client
        Optional<Client> client = State.getClientManager().getClientByID(id);

        if (client.isPresent()) {
            // Verify that request has access to view the client
            State.getAuthenticationManager().verifyClientAccess(authToken, client.get());
            // Return client's transplant requests
            return new ResponseEntity<>(client.get().getTransplantRequests(), HttpStatus.OK);
        } else {
            // No client exists with that id, return 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Creates a transplant request.
     *
     * @param transplantRequest the transplant request to add
     * @param id the client's ID
     * @param eTag A hashed value of the object used for optimistic concurrency control
     * @return list of all transplant requests for that client
     */
    @PostMapping("/clients/{id}/transplantRequests")
    public ResponseEntity<Collection<TransplantRequest>> postTransplantRequest(
            @RequestBody TransplantRequest transplantRequest,
            @PathVariable int id,
            @RequestHeader(value = "If-Match", required = false) String eTag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        // Check authentication
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        // Get the client given by the ID
        Optional<Client> optionalClient = State.getClientManager().getClientByID(id);
        if (!optionalClient.isPresent()) {
            // no client exists with that ID - send a 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Client client = optionalClient.get();

        //Check ETag
        checkETag(client, eTag);

        // Validate the transplant request
        try {
            transplantRequest.setClient(client); //required for validation
            TransplantRequestValidator.validateTransplantRequest(transplantRequest);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Add transplant request to client
        Action action = new AddTransplantRequestAction(client, transplantRequest, State.getClientManager());
        State.getActionInvoker(authToken).execute(action);
        Collection<TransplantRequest> transplantRequests = client.getTransplantRequests();

        //Add the new ETag to the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.getETag());

        return new ResponseEntity<>(transplantRequests, headers, HttpStatus.CREATED);
    }

    /**
     * Modifies a transplant request. Currently only allows resolution of requests.
     *
     * @param resolveRequestObject the resolve request object
     * @param uid the client's ID
     * @param id the transplant request's ID
     * @param eTag A hashed value of the object used for optimistic concurrency control
     * @return list of all transplant requests for that client
     */
    @PatchMapping("/clients/{uid}/transplantRequests/{id}")
    @JsonView(Views.Overview.class)
    public ResponseEntity<TransplantRequest> patchTransplantRequest(
            @RequestBody ResolveTransplantRequestObject resolveRequestObject,
            @PathVariable int uid,
            @PathVariable int id,
            @RequestHeader(value = "If-Match", required = false) String eTag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        // Check authentication
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        // Get the client given by the ID
        Optional<Client> optionalClient = State.getClientManager().getClientByID(uid);
        if (!optionalClient.isPresent()) {
            // no client exists with that ID - send a 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Client client = optionalClient.get();

        //Check ETag
        checkETag(client, eTag);

        // Get the original transplant request given by the ID
        Optional<TransplantRequest> optionalRequest = client.getTransplantRequestById(id);
        if (!optionalRequest.isPresent()) {
            // No transplant exists with that ID
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        TransplantRequest originalTransplantRequest = optionalRequest.get();

        // Patch currently only allows resolution of requests, so throw an error if other things have changed.
        // Only the status, resolved reason, and resolved date (and time) are allowed to change.
        // The client, organ, and request date (and time) must stay the same.
        // If anything has illegally changed, it will return a 400.
        if (resolveRequestObject.getResolvedDateTime() == null ||
                NotEmptyStringValidator.isInvalidString(resolveRequestObject.getResolvedReason()) ||
                resolveRequestObject.getResolvedDateTime().isBefore(originalTransplantRequest.getRequestDateTime())) {
            // illegal changes
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Resolve transplant request
        Action action = new ResolveTransplantRequestAction(originalTransplantRequest,
                resolveRequestObject.getStatus(),
                resolveRequestObject.getResolvedReason(),
                resolveRequestObject.getResolvedDateTime(),
                State.getClientManager());
        State.getActionInvoker(authToken).execute(action);

        //Add the new ETag to the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.getETag());

        return new ResponseEntity<>(originalTransplantRequest, headers, HttpStatus.CREATED);

    }
}