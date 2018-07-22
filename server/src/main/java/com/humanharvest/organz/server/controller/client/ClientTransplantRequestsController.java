package com.humanharvest.organz.server.controller.client;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.client.AddTransplantRequestAction;
import com.humanharvest.organz.actions.client.ResolveTransplantRequestAction;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.IfMatchRequiredException;
import com.humanharvest.organz.utilities.validators.client.TransplantRequestValidator;
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

    /**
     * Retrieves all transplant requests stored in the system matching the given criteria.
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
    public ResponseEntity<Collection<TransplantRequest>> getAllTransplantRequests(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "count", required = false) Integer count,
            @RequestParam(value = "region", required = false) List<Region> regions,
            @RequestParam(value = "organs", required = false) List<Organ> organs,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException {

        // Verify that request has clinician/admin authorization
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        // Get all requests that match region/organ filters
        List<TransplantRequest> matchingRequests = State.getClientManager().getAllTransplantRequests().stream()
                .filter(transplantRequest ->
                        regions == null || regions.contains(transplantRequest.getClient().getRegion()))
                .filter(transplantRequest ->
                        organs == null || organs.contains(transplantRequest.getRequestedOrgan()))
                .collect(Collectors.toList());

        // Return subset for given offset/count parameters (used for pagination)
        if (offset == null) {
            offset = 0;
        }
        if (count == null) {
            return new ResponseEntity<>(
                    matchingRequests.subList(
                            Math.min(offset, matchingRequests.size()),
                            matchingRequests.size()),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    matchingRequests.subList(
                            Math.min(offset, matchingRequests.size()),
                            Math.min(offset + count, matchingRequests.size())),
                    HttpStatus.OK);
        }
    }

    /**
     * Retrieves all transplant requests belonging to a client with a given id.
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
     * @param transplantRequest the transplant request to add
     * @param id the client's ID
     * @param etag A hashed value of the object used for optimistic concurrency control
     * @return list of all transplant requests for that client
     */
    @PostMapping("/clients/{id}/transplantRequests")
    public ResponseEntity<Collection<TransplantRequest>> postTransplantRequest(
            @RequestBody TransplantRequest transplantRequest,
            @PathVariable int id,
            @RequestHeader(value = "If-Match", required = false) String etag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        // Get the client given by the ID
        Optional<Client> optionalClient = State.getClientManager().getClientByID(id);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();

            // Check authentication
            State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

            // Check etag
            if (etag == null) {
                throw new IfMatchRequiredException();
            }
            if (!client.getETag().equals(etag)) {
                throw new IfMatchFailedException();
            }

            // Validate the transplant request
            try {
                transplantRequest.setClient(client); //required for validation
                TransplantRequestValidator.validateTransplantRequest(transplantRequest);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Add transplant request to client and send 201
            Action action = new AddTransplantRequestAction(client, transplantRequest, State.getClientManager());
            State.getActionInvoker(authToken).execute(action);
            Collection<TransplantRequest> transplantRequests = client.getTransplantRequests();
            return new ResponseEntity<>(transplantRequests, HttpStatus.CREATED);

        } else {
            // no client exists with that ID - send a 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Modifies a transplant request. Currently only allows resolution of requests.
     * @param transplantRequest the transplant request to add
     * @param uid the client's ID
     * @param id the transplant request's ID
     * @param etag A hashed value of the object used for optimistic concurrency control
     * @return list of all transplant requests for that client
     */
    @PatchMapping("/clients/{uid}/transplantRequests/{id}")
    public ResponseEntity<TransplantRequest> postTransplantRequest(
            @RequestBody TransplantRequest transplantRequest,
            @PathVariable int uid,
            @PathVariable int id,
            @RequestHeader(value = "If-Match", required = false) String etag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        // Get the client given by the ID
        Optional<Client> optionalClient = State.getClientManager().getClientByID(uid);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            TransplantRequest originalTransplantRequest;

            // Get the original transplant request given by the ID
            try {
                originalTransplantRequest = client.getTransplantRequests().get(id);
            } catch (IndexOutOfBoundsException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Check authentication
            State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

            // Check etag
            if (etag == null) {
                throw new IfMatchRequiredException();
            }
            if (!client.getETag().equals(etag)) {
                throw new IfMatchFailedException();
            }

            // Validate the transplant request
            try {
                TransplantRequestValidator.validateTransplantRequest(originalTransplantRequest);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Patch currently only allows resolution of requests, so throw an error if other things have changed.
            // Only the status, resolved reason, and resolved date (and time) are allowed to change.
            // The client, organ, and request date (and time) must stay the same.
            // If anything has illegally changed, it will return a 400.
            if (originalTransplantRequest.getClient().getUid().equals(client.getUid())
                    && originalTransplantRequest.getRequestedOrgan() == transplantRequest.getRequestedOrgan()
                    && originalTransplantRequest.getRequestDate().equals(transplantRequest.getRequestDate())) {

                // Resolve transplant request and send 201
                Action action = new ResolveTransplantRequestAction(originalTransplantRequest,
                        transplantRequest.getStatus(),
                        transplantRequest.getResolvedReason(),
                        transplantRequest.getResolvedDate(),
                        State.getClientManager());
                State.getActionInvoker(authToken).execute(action);
                return new ResponseEntity<>(originalTransplantRequest, HttpStatus.CREATED);
            } else {
                // illegal changes
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            // no client exists with that ID - send a 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}