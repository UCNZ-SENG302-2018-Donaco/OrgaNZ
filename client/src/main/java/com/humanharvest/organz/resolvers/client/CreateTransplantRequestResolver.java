package com.humanharvest.organz.resolvers.client;

import java.util.List;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.client.CreateTransplantRequestView;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class CreateTransplantRequestResolver {

    private Client client;
    private CreateTransplantRequestView transplantRequestView;

    public CreateTransplantRequestResolver(Client client, CreateTransplantRequestView transplantRequestView) {
        this.client = client;
        this.transplantRequestView = transplantRequestView;
    }

    public List<TransplantRequest> execute() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setIfMatch(State.getClientEtag());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity entity = new HttpEntity<>(transplantRequestView, httpHeaders);

        ResponseEntity<List> responseEntity = State.getRestTemplate()
                .postForEntity(State.BASE_URI + "clients/" + client.getUid() + "/transplantRequests", entity,
                        List.class);

        State.setClientEtag(responseEntity.getHeaders().getETag());
        return responseEntity.getBody();
    }

}

