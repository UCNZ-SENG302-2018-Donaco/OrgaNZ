package com.humanharvest.organz.resolvers.client;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.state.State;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class CreateClinicianResolver {

//    private CreateClientView createClientView;

    public CreateClinicianResolver() {
    }

    public Clinician execute() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());
        HttpEntity entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity<Clinician> responseEntity = State.getRestTemplate()
                .postForEntity(State.BASE_URI + "clients", entity, Clinician.class);

        State.setClientEtag(responseEntity.getHeaders().getETag());
        return responseEntity.getBody();
    }

}
