package com.humanharvest.organz.resolvers.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.client.CreateMedicationRecordView;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class AddMedicationRecordResolver {

    private static final String baseUrl = "http://localhost:8080/";

    private Client client;
    private CreateMedicationRecordView recordView;

    public AddMedicationRecordResolver(Client client, CreateMedicationRecordView recordView) {
        this.client = client;
        this.recordView = recordView;
    }

    public MedicationRecord execute() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setIfMatch(State.getClientEtag());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity entity = new HttpEntity<>(recordView, httpHeaders);

        ResponseEntity<MedicationRecord> responseEntity = State.getRestTemplate()
                .postForEntity(State.BASE_URI + "clients/" + client.getUid() + "/medications", entity,
                        MedicationRecord.class);

        State.setClientEtag(responseEntity.getHeaders().getETag());
        return responseEntity.getBody();
    }

}
