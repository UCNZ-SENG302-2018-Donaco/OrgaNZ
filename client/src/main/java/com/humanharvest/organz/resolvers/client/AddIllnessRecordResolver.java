package com.humanharvest.organz.resolvers.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.client.CreateIllnessView;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class AddIllnessRecordResolver {
  private Client client;
  private CreateIllnessView createIllnessView;


  public AddIllnessRecordResolver(Client client,CreateIllnessView createIllnessView){
    this.client = client;
    this.createIllnessView = createIllnessView;
  }

  public List<IllnessRecord> execute() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setIfMatch(State.getClientEtag());
    httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

    HttpEntity entity = new HttpEntity<>(createIllnessView, httpHeaders);

    ResponseEntity<List<IllnessRecord>> responseEntity = State.getRestTemplate()
        .exchange(State.BASE_URI + "clients/" + client.getUid() + "/illnesses", HttpMethod.POST, entity,
            new ParameterizedTypeReference<List<IllnessRecord>>() {});

    State.setClientEtag(responseEntity.getHeaders().getETag());
    client.setIllnessHistory(responseEntity.getBody());

    return responseEntity.getBody();

  }

}
