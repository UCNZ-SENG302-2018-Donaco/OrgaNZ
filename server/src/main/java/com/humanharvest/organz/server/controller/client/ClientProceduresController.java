package com.humanharvest.organz.server.controller.client;

import java.util.Collection;
import java.util.Optional;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.client.AddProcedureRecordAction;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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
            @PathVariable Integer uid,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException {

        Optional<Client> client = State.getClientManager().getClientByID(uid);
        if (client.isPresent()) {
            // Check request has authorization to view client's procedures
            State.getAuthenticationManager().verifyClientAccess(authToken, client.get());
            // Returns the pending procedures for the client
            return new ResponseEntity<>(client.get().getProcedures(), HttpStatus.OK);
        } else {
            // Client does not exist, return 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/clients/{uid}/procedures")
    public ResponseEntity<Collection<ProcedureRecord>> createProcedureRecord(
            @RequestBody ProcedureRecord procedureRecord,
            @PathVariable int uid,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException {

        // Check request has authorization to create a procedure
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        Optional<Client> client = State.getClientManager().getClientByID(uid);
        if (client.isPresent()) {
            // Execute add procedure action
            Action action = new AddProcedureRecordAction(client.get(), procedureRecord, State.getClientManager());
            State.getInvoker().execute(action);
            // Return response containing list of client's procedures
            return new ResponseEntity<>(client.get().getProcedures(), HttpStatus.OK);
        } else {
            // No client exists with that id, return 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
