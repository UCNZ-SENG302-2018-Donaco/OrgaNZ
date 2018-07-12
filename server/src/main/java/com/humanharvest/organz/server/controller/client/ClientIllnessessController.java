package com.humanharvest.organz.server.controller.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.state.State;
import com.sun.org.apache.regexp.internal.RE;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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


}
