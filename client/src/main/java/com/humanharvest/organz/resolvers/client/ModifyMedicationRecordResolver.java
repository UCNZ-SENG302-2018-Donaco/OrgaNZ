package com.humanharvest.organz.resolvers.client;

import java.time.LocalDate;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.state.State;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ModifyMedicationRecordResolver {

    private Client client;
    private MedicationRecord record;
    private LocalDate stopDate;

    public ModifyMedicationRecordResolver(Client client, MedicationRecord record, LocalDate stopDate) {
        this.client = client;
        this.record = record;
        this.stopDate = stopDate; // Start date of a medication cannot be modified
    }

    public MedicationRecord execute() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setIfMatch(State.getClientEtag());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity entity = new HttpEntity(httpHeaders);

        // todo needs to be changed to use the id rather than the index once it is working
        int id = client.getAllMedications().indexOf(record);

        String modification;
        if (stopDate == null) {
            modification = "/start";
        } else {
            modification = "/stop";
        }

        ResponseEntity<MedicationRecord> responseEntity = State.getRestTemplate().postForEntity(State.BASE_URI +
                "clients/" + client.getUid() + "/medications" + "/" + id + modification, entity, MedicationRecord
                .class);

        State.setClientEtag(responseEntity.getHeaders().getETag());
        return responseEntity.getBody();
    }
}
