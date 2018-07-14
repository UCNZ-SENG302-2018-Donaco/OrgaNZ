package com.humanharvest.organz.state;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.humanharvest.organz.Administrator;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

public class AdministratorManagerRest implements AdministratorManager {

    @Override
    public void addAdministrator(Administrator administrator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Administrator> getAdministrators() {
        ResponseEntity<List<Administrator>> response = State.getRestTemplate().exchange(
                State.BASE_URI + "administrators",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Administrator>>() {
                });

        List<Administrator> administrators = response.getBody();
        if (administrators == null) {
            return Collections.emptyList();
        }
        return administrators;
    }

    @Override
    public Iterable<Administrator> getAdministratorsFiltered(String nameQuery, Integer offset, Integer count) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(State.BASE_URI + "administrators/");

        if (nameQuery != null) {
            builder = builder.queryParam("q", nameQuery);
        }
        if (offset != null) {
            builder = builder.queryParam("offset", offset);
        }
        if (count != null) {
            builder = builder.queryParam("count", count);
        }

        ResponseEntity<List<Administrator>> response = State.getRestTemplate().exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Administrator>>() {
                });

        List<Administrator> administrators = response.getBody();
        if (administrators == null) {
            return Collections.emptyList();
        }
        return administrators;
    }

    @Override
    public void removeAdministrator(Administrator administrator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean doesUsernameExist(String username) {
        // TODO?
        return false;
    }

    @Override
    public Optional<Administrator> getAdministratorByUsername(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Administrator getDefaultAdministrator() {
        throw new UnsupportedOperationException();
    }
}
