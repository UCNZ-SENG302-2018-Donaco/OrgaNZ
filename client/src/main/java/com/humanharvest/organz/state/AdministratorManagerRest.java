package com.humanharvest.organz.state;

import com.humanharvest.organz.Administrator;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AdministratorManagerRest implements AdministratorManager {

    @Override
    public void addAdministrator(Administrator administrator) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());
        HttpEntity<Administrator> entity = new HttpEntity<>(administrator, httpHeaders);

        State.getRestTemplate().postForObject(
                State.getBaseUri() + "administrators",
                entity,
                Administrator.class);
    }

    @Override
    public List<Administrator> getAdministrators() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());
        HttpEntity<?> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity<List<Administrator>> response = State.getRestTemplate().exchange(
                State.getBaseUri() + "administrators",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Administrator>>() {
                });

        List<Administrator> administrators = response.getBody();
        if (administrators == null) {
            return Collections.emptyList();
        }
        return administrators;
    }

    @Override
    public Iterable<Administrator> getAdministratorsFiltered(String nameQuery, Integer offset, Integer count) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(State.getBaseUri() + "administrators/");

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
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setIfMatch(State.getAdministratorEtag());
        httpHeaders.set("X-Auth-Token", State.getToken());

        HttpEntity<Administrator> entity = new HttpEntity<>(null, httpHeaders);

        State.getRestTemplate().exchange(State.getBaseUri() + "administrators/{username}",
                HttpMethod.DELETE,
                entity,
                String.class, administrator.getUsername());
    }

    @Override
    public boolean doesUsernameExist(String username) {
        // TODO?
        return false;
    }

    @Override
    public Optional<Administrator> getAdministratorByUsername(String username) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());

        HttpEntity<?> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity<Administrator> responseEntity = State.getRestTemplate().exchange(
                State.getBaseUri() + "administrators/{username}",
                HttpMethod.GET,
                entity,
                Administrator.class,
                username);

        State.setAdministratorEtag(responseEntity.getHeaders().getETag());
        return Optional.ofNullable(responseEntity.getBody());
    }

    @Override
    public Administrator getDefaultAdministrator() {
        return getAdministratorByUsername("admin").orElseThrow(IllegalStateException::new);
    }

    @Override
    public void applyChangesTo(Administrator administrator) {
        throw new UnsupportedOperationException();
    }
}
