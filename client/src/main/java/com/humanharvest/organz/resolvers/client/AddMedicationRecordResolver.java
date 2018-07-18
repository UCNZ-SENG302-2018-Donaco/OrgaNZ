package com.humanharvest.organz.resolvers.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.state.State;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class AddMedicationRecordResolver {

    private static final String baseUrl = "http://localhost:8080/";

    private Client client;
    private MedicationRecord medicationRecord;

    public AddMedicationRecordResolver(Client client, MedicationRecord medicationRecord) {
        this.client = client;
        this.medicationRecord = medicationRecord;
    }

    public void execute() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setIfMatch(State.getClientEtag());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

        String serialized;

        try {
            serialized = new ObjectMapper().writeValueAsString(medicationRecord);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        HttpEntity<String> entity = new HttpEntity<>(serialized, httpHeaders);

        ResponseEntity<MedicationRecord> responseEntity = State.getRestTemplate()
                .exchange(
                        baseUrl + "clients/{uid}/medications",
                        HttpMethod.POST,
                        entity,
                        MedicationRecord.class,
                        client.getUid());

        State.setClientEtag(responseEntity.getHeaders().getETag());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            System.err.println(responseEntity.toString());
        }
    }

}
