package com.humanharvest.organz.state;

import java.util.Optional;
import java.util.Set;

import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class ConfigManagerRest implements ConfigManager {

    // Countries

    @Override
    public Set<Country> getAllowedCountries() {

        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<Set<Country>> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity<Set<Country>> responseEntity = State.getRestTemplate()
                .exchange(State.getBaseUri() + "/config/countries", HttpMethod.GET, entity, new
                        ParameterizedTypeReference<Set<Country>>
                                () {
                        });

        return responseEntity.getBody();
    }

    @Override
    public void setAllowedCountries(Set<Country> countries) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Auth-Token", State.getToken());

        HttpEntity<Set<Country>> entity = new HttpEntity<>(countries, httpHeaders);

        State.getRestTemplate()
                .exchange(State.getBaseUri() + "/config/countries", HttpMethod.POST, entity,
                        new ParameterizedTypeReference<Set<Country>>() {
                        });
    }

    // Hospitals

    @Override
    public Set<Hospital> getHospitals() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Auth-Token", State.getToken());
        HttpEntity<Set<Hospital>> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity<Set<Hospital>> responseEntity = State.getRestTemplate()
                .exchange(State.getBaseUri() + "/config/hospitals", HttpMethod.GET, entity, new
                        ParameterizedTypeReference<Set<Hospital>>
                                () {
                        });

        return responseEntity.getBody();
    }

    @Override
    public void setHospitals(Set<Hospital> hospitals) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Auth-Token", State.getToken());

        HttpEntity<Set<Hospital>> entity = new HttpEntity<>(hospitals, httpHeaders);

        State.getRestTemplate()
                .exchange(State.getBaseUri() + "/config/hospitals", HttpMethod.POST, entity,
                        new ParameterizedTypeReference<Set<Hospital>>() {
                        });
    }

    @Override
    public Optional<Hospital> getHospitalById(long id) {
        for (Hospital hospital : getHospitals()) {
            if (hospital.getId().equals(id)) {
                return Optional.of(hospital);
            }
        }
        return Optional.empty();
    }

    @Override
    public void setTransplantProgram(long id, Set<Organ> transplantProgram) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Auth-Token", State.getToken());

        HttpEntity<Set<Organ>> entity = new HttpEntity<>(transplantProgram, httpHeaders);

        State.getRestTemplate()
                .exchange(State.getBaseUri() + "/config/hospitals/" + id + "/transplantPrograms",
                        HttpMethod.POST, entity, new ParameterizedTypeReference<Set<Organ>>() {
                        });
    }
}
