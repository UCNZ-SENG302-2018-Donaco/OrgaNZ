package com.humanharvest.organz.server.controller.client;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.validators.client.TransplantRequestValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientTransplantRequestsController {

    @GetMapping("/clients/transplantRequests")
    public ResponseEntity<Collection<TransplantRequest>> getAllTransplantRequests(
            @RequestParam(value="offset", required = false) Integer offset,
            @RequestParam(value="count", required = false) Integer count,
            @RequestParam(value="region", required = false) List<Region> regions,
            @RequestParam(value="organs", required = false) List<Organ> organs) {

        List<TransplantRequest> matchingRequests = State.getClientManager().getAllTransplantRequests().stream()
                .filter(transplantRequest ->
                        regions == null || regions.contains(transplantRequest.getClient().getRegion()))
                .filter(transplantRequest ->
                        organs == null || organs.contains(transplantRequest.getRequestedOrgan()))
                .collect(Collectors.toList());

        System.out.println(matchingRequests.toString());

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

    @GetMapping("/clients/{id}/transplantRequests")
    public ResponseEntity<Collection<TransplantRequest>> getClientTransplantRequests(@PathVariable int id) {
        Optional<Client> client = State.getClientManager().getClientByID(id);
        if (client.isPresent()) {
            return new ResponseEntity<>(client.get().getTransplantRequests(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Creates a transplant request.
     * @param transplantRequest the transplant request to add
     * @param id the client's ID
     * @param ETag A hashed value of the object used for optimistic concurrency control
     * @return list of all transplant requests for that client
     */
    @PostMapping("/clients/{id}/transplantRequests")
    public ResponseEntity<Collection<TransplantRequest>> postTransplantRequest(
            @RequestBody TransplantRequest transplantRequest,
            @PathVariable int id,
            @RequestHeader(value = "If-Match",required = false) String ETag) {

        // Get the client given by the ID
        Optional<Client> client = State.getClientManager().getClientByID(id);
        if (client.isPresent()) {

            // Check etag
            if (ETag == null || !client.get().getEtag().equals(ETag)) {
                throw new IfMatchFailedException();
            }

            // Validate the transplant request
            try {
                transplantRequest.setClient(client.get());
                TransplantRequestValidator.validateTransplantRequest(transplantRequest);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Add transplant request to client and send 201
            client.get().addTransplantRequest(transplantRequest);
            Collection<TransplantRequest> transplantRequests = client.get().getTransplantRequests();
            return new ResponseEntity<>(transplantRequests, HttpStatus.CREATED);

        } else {
            // no client exists with that ID - send a 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
