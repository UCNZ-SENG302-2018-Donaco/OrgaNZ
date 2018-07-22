package com.humanharvest.organz.resolvers.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.state.State;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class DeleteIllnessRecordResolver {

  private Client client;
  private IllnessRecord record;

  public DeleteIllnessRecordResolver(Client client, IllnessRecord record){
    this.client = client;
    this.record = record;

  }

  public void execute(){
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setIfMatch(State.getClientEtag());
    httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
    httpHeaders.set("X-Auth-Token", State.getToken());
    HttpEntity entity = new HttpEntity<>(httpHeaders);

    int id = client.getAllIllnessHistory().indexOf(record);

    ResponseEntity<String> responseEntity = State.getRestTemplate()
        .exchange(State.BASE_URI + "clients/{uid}/illnesses/{id}", HttpMethod.DELETE, entity, String.class, client.getUid(),id);

    State.setClientEtag(responseEntity.getHeaders().getETag());
  }


}
