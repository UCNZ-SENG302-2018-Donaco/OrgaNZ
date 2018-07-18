package com.humanharvest.organz.resolvers.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.client.CreateClientView;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class CreateClientResolver {

    private CreateClientView createClientView;

    public CreateClientResolver(CreateClientView createClientView) {
        this.createClientView = createClientView;
    }

    public Client execute() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());
        HttpEntity entity = new HttpEntity<>(createClientView, httpHeaders);

        ResponseEntity<Client> responseEntity = State.getRestTemplate()
                .postForEntity(State.BASE_URI + "clients", entity, Client.class);

        State.setClientEtag(responseEntity.getHeaders().getETag());
        return responseEntity.getBody();
    }

}
