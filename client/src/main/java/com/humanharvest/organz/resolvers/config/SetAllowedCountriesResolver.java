package com.humanharvest.organz.resolvers.config;

import java.util.EnumSet;

import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class SetAllowedCountriesResolver {

    private EnumSet<Country> countries;

    public SetAllowedCountriesResolver(EnumSet<Country> countries) {
        this.countries = countries;
    }

    public void execute() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Auth-Token", State.getToken());


        HttpEntity<EnumSet<Country>> entity = new HttpEntity<>(countries, httpHeaders);

        ResponseEntity responseEntity = State.getRestTemplate()
                .exchange(State.BASE_URI + "/config/countries", HttpMethod.POST, entity,
                        new ParameterizedTypeReference<EnumSet<Country>>(){});
    }
}
