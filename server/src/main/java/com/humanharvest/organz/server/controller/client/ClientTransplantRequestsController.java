package com.humanharvest.organz.server.controller.client;

import java.util.Collection;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.client.Views;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientTransplantRequestsController {

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
     * @param id the client's ID
     * @return list of all transplant requests for that client
     */
    @PostMapping("/clients/{id}/transplantRequests")
    @JsonView(Views.Overview.class)
    public ResponseEntity<Collection<TransplantRequest>> postTransplantRequest(
            @RequestBody TransplantRequest transplantRequest,
            @PathVariable int id) {
        Optional<Client> client = State.getClientManager().getClientByID(id);

        if (client.isPresent()) {
            client.get().addTransplantRequest(transplantRequest);

            Collection<TransplantRequest> transplantRequests = client.get().getTransplantRequests();
            return new ResponseEntity<>(transplantRequests, HttpStatus.OK);

        } else {
            // no client exists with that ID
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


    }
}
