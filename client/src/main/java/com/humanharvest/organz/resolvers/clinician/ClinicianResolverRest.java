package com.humanharvest.organz.resolvers.clinician;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.clinician.ModifyClinicianObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ClinicianResolverRest implements ClinicianResolver {

    public Clinician modifyClinician(Clinician clinician, ModifyClinicianObject modifyClinicianObject) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setIfMatch(State.getClinicianEtag());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());

        HttpEntity<ModifyClinicianObject> entity = new HttpEntity<>(modifyClinicianObject, httpHeaders);

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
