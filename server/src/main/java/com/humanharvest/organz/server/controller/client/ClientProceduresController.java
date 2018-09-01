package com.humanharvest.organz.server.controller.client;

import static com.humanharvest.organz.utilities.validators.ClientValidator.checkClientETag;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.client.procedure.AddProcedureRecordAction;
import com.humanharvest.organz.actions.client.procedure.DeleteProcedureRecordAction;
import com.humanharvest.organz.actions.client.procedure.ModifyProcedureRecordAction;
import com.humanharvest.organz.actions.client.procedure.ScheduleTransplantAction;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.exceptions.DateOutOfBoundsException;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.IfMatchRequiredException;
import com.humanharvest.organz.views.client.ModifyProcedureObject;
import com.humanharvest.organz.views.client.Views;

import com.fasterxml.jackson.annotation.JsonView;
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

/**
 * Provides handlers for requests to these endpoints:
 * - GET /clients/{uid}/procedures
 * - POST /clients/{uid}/procedures
 * - PATCH /clients/{uid}/procedures/{id}
 * - DELETE /clients/{uid}/procedures/{id}
 */
@RestController
public class ClientProceduresController {

    private static final Logger LOGGER = Logger.getLogger(ClientProceduresController.class.getName());

    @GetMapping("/clients/{uid}/procedures")
    public ResponseEntity<Collection<ProcedureRecord>> getProceduresForClient(
            @PathVariable int uid,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException, IfMatchFailedException, IfMatchRequiredException {

        Optional<Client> client = State.getClientManager().getClientByID(uid);
        if (client.isPresent()) {
            // Check request has authorization to view client's procedures
            State.getAuthenticationManager().verifyClientAccess(authToken, client.get());

            // Add the ETag to the headers
            HttpHeaders headers = new HttpHeaders();
            headers.setETag(client.get().getETag());

            // Returns the pending procedures for the client
            return new ResponseEntity<>(client.get().getProcedures(), headers, HttpStatus.OK);
        } else {
            // Client does not exist, return 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/clients/{uid}/procedures")
    public ResponseEntity<Collection<ProcedureRecord>> createProcedureRecord(
            @RequestBody ProcedureRecord procedureRecord,
            @PathVariable int uid,
            @RequestHeader(value = "If-Match", required = false) String ETag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException, IfMatchFailedException, IfMatchRequiredException {

        // Check request has authorization to create a procedure
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        Optional<Client> optionalClient = State.getClientManager().getClientByID(uid);
        if (!optionalClient.isPresent()) {
            // No client exists with that id, return 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Client client = optionalClient.get();

        //Check ETag
        checkClientETag(client, ETag);

        // Execute add procedure action
        Action action = new AddProcedureRecordAction(client, procedureRecord, State.getClientManager());
        State.getActionInvoker(authToken).execute(action);

        // Add the new ETag to the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.getETag());

        // Return response containing list of client's procedures
        return new ResponseEntity<>(client.getProcedures(), headers, HttpStatus.CREATED);
    }

    @PatchMapping("/clients/{uid}/procedures/{id}")
    @JsonView(Views.Overview.class)
    public ResponseEntity<ProcedureRecord> modifyProcedureRecord(
            @RequestBody ModifyProcedureObject modifyProcedureObject,
            @PathVariable int uid,
            @PathVariable int id,
            @RequestHeader(value = "If-Match", required = false) String ETag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) throws AuthenticationException {

        // Check request has authorization to patch a procedure
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        // Try to find a client with matching uid
        Optional<Client> optionalClient = State.getClientManager().getClientByID(uid);
        if (!optionalClient.isPresent()) {
            // No client exists with those ids, return 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Client client = optionalClient.get();

        //Check ETag
        checkClientETag(client, ETag);

        // Try to find a procedure record with matching id
        Optional<ProcedureRecord> optionalRecord = client.getProcedures().stream()
                .filter(procedure -> procedure.getId() != null && procedure.getId() == id)
                .findFirst();

        if (!optionalRecord.isPresent()) {
            // No client exists with those ids, return 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ProcedureRecord toModify = optionalRecord.get();

        // Create action
        ModifyProcedureRecordAction action = new ModifyProcedureRecordAction(
                toModify, State.getClientManager());

        // Register all changes
        if (modifyProcedureObject.getSummary() != null) {
            action.changeSummary(modifyProcedureObject.getSummary());
        }
        if (modifyProcedureObject.getDescription() != null) {
            action.changeDescription(modifyProcedureObject.getDescription());
        }
        if (modifyProcedureObject.getDate() != null) {
            action.changeDate(modifyProcedureObject.getDate());
        }
        if (modifyProcedureObject.getAffectedOrgans() != null) {
            action.changeAffectedOrgans(modifyProcedureObject.getAffectedOrgans());
        }

        // Execute the action
        try {
            State.getActionInvoker(authToken).execute(action);
        } catch (IllegalStateException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Add the new ETag to the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.getETag());

        // Return OK response
        return new ResponseEntity<>(toModify, headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/clients/{uid}/procedures/{id}")
    public ResponseEntity deleteProcedureRecord(
            @PathVariable int uid,
            @PathVariable int id,
            @RequestHeader(value = "If-Match", required = false) String ETag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException, IfMatchFailedException, IfMatchRequiredException {

        // Check request has authorization to delete a procedure
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        // Try to find a client with matching uid
        Optional<Client> optionalClient = State.getClientManager().getClientByID(uid);
        if (!optionalClient.isPresent()) {
            // No client exists with that id, return 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Client client = optionalClient.get();

        //Check ETag
        checkClientETag(client, ETag);

        // Try to find a procedure record with matching id
        Optional<ProcedureRecord> optionalRecord = client.getProcedures().stream()
                .filter(procedure -> procedure.getId() != null && procedure.getId() == id)
                .findFirst();
        if (!optionalRecord.isPresent()) {
            // No procedure exists with that id, return 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ProcedureRecord toDelete = optionalRecord.get();

        // Execute delete procedure action
        Action action = new DeleteProcedureRecordAction(client, toDelete, State.getClientManager());
        State.getActionInvoker(authToken).execute(action);

        // Add the new ETag to the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.getETag());

        // Return OK response
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @PostMapping("/clients/{uid}/transplants")
    public ResponseEntity<Collection<ProcedureRecord>> scheduleTransplantProcedure(
            @PathVariable int uid,
            @RequestParam long organId,
            @RequestParam long requestId,
            @RequestParam LocalDate date,
            @RequestHeader(value = "If-Match", required = false) String ETag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException, IfMatchFailedException, IfMatchRequiredException {

        // Check request has authorization to create a procedure
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        Optional<Client> optionalClient = State.getClientManager().getClientByID(uid);
        if (!optionalClient.isPresent()) {
            // No client exists with that id, return 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Client client = optionalClient.get();

        // Check ETag
        checkClientETag(client, ETag);

        // Find the organ to be transplanted
        DonatedOrgan organ = client.getDonatedOrganById(organId);
        if (organ == null) {
            // No organ exists with that id, return 400
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Find the recipient's request for this organ
        Optional<TransplantRequest> optionalRequest = client.getTransplantRequestById(requestId);
        if (!optionalRequest.isPresent()) {
            // No request exists with that id, return 400
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        TransplantRequest request = optionalRequest.get();

        // Check that the organ hasn't been donated yet, and the request is still waiting
        if (!(organ.getReceiver() == null && request.getStatus() == TransplantRequestStatus.WAITING)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Execute add procedure action
        try {
            Action action = new ScheduleTransplantAction(organ, request, date, State.getClientManager());
            State.getActionInvoker(authToken).execute(action);
        } catch (DateOutOfBoundsException exc) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Add the new ETag to the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.getETag());

        // Return response containing list of client's procedures
        return new ResponseEntity<>(client.getProcedures(), headers, HttpStatus.CREATED);
    }
}
