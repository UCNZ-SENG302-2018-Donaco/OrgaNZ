package com.humanharvest.organz.state;

import java.io.File;
import java.time.LocalDate;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.enums.ClientSortOptionsEnum;
import com.humanharvest.organz.utilities.enums.ClientType;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.IfMatchRequiredException;
import com.humanharvest.organz.utilities.type_converters.EnumSetToString;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

public class ClientManagerRest implements ClientManager {

    @Override
    public List<Client> getClients() throws AuthenticationException {
        ParameterizedTypeReference<List<Client>> typeReference =
                new ParameterizedTypeReference<List<Client>>() {
                };

        ResponseEntity<List<Client>> clientResponse = State.getRestTemplate().exchange(
                State.BASE_URI + "clients",
                HttpMethod.GET,
                null,
                typeReference);

        return clientResponse.getBody();
    }

    @Override
    public List<Client> getClients(
            String q,
            Integer offset,
            Integer count,
            Integer minimumAge,
            Integer maximumAge,
            EnumSet<Region> regions,
            EnumSet<Gender> birthGenders,
            ClientType clientType,
            EnumSet<Organ> donating,
            EnumSet<Organ> requesting,
            ClientSortOptionsEnum sortOption,
            Boolean isReversed) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(State.BASE_URI + "/clients")
                .queryParam("q", q)
                .queryParam("offset", offset)
                .queryParam("count", count)
                .queryParam("minimumAge", minimumAge)
                .queryParam("maximumAge", maximumAge)
                .queryParam("regions", EnumSetToString.convert(regions))
                .queryParam("birthGenders", EnumSetToString.convert(birthGenders))
                .queryParam("clientType", clientType)
                .queryParam("donating", EnumSetToString.convert(donating))
                .queryParam("requesting", EnumSetToString.convert(requesting))
                .queryParam("sortOption", sortOption)
                .queryParam("isReversed", isReversed);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        String outString = builder.toUriString();

        System.out.println(outString);

        ParameterizedTypeReference<List<Client>> typeReference =
                new ParameterizedTypeReference<List<Client>>() {
                };

        HttpEntity<List<Client>> response = State.getRestTemplate().exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                typeReference
        );

        return response.getBody();
    }

    @Override
    public void setClients(Collection<Client> clients) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addClient(Client client) throws AuthenticationException {
        State.getRestTemplate().postForObject(State.BASE_URI + "clients", new HttpEntity<>(client), Client.class);
    }

    @Override
    public void removeClient(Client client)
            throws IfMatchFailedException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setIfMatch(State.getClientEtag());
        httpHeaders.set("X-Auth-Token", State.getToken());

        HttpEntity entity = new HttpEntity<>(null, httpHeaders);

        State.getRestTemplate().exchange(State.BASE_URI + "clients/{uid}", HttpMethod.DELETE,
                entity,
                String.class, client.getUid());
    }

    @Override
    public void applyChangesTo(Client client) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Client> getClientByID(int id)
            throws AuthenticationException, IfMatchFailedException, IfMatchRequiredException {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());

        HttpEntity<Client> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity<Client> responseEntity = State.getRestTemplate()
                .exchange(State.BASE_URI + "clients/{id}", HttpMethod.GET, entity, Client.class, id);
        State.setClientEtag(responseEntity.getHeaders().getETag());
        return Optional.ofNullable(responseEntity.getBody());
    }

    @Override
    public boolean doesClientExist(String firstName, String lastName, LocalDate dateOfBirth) {
        // TODO?
        return false;
    }

    @Override
    public Collection<TransplantRequest> getAllTransplantRequests() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<TransplantRequest> getAllCurrentTransplantRequests() {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getClientImage(int uid) {
        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setContentType(MediaType.);
        httpHeaders.set("X-Auth-Token", State.getToken());
        HttpEntity<Client> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity<File> responseEntity = State.getRestTemplate().exchange(State.BASE_URI +
                "clients/{uid}/image", HttpMethod.GET, entity, File.class, uid);

        return responseEntity.getBody();
    }
}
