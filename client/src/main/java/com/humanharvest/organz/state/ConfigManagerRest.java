package com.humanharvest.organz.state;

import java.util.Set;

import com.humanharvest.organz.utilities.enums.Country;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class ConfigManagerRest implements ConfigManager {

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
}
