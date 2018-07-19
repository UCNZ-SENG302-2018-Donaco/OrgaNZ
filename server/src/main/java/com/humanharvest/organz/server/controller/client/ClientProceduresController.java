package com.humanharvest.organz.server.controller.client;

import java.util.Collection;
import java.util.Optional;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.state.State;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Provides handlers for requests to these endpoints:
 * - GET /clients/{uid}/procedures
 * - POST /clients/{uid}/procedures
 * - PATCH /clients/{uid}/procedures/{id}
 * - DELETE /clients/{uid}/procedures/{id}
 */
@RestController
public class ClientProceduresController {

    @GetMapping("/clients/{uid}/procedures")
    public ResponseEntity<Collection<ProcedureRecord>> getProceduresForClient(
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken,
            @PathVariable Integer uid) {
        //Retrieves the authentication token
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);
        //Retrieves the client by uid
        Optional<Client> client = State.getClientManager().getClientByID(uid);
        //returns the pending procedures for the client
        return client.map(client1 -> new ResponseEntity<Collection<ProcedureRecord>>
                (client1.getPendingProcedures(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>
                        (HttpStatus.NOT_FOUND));
    }

    @PostMapping("/clients/{uid}/procedures")
    public ResponseEntity<Collection<ProcedureRecord>> createProcedureRecord() {
        throw new UnsupportedOperationException();
    }

    @PatchMapping("/clients/{uid}/procedures/{id)")
    public ResponseEntity<ProcedureRecord> modifyProcedureRecord() {
        throw new UnsupportedOperationException();
    }

    @DeleteMapping("/clients/{uid}/procedures/{id}")
    public ResponseEntity deleteProcedureRecord() {
        throw new UnsupportedOperationException();
    }
}
