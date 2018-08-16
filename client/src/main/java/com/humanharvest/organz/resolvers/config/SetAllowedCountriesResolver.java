package com.humanharvest.organz.resolvers.config;

import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import java.util.Set;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public class SetAllowedCountriesResolver {

    private Set<Country> countries;

    public SetAllowedCountriesResolver(Set<Country> countries) {
        this.countries = countries;
    }

    public void execute() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Auth-Token", State.getToken());


        HttpEntity<Set<Country>> entity = new HttpEntity<>(countries, httpHeaders);

        State.getRestTemplate()
            .exchange(State.getBaseUri() + "/config/countries", HttpMethod.POST, entity,
                new ParameterizedTypeReference<Set<Country>>(){});
    }
}
