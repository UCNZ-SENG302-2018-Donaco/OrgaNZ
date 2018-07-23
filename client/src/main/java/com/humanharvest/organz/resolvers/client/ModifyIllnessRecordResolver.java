package com.humanharvest.organz.resolvers.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.state.State;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ModifyIllnessRecordResolver {

  private Client client;
  private IllnessRecord record;


  public ModifyIllnessRecordResolver(Client client,IllnessRecord record){
    this.client = client;
    this.record = record;
  }

  public IllnessRecord execute(){
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setIfMatch(State.getClientEtag());
    httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

    HttpEntity<IllnessRecord> entity = new HttpEntity<>(httpHeaders);

    // TODO: Finish Patch Implementation
    int id = client.getAllIllnessHistory().indexOf(record);

    IllnessRecord illnessRecord = client.getAllIllnessHistory().get(id);

    return illnessRecord;
  }

}
