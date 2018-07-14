package com.humanharvest.organz.server.controller.client;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.Views.Client.CreateIllnessView;
import com.humanharvest.organz.Views.Client.ModifyClientObject;
import com.humanharvest.organz.Views.Client.ModifyIllnessObject;
import com.humanharvest.organz.Views.Client.Views;
import com.humanharvest.organz.Views.Client.Views.Overview;
import com.humanharvest.organz.actions.client.AddIllnessRecordAction;
import com.humanharvest.organz.actions.client.ModifyClientByObjectAction;
import com.humanharvest.organz.actions.client.ModifyIllnessRecordAction;
import com.humanharvest.organz.actions.client.ModifyIllnessRecordByObjectAction;
import com.humanharvest.organz.server.exceptions.GlobalControllerExceptionHandler.InvalidRequestException;
import com.humanharvest.organz.server.exceptions.IfMatchFailedException;
import com.humanharvest.organz.server.exceptions.IfMatchRequiredException;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.validators.client.ModifyIllnessValidator;
import com.sun.org.apache.regexp.internal.RE;
import java.util.List;
import javax.validation.constraints.Null;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientIllnessessController {

  /**
   * Gets Clients Current Illnesses
   * @param id Id Of Client
   * @return Returns list of Current Illnesses
   */
  @GetMapping("/clients/{id}/current-illnesses")
  public ResponseEntity<List<IllnessRecord>> getClientCurrentIllnesses(@PathVariable int id) {
      Client client = State.getClientManager().getClientByID(id);
      // Client does not exist
      if (client == null){
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      } else {
        HttpHeaders headers = new HttpHeaders();
        headers.add("ETag", client.getEtag());

        return new ResponseEntity<List<IllnessRecord>>(client.getCurrentIllnesses(),headers,HttpStatus.OK);
      }

  }

  /**
   * Gets Clients Past Illnesses
   * @param id Id Of Client
   * @return Returns list of past Illnesses, if client exists.
   */
  @GetMapping("/clients/{id}/past-illnesses")
  public ResponseEntity<List<IllnessRecord>> getClientPastIllnesses(@PathVariable int id){
    Client client = State.getClientManager().getClientByID(id);
    if (client == null){
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    } else {
      HttpHeaders headers = new HttpHeaders();
      headers.add("ETag",client.getEtag());
      return  new ResponseEntity<List<IllnessRecord>>(client.getPastIllnesses(),headers,HttpStatus.OK);
    }


  }

  @PatchMapping("/clients/{uid}/illnesses/{id}")
  @JsonView(Views.Overview.class)
  public ResponseEntity<IllnessRecord> patchIllness(@PathVariable int uid,
      @PathVariable int id,
      @RequestBody ModifyIllnessObject modifyIllnessObject,
      @RequestHeader(value = "If-Match",required = false)String ETag)
      throws IfMatchRequiredException, IfMatchFailedException, InvalidRequestException {
    if(!ModifyIllnessValidator.isValid(modifyIllnessObject)) {
      throw new InvalidRequestException();

    }

    System.out.println(modifyIllnessObject.toString());

    //Fetch the client given by ID
    Client client = State.getClientManager().getClientByID(uid);
    IllnessRecord record;
    try {
      record = client.getCurrentIllnesses().get(id-1); // starting index 1.
    } catch (Exception e) {
      //Record does not exist
      System.out.println("Record does not exist");
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    if (client == null) {
      //Return 404 if that client does not exist
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    if (ETag == null) {
      throw new IfMatchRequiredException();
    }
    System.out.println(client.getEtag());
    if (!client.getEtag().equals(ETag)) {
      throw new IfMatchFailedException();
    }

    //Create the old details to allow undoable action
    ModifyIllnessObject oldIllnessRecord = new ModifyIllnessObject();
    //Copy the values from the current record to our oldrecord
    BeanUtils.copyProperties(record, oldIllnessRecord, modifyIllnessObject.getUnmodifiedFields());
    //Make the action (this is a new action)
    ModifyIllnessRecordByObjectAction action = new ModifyIllnessRecordByObjectAction(client,
        State.getClientManager(),oldIllnessRecord,modifyIllnessObject);
    //Execute action, this would correspond to a specific users invoker in full version
    State.getInvoker().execute(action);

    //Add the new ETag to the headers
    HttpHeaders headers = new HttpHeaders();
    headers.setETag(client.getEtag());

    return new ResponseEntity<>(record, headers, HttpStatus.OK);

  }

  @PostMapping("/clients/{uid}/illnesses")
  @JsonView(Views.Overview.class)
  public ResponseEntity<IllnessRecord> postIllness(@RequestBody CreateIllnessView illnessView,
      @PathVariable int uid)
      throws InvalidRequestException {



    Client client = State.getClientManager().getClientByID(uid);
    if (client == null) {
      //Return 404 if that client does not exist
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    IllnessRecord record = new IllnessRecord(illnessView.getIllnessName(),
        illnessView.getDiagnosisDate(),illnessView.getCuredDate(),illnessView.isChronic());

    client.addIllnessRecord(record);

    HttpHeaders headers = new HttpHeaders();
    headers.setETag(client.getEtag());

    return new ResponseEntity<>(record,headers,HttpStatus.CREATED);




  }



}
