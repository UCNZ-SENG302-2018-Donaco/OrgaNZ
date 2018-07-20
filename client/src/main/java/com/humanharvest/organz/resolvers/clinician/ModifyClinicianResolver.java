package com.humanharvest.organz.resolvers.clinician;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.clinician.ModifyClinicianObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ModifyClinicianResolver {

    private Clinician clinician;
    private ModifyClinicianObject modifyClinicianObject;

    public ModifyClinicianResolver(Clinician clinician, ModifyClinicianObject modifyClinicianObject) {
        this.clinician = clinician;
        this.modifyClinicianObject = modifyClinicianObject;
    }

    public Clinician execute() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setIfMatch(State.getClinicianEtag());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());
        System.out.println(State.getClinicianEtag());
        String serialized;
        try {
            serialized = State.customObjectMapper().writeValueAsString(modifyClinicianObject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

        HttpEntity<String> entity = new HttpEntity<>(serialized, httpHeaders);

        System.out.println(serialized);

        ResponseEntity<Clinician> responseEntity = State.getRestTemplate()
                .exchange(
                        State.BASE_URI + "clinicians/{staffId}",
                        HttpMethod.PATCH,
                        entity,
                        Clinician.class,
                        clinician.getStaffId());

        State.setClinicianEtag(responseEntity.getHeaders().getETag());
        return responseEntity.getBody();
    }

}
