package com.humanharvest.organz.state;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.enums.*;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.IfMatchRequiredException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.type_converters.EnumSetToString;
import com.humanharvest.organz.views.client.DonatedOrganView;
import com.humanharvest.organz.views.client.PaginatedClientList;
import com.humanharvest.organz.views.client.PaginatedDonatedOrgansList;
import com.humanharvest.organz.views.client.PaginatedTransplantList;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
            Set<String> regions,
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
                .queryParam("regions", String.join(",", regions))
                .queryParam("birthGenders", EnumSetToString.convert(birthGenders))
                .queryParam("clientType", clientType)
                .queryParam("donating", EnumSetToString.convert(donating))
                .queryParam("requesting", EnumSetToString.convert(requesting))
                .queryParam("sortOption", sortOption)
                .queryParam("isReversed", isReversed);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

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
            Set<String> regions, Set<Organ> organs) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set("X-Auth-Token", State.getToken());
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(State.BASE_URI + "/clients/transplantRequests")
                .queryParam("offset", offset)
                .queryParam("count", count);

        if (regions != null && !regions.isEmpty()) {
            builder = builder.queryParam("regions", String.join(",", regions));
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
     * Gets all organs that are available for donation
     * @return a collection of all available organs for donation
     */
    @Override
    public Collection<DonatedOrgan> getAllOrgansToDonate() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", State.getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<PaginatedDonatedOrgansList> responseEntity = State.getRestTemplate().exchange(
                State.BASE_URI + "/clients/organs",
                HttpMethod.GET,
                entity, PaginatedDonatedOrgansList.class);

        return responseEntity.getBody().getDonatedOrgans().stream()
                .map(DonatedOrganView::getDonatedOrgan)
                .collect(Collectors.toList());
    }

    /**
     * Gets all organs to donate for the specified regions and organTypes.
     * @param regions regions to filter by. If empty, all regions are selected
     * @param organType organ types to filter by. If empty, all types are selected
     * @return A collection of the the organs available to donate based off the specified filters.
     */
    @Override
    public PaginatedDonatedOrgansList getAllOrgansToDonate(Integer offset, Integer count, Set<String> regions,
                                                           Set<Organ> organType, DonatedOrganSortOptionsEnum sortOption, Boolean reversed) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", State.getToken());
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(State.BASE_URI + "/clients/organs")
                .queryParam("offset", offset)
                .queryParam("count", count)
                .queryParam("regions", String.join(",", regions))
                .queryParam("organType", EnumSetToString.convert(EnumSet.copyOf(organType)))
                .queryParam("sortOption", sortOption)
                .queryParam("reversed", reversed);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<PaginatedDonatedOrgansList> responseEntity = State.getRestTemplate().exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity, PaginatedDonatedOrgansList.class);

        return responseEntity.getBody();
    }

    /**
     * @param donatedOrgan available organ to find potential matches for
     * @return list of potential recipients of the client
     */
    @Override
    public List<Client> getOrganMatches(DonatedOrgan donatedOrgan) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Auth-Token", State.getToken());

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<List<Client>> responseEntity = State.getRestTemplate().exchange(State.BASE_URI +
                "/matchOrganToRecipients/" + donatedOrgan.getId(), HttpMethod.GET, entity, new
                ParameterizedTypeReference<List<Client>>() {
                });

        return responseEntity.getBody();
    }
}
