package com.humanharvest.organz.resolvers.client;

import java.time.LocalDate;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.client.SingleDateView;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class MarkClientAsDeadResolver {

    private Client client;
    private LocalDate dateOfDeath;

    public MarkClientAsDeadResolver(Client client, LocalDate dateOfDeath)
    {
        this.client = client;
        this.dateOfDeath = dateOfDeath;
    }

    public Client execute() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setIfMatch(State.getClientEtag());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());
        HttpEntity entity = new HttpEntity<>(new SingleDateView(dateOfDeath), httpHeaders);

        ResponseEntity<Client> responseEntity = State.getRestTemplate()
                .postForEntity(State.BASE_URI + "clients/{uid}/dead", entity, Client.class, client.getUid());

        State.setClientEtag(responseEntity.getHeaders().getETag());
        return responseEntity.getBody();
    }

}
