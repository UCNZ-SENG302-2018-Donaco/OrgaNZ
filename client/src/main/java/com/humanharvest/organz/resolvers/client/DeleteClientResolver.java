package com.humanharvest.organz.resolvers.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.State;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class DeleteClientResolver {

    private Client client;

    public DeleteClientResolver(Client client) {
        this.client = client;
    }

    public void execute() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setIfMatch(State.getClientEtag());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());
        HttpEntity entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> responseEntity = State.getRestTemplate()
                .exchange(State.BASE_URI + "clients/{uid}", HttpMethod.DELETE, entity, String.class, client.getUid());

        State.setClientEtag(responseEntity.getHeaders().getETag());
    }
}
