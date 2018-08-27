package com.humanharvest.organz.server.controller.client;

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
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class ClientIllnessesController {

    private static final Logger LOGGER = Logger.getLogger(ClientController.class.getName());

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
            headers.setETag(client.getETag());

            return new ResponseEntity<>(optionalClient.get().getIllnesses(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/clients/{uid}/illnesses/{id}")
    public ResponseEntity<IllnessRecord> patchIllness(
            @PathVariable int uid,
            @PathVariable int id,
            @RequestBody ModifyIllnessObject modifyIllnessObject,
            @RequestHeader(value = "If-Match", required = false) String ETag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws IfMatchRequiredException, IfMatchFailedException, InvalidRequestException {

        //Fetch the client given by ID
        Optional<Client> optionalClient = State.getClientManager().getClientByID(uid);
        if (!optionalClient.isPresent()) {
            //Return 404 if that client does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (ETag == null) {
            throw new IfMatchRequiredException();
        }

        if (!optionalClient.get().getETag().equals(ETag)) {
            throw new IfMatchFailedException();
        }

        IllnessRecord record;
        try {
            Client client = optionalClient.get();
            record = client.getIllnessById(id);
            if (record == null) {
                //Return 404 if that illness does not exist
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            State.getAuthenticationManager().verifyClientAccess(authToken, client);

            if (!ModifyIllnessValidator.isValid(modifyIllnessObject)) {
                throw new InvalidRequestException();
            }

            if (record.isChronic() && modifyIllnessObject.getCuredDate() != null) {
                //Cured date is trying to be set while disease is chronic.
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (modifyIllnessObject.getIllnessName() == null) {
                modifyIllnessObject.setIllnessName(record.getIllnessName());
            }
            if (modifyIllnessObject.getDiagnosisDate() == null) {
                modifyIllnessObject.setDiagnosisDate(record.getDiagnosisDate());
            }

        } catch (NullPointerException e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            //Record does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
    public ResponseEntity<List<IllnessRecord>> postIllness(
            @RequestBody CreateIllnessView illnessView,
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

        AddIllnessRecordAction addIllnessRecordAction = new AddIllnessRecordAction(client.get(), record,
                State.getClientManager());

        State.getActionInvoker(authToken).execute(addIllnessRecordAction);
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.get().getETag());
        return new ResponseEntity<>(client.get().getIllnesses(), headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/clients/{uid}/illnesses/{id}")
    public ResponseEntity<IllnessRecord> deleteIllness(
            @PathVariable int uid,
            @PathVariable int id,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) throws InvalidRequestException {
        Optional<Client> client = State.getClientManager().getClientByID(uid);
        if (!client.isPresent()) {
            //Return 404 if that client does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        IllnessRecord removeRecord = client.get().getIllnessById(id);
        if (removeRecord == null) {
            //Return 404 if that illness does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        State.getAuthenticationManager().verifyClientAccess(authToken, client.get());
        DeleteIllnessRecordAction action = new DeleteIllnessRecordAction(client.get(), removeRecord,
                State.getClientManager());
        State.getActionInvoker(authToken).execute(action);

        Client client1 = State.getClientManager()
                .getClientByID(client.get().getUid())
                .orElseThrow(IllegalStateException::new);

        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client1.getETag());

        return new ResponseEntity<>(removeRecord, headers, HttpStatus.OK);

    }
}
