package com.humanharvest.organz.resolvers.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.views.client.ModifyClientObject;
import com.humanharvest.organz.state.State;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ModifyClientDetailsResolver {

    private static final String baseUrl = "http://localhost:8080/";

    private Client client;
    private ModifyClientObject modifyClientObject;

    public ModifyClientDetailsResolver(Client client, ModifyClientObject modifyClientObject) {
        this.client = client;
        this.modifyClientObject = modifyClientObject;
    }

    public void execute() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setIfMatch(State.getClientEtag());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        System.out.println(State.getClientEtag());
        String serialized;
        try {
            serialized = new ObjectMapper().writeValueAsString(modifyClientObject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        HttpEntity<String> entity = new HttpEntity<>(serialized, httpHeaders);

        System.out.println(serialized);

        ResponseEntity<Client> responseEntity = State.getRestTemplate()
                .exchange(
                        baseUrl + "clients/{uid}",
                        HttpMethod.PATCH,
                        entity,
                        Client.class,
                        client.getUid());

        State.setClientEtag(responseEntity.getHeaders().getETag());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            System.err.println(responseEntity.toString());
        }
    }
}
