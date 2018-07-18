package com.humanharvest.organz.server.controller.client;

import java.util.Collection;
import java.util.Optional;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.State;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
