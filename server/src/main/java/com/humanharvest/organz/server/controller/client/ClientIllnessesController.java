package com.humanharvest.organz.server.controller.client;

import java.util.List;
import java.util.Optional;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.client.illness.AddIllnessRecordAction;
import com.humanharvest.organz.actions.client.illness.DeleteIllnessRecordAction;
import com.humanharvest.organz.actions.client.illness.ModifyIllnessRecordByObjectAction;
import com.humanharvest.organz.server.exceptions.GlobalControllerExceptionHandler.InvalidRequestException;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.validators.client.ModifyIllnessValidator;
import com.humanharvest.organz.views.client.CreateIllnessView;
import com.humanharvest.organz.views.client.ModifyIllnessObject;

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

@RestController
public class ClientIllnessesController {

    /**
     * Gets Clients Illnesses
     *
     * @param id Id Of Client
     * @return Returns list of Illnesses
     */
    @GetMapping("/clients/{id}/illnesses")
    public ResponseEntity<List<IllnessRecord>> getClientCurrentIllnesses(
            @PathVariable int id,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        Optional<Client> optionalClient = State.getClientManager().getClientByID(id);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            //State.getAuthenticationManager().verifyClientAccess(authToken, client);
            HttpHeaders headers = new HttpHeaders();

            return new ResponseEntity<>(client.getIllnesses(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/clients/{uid}/illnesses/{id}")
    public ResponseEntity<IllnessRecord> patchIllness(
            @PathVariable int uid,
            @PathVariable int id,
            @RequestBody ModifyIllnessObject newIllnessDetails,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws InvalidRequestException {

        //Fetch the client given by ID
        Optional<Client> optionalClient = State.getClientManager().getClientByID(uid);
        if (!optionalClient.isPresent()) {
            //Return 404 if that client does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Client client = optionalClient.get();

        //Auth check
        State.getAuthenticationManager().verifyClientAccess(authToken, client);

        IllnessRecord record = client.getIllnessById(id);

        if (record == null) {
            //Return 404 if that illness does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!ModifyIllnessValidator.isValid(newIllnessDetails)) {
            throw new InvalidRequestException();
        }

        if (record.getIsChronic() && newIllnessDetails.getCuredDate() != null) {
            //Cured date is trying to be set while disease is chronic.
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        //Create the old details to allow undoable action
        ModifyIllnessObject oldIllnessDetails = new ModifyIllnessObject();
        //Copy the values from the current record to our old record
        BeanUtils.copyProperties(record, oldIllnessDetails, newIllnessDetails.getUnmodifiedFields());

        //Make the action (this is a new action)
        Action action = new ModifyIllnessRecordByObjectAction(
                record, State.getClientManager(), oldIllnessDetails, newIllnessDetails);

        State.getActionInvoker(authToken).execute(action);

        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(record, headers, HttpStatus.OK);

    }

    @PostMapping("/clients/{uid}/illnesses")
    public ResponseEntity<List<IllnessRecord>> postIllness(
            @RequestBody CreateIllnessView illnessView,
            @PathVariable int uid,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws InvalidRequestException {

        Optional<Client> optionalClient = State.getClientManager().getClientByID(uid);
        if (!optionalClient.isPresent()) {
            //Return 404 if that client does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Client client = optionalClient.get();

        State.getAuthenticationManager().verifyClientAccess(authToken, client);

        IllnessRecord record = new IllnessRecord(illnessView.getIllnessName(),
                illnessView.getDiagnosisDate(), illnessView.getIsChronic());

        AddIllnessRecordAction addIllnessRecordAction = new AddIllnessRecordAction(client, record,
                State.getClientManager());

        State.getActionInvoker(authToken).execute(addIllnessRecordAction);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(client.getIllnesses(), headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/clients/{uid}/illnesses/{id}")
    public ResponseEntity<IllnessRecord> deleteIllness(
            @PathVariable int uid,
            @PathVariable int id,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) throws InvalidRequestException {
        Optional<Client> optionalClient = State.getClientManager().getClientByID(uid);
        if (!optionalClient.isPresent()) {
            //Return 404 if that client does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Client client = optionalClient.get();

        IllnessRecord removeRecord = client.getIllnessById(id);
        if (removeRecord == null) {
            //Return 404 if that illness does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        State.getAuthenticationManager().verifyClientAccess(authToken, client);
        DeleteIllnessRecordAction action = new DeleteIllnessRecordAction(client, removeRecord,
                State.getClientManager());
        State.getActionInvoker(authToken).execute(action);

        Client client1 = State.getClientManager()
                .getClientByID(client.getUid())
                .orElseThrow(IllegalStateException::new);

        HttpHeaders headers = new HttpHeaders();

        return new ResponseEntity<>(removeRecord, headers, HttpStatus.OK);

    }
}
