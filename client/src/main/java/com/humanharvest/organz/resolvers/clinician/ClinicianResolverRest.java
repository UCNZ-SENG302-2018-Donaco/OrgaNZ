package com.humanharvest.organz.resolvers.clinician;

import java.util.List;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.clinician.ModifyClinicianObject;
import org.springframework.core.ParameterizedTypeReference;
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
                        State.getBaseUri() + "clinicians/{staffId}",
                        HttpMethod.PATCH,
                        entity,
                        Clinician.class,
                        clinician.getStaffId());

        State.setClinicianEtag(responseEntity.getHeaders().getETag());
        return responseEntity.getBody();
    }

    @Override
    public List<HistoryItem> getHistory(Clinician clinician) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());

        HttpEntity entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity<List<HistoryItem>> responseEntity = State.getRestTemplate()
                .exchange(
                        State.getBaseUri() + "clinicians/{staffId}/history",
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<List<HistoryItem>>() {
                        },
                        clinician.getStaffId());

        State.setClinicianEtag(responseEntity.getHeaders().getETag());
        return responseEntity.getBody();
    }
}
