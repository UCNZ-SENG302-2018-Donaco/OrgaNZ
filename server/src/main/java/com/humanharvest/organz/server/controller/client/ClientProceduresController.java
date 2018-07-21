package com.humanharvest.organz.server.controller.client;

import java.util.Collection;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.client.AddProcedureRecordAction;
import com.humanharvest.organz.actions.client.DeleteProcedureRecordAction;
import com.humanharvest.organz.actions.client.ModifyProcedureRecordAction;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.IfMatchRequiredException;
import com.humanharvest.organz.views.client.ModifyProceduresObject;
import com.humanharvest.organz.views.client.Views;
import org.springframework.beans.BeanUtils;
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
            @RequestHeader(value = "If-Match", required = false) String eTag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException {

        Optional<Client> client = State.getClientManager().getClientByID(uid);
        if (eTag == null) {
            throw new IfMatchRequiredException();
        }
        if (!client.get().getETag().equals(eTag)) {
            throw new IfMatchFailedException();
        }
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
            @RequestHeader(value = "If-Match", required = false) String eTag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException {

        // Check request has authorization to create a procedure
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        System.out.println(authToken);
        Optional<Client> client = State.getClientManager().getClientByID(uid);
        if (eTag == null) {
            throw new IfMatchRequiredException();
        }
        if (!client.get().getETag().equals(eTag)) {
            throw new IfMatchFailedException();
        }
        if (client.isPresent()) {
            // Execute add procedure action
            Action action = new AddProcedureRecordAction(client.get(), procedureRecord, State.getClientManager());
            State.getActionInvoker(authToken).execute(action);
            // Return response containing list of client's procedures
            return new ResponseEntity<>(client.get().getProcedures(), HttpStatus.OK);
        } else {
            // No client exists with that id, return 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/clients/{uid}/procedures/{id}")
    @JsonView(Views.Overview.class)
    public ResponseEntity<ProcedureRecord> modifyProcedureRecord(
            @RequestBody ModifyProceduresObject modifyProceduresObject,
            @RequestHeader(value = "If-Match", required = false) String eTag,
            @PathVariable int uid,
            @PathVariable int id,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) throws AuthenticationException {
        Optional<Client> client = State.getClientManager().getClientByID(uid);
        if (client.isPresent()) {
            // Try to find a procedure record with matching id
            Optional<ProcedureRecord> toDelete = client.get().getProcedures().stream()
                    .filter(procedure -> procedure.getId() != null && procedure.getId() == id)
                    .findFirst();
        }
        ProcedureRecord record;
        try {
            record = client.get().getProcedures().get(id - 1);
        } catch (IndexOutOfBoundsException e) {
            //procedure record does not exist
            System.out.println("Procedure record does not exist");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (eTag == null) {
            throw new IfMatchRequiredException();
        }
        if (!client.get().getETag().equals(eTag)) {
            throw new IfMatchFailedException();
        }

        ModifyProceduresObject oldModifyProceduresObject = new ModifyProceduresObject();
        BeanUtils.copyProperties(record, oldModifyProceduresObject, modifyProceduresObject.getUnmodifiedFields());
        ModifyProcedureRecordAction action = new ModifyProcedureRecordAction(record, State.getClientManager());

        State.getActionInvoker(authToken).execute(action);

        //Add the new ETag to the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.get().getETag());

        //Respond, apparently updates should be 200 not 201 unlike 365 and our spec
        return new ResponseEntity<>(record, headers, HttpStatus.OK);
    }

    @DeleteMapping("/clients/{uid}/procedures/{id}")
    public ResponseEntity deleteProcedureRecord(
            @PathVariable int uid,
            @PathVariable int id,
            @RequestHeader(value = "If-Match", required = false) String eTag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException {

        // Check request has authorization to delete a procedure
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        // Try to find a client with matching uid
        Optional<Client> client = State.getClientManager().getClientByID(uid);
        if (client.isPresent()) {
            // Try to find a procedure record with matching id
            Optional<ProcedureRecord> toDelete = client.get().getProcedures().stream()
                    .filter(procedure -> procedure.getId() != null && procedure.getId() == id)
                    .findFirst();
            if (toDelete.isPresent()) {
                // Execute delete procedure action
                Action action = new DeleteProcedureRecordAction(client.get(), toDelete.get(), State.getClientManager());
                State.getActionInvoker(authToken).execute(action);
                // Return OK response
                return new ResponseEntity<>(client.get().getProcedures(), HttpStatus.CREATED);
            }
        }
        if (eTag == null) {
            throw new IfMatchRequiredException();
        }
        if (!client.get().getETag().equals(eTag)) {
            throw new IfMatchFailedException();
        }

        // No client/procedure exists with those ids, return 404
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
