package com.humanharvest.organz.state;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DashboardStatistics;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.TransplantRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.enums.ClientSortOptionsEnum;
import com.humanharvest.organz.utilities.enums.ClientType;
import com.humanharvest.organz.utilities.enums.DonatedOrganSortOptionsEnum;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.IfMatchRequiredException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.type_converters.EnumSetToString;
import com.humanharvest.organz.views.client.DonatedOrganView;
import com.humanharvest.organz.views.client.PaginatedClientList;
import com.humanharvest.organz.views.client.PaginatedDonatedOrgansList;
import com.humanharvest.organz.views.client.PaginatedTransplantList;
import com.humanharvest.organz.views.client.TransplantRecordView;
import com.humanharvest.organz.views.client.TransplantRequestView;

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
                State.getBaseUri() + "clients",
                HttpMethod.GET,
                null,
                PaginatedClientList.class);

        return clientResponse.getBody().getClients();
    }

    @Override
    public void setClients(Collection<Client> clients) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaginatedClientList getClients(
            String q,
            Integer offset,
            Integer count,
            Integer minimumAge,
            Integer maximumAge,
            Set<String> regions,
            Set<Gender> birthGenders,
            ClientType clientType,
            Set<Organ> donating,
            Set<Organ> requesting,
            ClientSortOptionsEnum sortOption,
            Boolean isReversed) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(State.getBaseUri() + "/clients")
                .queryParam("q", q)
                .queryParam("offset", offset)
                .queryParam("count", count)
                .queryParam("minimumAge", minimumAge)
                .queryParam("maximumAge", maximumAge)
                .queryParam("regions", String.join(",", regions))
                .queryParam("birthGenders", EnumSetToString.convert(birthGenders))
                .queryParam("clientType", clientType.name())
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
        HttpEntity<?> entity = new HttpEntity<>(httpHeaders);

        State.getRestTemplate().exchange(State.getBaseUri() + "clients/{uid}", HttpMethod.DELETE,
                entity,
                String.class, client.getUid());
    }

    @Override
    public void applyChangesTo(Client client) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void applyChangesTo(DonatedOrgan donatedOrgan) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void applyChangesTo(TransplantRequest request) {
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
                    .exchange(State.getBaseUri() + "clients/{id}", HttpMethod.GET, entity, Client.class, id);
        } catch (NotFoundException ignored) {
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

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(State.getBaseUri() + "/clients/transplantRequests")
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
                    .replace(" ", ""));
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

    @Override
    public DashboardStatistics getStatistics() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", State.getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<DashboardStatistics> responseEntity = State.getRestTemplate().exchange(State.getBaseUri() +
                "/statistics", HttpMethod.GET, entity, DashboardStatistics.class);

        return responseEntity.getBody();
    }

    /**
     * Gets all organs that are available for donation
     *
     * @return a collection of all available organs for donation
     */
    @Override
    public Collection<DonatedOrgan> getAllOrgansToDonate() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", State.getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<PaginatedDonatedOrgansList> responseEntity = State.getRestTemplate().exchange(
                State.getBaseUri() + "/clients/organs",
                HttpMethod.GET,
                entity, PaginatedDonatedOrgansList.class);

        if (responseEntity.getBody() == null) {
            return new ArrayList<>();
        } else {
            return responseEntity.getBody().getDonatedOrgans().stream()
                    .map(DonatedOrganView::getDonatedOrgan)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Gets all organs to donate for the specified regions and organTypes.
     *
     * @param regions regions to filter by. If empty, all regions are selected
     * @param organType organ types to filter by. If empty, all types are selected
     * @return A collection of the the organs available to donate based off the specified filters.
     */
    @Override
    public PaginatedDonatedOrgansList getAllOrgansToDonate(
            Integer offset,
            Integer count,
            Set<String> regions,
            Set<Organ> organType,
            DonatedOrganSortOptionsEnum sortOption,
            Boolean reversed) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", State.getToken());
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(State.getBaseUri() + "/clients/organs")
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

        ResponseEntity<List<Client>> responseEntity = State.getRestTemplate().exchange(State.getBaseUri() +
                "/matchOrganToRecipients/" + donatedOrgan.getId(), HttpMethod.GET, entity, new
                ParameterizedTypeReference<List<Client>>() {
                });

        return responseEntity.getBody();
    }

    /**
     * Uses endpoint to get list of viable deceased donors
     * @return list of viable deceased donors
     */
    @Override
    public List<Client> getViableDeceasedDonors() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Auth-Token", State.getToken());

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<List<Client>> responseEntity = State.getRestTemplate().exchange(State.getBaseUri() +
                "/viableDeceasedDonors", HttpMethod.GET, entity, new
                ParameterizedTypeReference<List<Client>>() {
                });

        return responseEntity.getBody();
    }

    /**
     * @param donatedOrgan available organ to find potential matches for
     * @return list of TransplantRequests that will match the given organ
     */
    @Override
    public List<TransplantRequest> getMatchingOrganTransplants(DonatedOrgan donatedOrgan) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Auth-Token", State.getToken());

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<List<TransplantRequestView>> responseEntity = State.getRestTemplate()
                .exchange(State.getBaseUri() +
                        "/matchOrganToTransplants/" + donatedOrgan.getId(), HttpMethod.GET, entity, new
                        ParameterizedTypeReference<List<TransplantRequestView>>() {
                        });

        if (responseEntity.getBody() != null) {
            return responseEntity.getBody().stream()
                    .map(TransplantRequestView::getTransplantRequest)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * @param donatedOrgan available organ to find potential matches for
     * @return The matching TransplantRecord for the given organ
     */
    @Override
    public TransplantRecord getMatchingOrganTransplantRecord(DonatedOrgan donatedOrgan) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Auth-Token", State.getToken());

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<TransplantRecordView> responseEntity = State.getRestTemplate()
                .exchange(State.getBaseUri() +
                                "/matchOrganToTransplantRecord/" + donatedOrgan.getId(), HttpMethod.GET, entity,
                        TransplantRecordView.class);

        if (responseEntity.getBody() == null) {
            return null;
        } else {
            return responseEntity.getBody().getTransplantRecord();
        }
    }
}
