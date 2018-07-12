package com.humanharvest.organz.server.controller.client;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.Views.Client.CreateIllnessView;
import com.humanharvest.organz.Views.Client.ModifyClientObject;
import com.humanharvest.organz.Views.Client.Views;
import com.humanharvest.organz.Views.Client.Views.Overview;
import com.humanharvest.organz.actions.client.AddIllnessRecordAction;
import com.humanharvest.organz.server.exceptions.GlobalControllerExceptionHandler.InvalidRequestException;
import com.humanharvest.organz.server.exceptions.IfMatchFailedException;
import com.humanharvest.organz.server.exceptions.IfMatchRequiredException;
import com.humanharvest.organz.state.State;
import com.sun.org.apache.regexp.internal.RE;
import java.util.List;
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
