package com.humanharvest.organz.resolvers.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.client.CreateClientView;
import com.humanharvest.organz.views.client.ModifyClientObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class CreateClientResolver {

    private CreateClientView createClientView;

    public CreateClientResolver(CreateClientView createClientView) {
        this.createClientView = createClientView;
    }

    public void execute() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

        ResponseEntity<Client> responseEntity = State.getRestTemplate()
                .postForEntity(State.BASE_URI + "clients", createClientView, Client.class);




        State.setClientEtag(responseEntity.getHeaders().getETag());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            System.err.println(responseEntity.toString());
        }
    }

}
