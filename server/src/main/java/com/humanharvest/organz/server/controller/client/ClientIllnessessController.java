package com.humanharvest.organz.server.controller.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
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
import org.springframework.web.bind.annotation.*;

@RestController
public class ClientIllnessessController {

    /**
     * Gets Clients Illnesses
     * @param id Id Of Client
     * @return Returns list of Illnesses
     */
    @GetMapping("/clients/{id}/illnesses")
    public ResponseEntity<List<IllnessRecord>> getClientCurrentIllnesses(@PathVariable int id,
        @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {


        Optional<Client> Optionalclient = State.getClientManager().getClientByID(id);
        // Client does not exist
        Client client = Optionalclient.get();
        State.getAuthenticationManager().verifyClientAccess(authToken, client);
        if (Optionalclient.isPresent()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("ETag",Optionalclient.get().getETag());
            List<IllnessRecord> illnesses = new ArrayList<>(Optionalclient.get().getCurrentIllnesses());
            illnesses.addAll(Optionalclient.get().getPastIllnesses());

            return new ResponseEntity<>(illnesses, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/clients/{uid}/illnesses/{id}")
    @JsonView(Views.Overview.class)
    public ResponseEntity<IllnessRecord> patchIllness(@PathVariable int uid,
            @PathVariable int id,
            @RequestBody ModifyIllnessObject modifyIllnessObject,
            @RequestHeader(value = "If-Match",required = false)String ETag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws IfMatchRequiredException, IfMatchFailedException, InvalidRequestException {
        if (!ModifyIllnessValidator.isValid(modifyIllnessObject)) {
            throw new InvalidRequestException();
        }

        //Fetch the client given by ID
        Optional<Client> client = State.getClientManager().getClientByID(uid);
        if (!client.isPresent()) {
            //Return 404 if that client does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        IllnessRecord record;
        try {
            record = client.get().getAllIllnessHistory().get(id - 1); // starting index 1.
        } catch (IndexOutOfBoundsException e) {
            //Record does not exist
            System.out.println("Record does not exist");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (ETag == null) {
            throw new IfMatchRequiredException();
        }
        System.out.println(client.get().getETag());
        if (!client.get().getETag().equals(ETag)) {
            throw new IfMatchFailedException();
        }



        //Create the old details to allow undoable action
        ModifyIllnessObject oldIllnessRecord = new ModifyIllnessObject();
        //Copy the values from the current record to our oldrecord
        BeanUtils.copyProperties(record, oldIllnessRecord, modifyIllnessObject.getUnmodifiedFields());
        //Make the action (this is a new action)
        ModifyIllnessRecordByObjectAction action = new ModifyIllnessRecordByObjectAction(record,
                State.getClientManager(),oldIllnessRecord,modifyIllnessObject);
        //Execute action, this would correspond to a specific users invoker in full version
        State.getActionInvoker(authToken).execute(action);


        //Add the new ETag to the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.get().getETag());
        return new ResponseEntity<>(record, headers, HttpStatus.OK);

    }

    @PostMapping("/clients/{uid}/illnesses")
    @JsonView(Views.Overview.class)
    public ResponseEntity <IllnessRecord> postIllness(@RequestBody CreateIllnessView illnessView,
            @PathVariable int uid)
            throws InvalidRequestException {

        Optional<Client> client = State.getClientManager().getClientByID(uid);
        if (!client.isPresent()) {
            //Return 404 if that client does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        IllnessRecord record = new IllnessRecord(illnessView.getIllnessName(),
                illnessView.getDiagnosisDate(),illnessView.getCuredDate(),illnessView.isChronic());

        client.get().addIllnessRecord(record);
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.get().getETag());

        return new ResponseEntity<>(record,headers,HttpStatus.CREATED);
    }

    @DeleteMapping("/clients/{uid}/illnesses/{id}")
    @JsonView(Views.Overview.class)
    public ResponseEntity<IllnessRecord> deleteIllness(@PathVariable int uid, @PathVariable int id) throws InvalidRequestException {
        Optional<Client> client = State.getClientManager().getClientByID(uid);
        if (!client.isPresent()) {
            //Return 404 if that client does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        IllnessRecord removeRecord = client.get().getAllIllnessHistory().get(id-1);
        client.get().deleteIllnessRecord(removeRecord);
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.get().getETag());

        return new ResponseEntity<>(removeRecord,headers,HttpStatus.OK);


    }
}
