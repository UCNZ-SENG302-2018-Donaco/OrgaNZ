package com.humanharvest.organz.server.controller.client;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.actions.client.AddIllnessRecordAction;
import com.humanharvest.organz.actions.client.DeleteIllnessRecordAction;
import com.humanharvest.organz.actions.client.ModifyIllnessRecordByObjectAction;
import com.humanharvest.organz.server.exceptions.GlobalControllerExceptionHandler.InvalidRequestException;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.IfMatchRequiredException;
import com.humanharvest.organz.utilities.validators.client.ModifyIllnessValidator;
import com.humanharvest.organz.views.client.CreateIllnessView;
import com.humanharvest.organz.views.client.ModifyIllnessObject;
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

@RestController
public class ClientIllnessesController {

    /**
     * Gets Clients Illnesses
     * @param id Id Of Client
     * @return Returns list of Illnesses
     */
    @GetMapping("/clients/{id}/illnesses")
    public ResponseEntity<List<IllnessRecord>> getClientCurrentIllnesses(@PathVariable int id,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        Optional<Client> Optionalclient = State.getClientManager().getClientByID(id);
        if (Optionalclient.isPresent()) {
            Client client = Optionalclient.get();
            //State.getAuthenticationManager().verifyClientAccess(authToken, client);
            HttpHeaders headers = new HttpHeaders();
            headers.setETag(client.getETag());

            return new ResponseEntity<>(Optionalclient.get().getIllnesses(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/clients/{uid}/illnesses/{id}")
    @JsonView(Views.Overview.class)
    public ResponseEntity<IllnessRecord> patchIllness(@PathVariable int uid,
            @PathVariable int id,
            @RequestBody ModifyIllnessObject modifyIllnessObject,
            @RequestHeader(value = "If-Match", required = false) String ETag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws IfMatchRequiredException, IfMatchFailedException, InvalidRequestException {
        if (!ModifyIllnessValidator.isValid(modifyIllnessObject)) {
            throw new InvalidRequestException();
        }

        //Fetch the client given by ID
        Optional<Client> optionalClient = State.getClientManager().getClientByID(uid);
        if (!optionalClient.isPresent()) {
            //Return 404 if that client does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        IllnessRecord record;
        try {
            Client client = optionalClient.get();
            record = client.getIllnesses().get(id - 1); // starting index 1.
            //State.getAuthenticationManager().verifyClientAccess(authToken, client);
            if(record.isChronic() && modifyIllnessObject.getCuredDate() != null){
                //Cured date is trying to be set while disease is chronic.
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (IndexOutOfBoundsException e) {
            //Record does not exist
            System.out.println("Record does not exist");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (ETag == null) {
            throw new IfMatchRequiredException();
        }
        System.out.println(optionalClient.get().getETag());
        if (!optionalClient.get().getETag().equals(ETag)) {
            throw new IfMatchFailedException();
        }

        //Create the old details to allow undoable action
        ModifyIllnessObject oldIllnessRecord = new ModifyIllnessObject();
        //Copy the values from the current record to our oldrecord
        BeanUtils.copyProperties(record, oldIllnessRecord, modifyIllnessObject.getUnmodifiedFields());
        //Make the action (this is a new action)
        ModifyIllnessRecordByObjectAction action = new ModifyIllnessRecordByObjectAction(record,
                State.getClientManager(), oldIllnessRecord, modifyIllnessObject);
        //Execute action, this would correspond to a specific users invoker in full version
        State.getActionInvoker(authToken).execute(action);

        //Add the new ETag to the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(optionalClient.get().getETag());
        return new ResponseEntity<>(record, headers, HttpStatus.OK);

    }

    @PostMapping("/clients/{uid}/illnesses")
    public ResponseEntity <List<IllnessRecord>> postIllness(@RequestBody CreateIllnessView illnessView,
            @PathVariable int uid,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws InvalidRequestException {

        Optional<Client> client = State.getClientManager().getClientByID(uid);
        if (client.isPresent()) {
            State.getAuthenticationManager().verifyClientAccess(authToken, client.get());
        } else {
            //Return 404 if that client does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        IllnessRecord record = new IllnessRecord(illnessView.getIllnessName(),
                illnessView.getDiagnosisDate(), illnessView.isChronic());

        AddIllnessRecordAction addIllnessRecordAction = new AddIllnessRecordAction(client.get(),record,
            State.getClientManager());

        State.getActionInvoker(authToken).execute(addIllnessRecordAction);
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.get().getETag());
        return new ResponseEntity<>(client.get().getIllnesses(), headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/clients/{uid}/illnesses/{id}")
    public ResponseEntity<IllnessRecord> deleteIllness(@PathVariable int uid,
            @PathVariable int id,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) throws InvalidRequestException {
        Optional<Client> client = State.getClientManager().getClientByID(uid);
        if (!client.isPresent()) {
            //Return 404 if that client does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        IllnessRecord removeRecord = client.get().getIllnesses().get(id);
        State.getAuthenticationManager().verifyClientAccess(authToken, client.get());
        DeleteIllnessRecordAction action = new DeleteIllnessRecordAction(client.get(),removeRecord,
            State.getClientManager());
        State.getActionInvoker(authToken).execute(action);
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.get().getETag());

        return new ResponseEntity<>(removeRecord, headers, HttpStatus.OK);

    }
}
