package com.humanharvest.organz.resolvers.config;

import java.util.Set;

import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public class ConfigResolverRest implements ConfigResolver {

    @Override
    public void setTransplantProgramsForHospital(Hospital hospital, Set<Organ> transplantPrograms) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Auth-Token", State.getToken());

        HttpEntity<Set<Organ>> entity = new HttpEntity<>(transplantPrograms, httpHeaders);

        State.getRestTemplate()
                .exchange(State.getBaseUri() + "config/hospitals/{id}/transplantPrograms",
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<Set<Organ>>() {
                        },
                        hospital.getId());
    }
}
