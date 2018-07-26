package com.humanharvest.organz.state;

import java.util.EnumSet;

import com.humanharvest.organz.utilities.enums.Country;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class ConfigManagerRest implements ConfigManager {

    @Override
    public EnumSet<Country> getAllowedCountries() {

        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<EnumSet<Country>> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity<EnumSet<Country>> responseEntity = State.getRestTemplate()
                .exchange(State.BASE_URI + "/config/countries", HttpMethod.GET, entity, new
                ParameterizedTypeReference<EnumSet<Country>>
                (){});

        return responseEntity.getBody();
    }

    @Override
    public void setAllowedCountries(EnumSet<Country> countries) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Auth-Token", State.getToken());


        HttpEntity<EnumSet<Country>> entity = new HttpEntity<>(countries, httpHeaders);

        State.getRestTemplate()
                .exchange(State.BASE_URI + "/config/countries", HttpMethod.POST, entity,
                        new ParameterizedTypeReference<EnumSet<Country>>(){});
    }
}
