package com.humanharvest.organz.state;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.enums.ClientSortOptionsEnum;
import com.humanharvest.organz.utilities.enums.ClientType;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.IfMatchRequiredException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.type_converters.EnumSetToString;
import com.humanharvest.organz.views.client.PaginatedClientList;
import com.humanharvest.organz.views.client.PaginatedTransplantList;
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

        ResponseEntity<PaginatedClientList> clientResponse = State.getRestTemplate().exchange(
                State.BASE_URI + "clients",
                HttpMethod.GET,
                null,
                PaginatedClientList.class);

        return clientResponse.getBody().getClients();
    }

    @Override
    public PaginatedClientList getClients(
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

        HttpEntity<PaginatedClientList> response = State.getRestTemplate().exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                PaginatedClientList.class
        );

        return response.getBody();
    }

    @Override
    public void setClients(Collection<Client> clients) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addClient(Client client) throws AuthenticationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeClient(Client client)
            throws IfMatchFailedException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setIfMatch(State.getClientEtag());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());
        HttpEntity entity = new HttpEntity<>(httpHeaders);

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

        ResponseEntity<Client> responseEntity;
        try {
            responseEntity = State.getRestTemplate()
                    .exchange(State.BASE_URI + "clients/{id}", HttpMethod.GET, entity, Client.class, id);
        } catch (NotFoundException e) {
            return Optional.empty();
        }
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
        //todo
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<TransplantRequest> getAllCurrentTransplantRequests() {
        return getAllCurrentTransplantRequests(0, Integer.MAX_VALUE, null, null).getTransplantRequests();
    }

    @Override
    public PaginatedTransplantList getAllCurrentTransplantRequests(Integer offset, Integer count,
            Set<Region> regions, Set<Organ> organs) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set("X-Auth-Token", State.getToken());
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(State.BASE_URI + "/clients/transplantRequests")
                .queryParam("offset", offset)
                .queryParam("count", count);

        if (regions != null && !regions.isEmpty()) {
            builder = builder.queryParam("region", regions.stream()
                    .map(Region::name)
                    .collect(Collectors.toList()).toString()
                    .substring(1, regions.toString().length() - 1)
                    .replaceAll(" ", ""));
        }
        if (organs != null && !organs.isEmpty()) {
            builder = builder.queryParam("organs", organs.stream()
                    .map(Organ::name)
                    .collect(Collectors.toList()).toString()
                    .substring(1, organs.toString().length() - 1)
                    .replaceAll(" ", ""));
        }

        ResponseEntity<PaginatedTransplantList> response = State.getRestTemplate().exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                PaginatedTransplantList.class);

        return response.getBody();
    }

    @Override
    public List<HistoryItem> getAllHistoryItems() {
        return Collections.emptyList();
    }

    /**
     * @return a list of all organs available for donation
     */
    @Override
    public Collection<DonatedOrgan> getAllOrgansToDonate() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", State.getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Collection<DonatedOrgan>> responseEntity = State.getRestTemplate().exchange(State.BASE_URI + "organs",
                HttpMethod.GET,
                entity, new ParameterizedTypeReference<Collection<DonatedOrgan>>(){});

        return responseEntity.getBody();
    }

    @Override
    public DonatedOrgan manuallyExpireOrgan(DonatedOrgan organ) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token",State.getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        //Todo: Unfinished

        return organ;


    }
}
