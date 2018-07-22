package com.humanharvest.organz.resolvers.client;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.views.client.CreateTransplantRequestView;
import com.humanharvest.organz.views.client.ModifyClientObject;
import com.humanharvest.organz.views.client.ResolveTransplantRequestView;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ClientResolverRest implements ClientResolver {

    @Override
    public Map<Organ, Boolean> getOrganDonationStatus(Client client) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());

        HttpEntity<?> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity<Map<Organ, Boolean>> responseEntity = State.getRestTemplate().exchange
                (State.BASE_URI + "clients/{id}/donationStatus", HttpMethod.GET, entity, new
                        ParameterizedTypeReference<Map<Organ, Boolean>>() {
                        }, client.getUid());
        State.setClientEtag(responseEntity.getHeaders().getETag());
        client.setOrganDonationStatus(responseEntity.getBody());
        return responseEntity.getBody();
    }

    @Override
    public List<TransplantRequest> getTransplantRequests(Client client) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());

        HttpEntity<?> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity<List<TransplantRequest>> responseEntity = State.getRestTemplate().exchange
                (State.BASE_URI + "clients/{id}/transplantRequests", HttpMethod.GET, entity, new
                        ParameterizedTypeReference<List<TransplantRequest>>() {
                        }, client.getUid());
        State.setClientEtag(responseEntity.getHeaders().getETag());
        client.setTransplantRequests(responseEntity.getBody());
        return responseEntity.getBody();
    }

    @Override
    public List<MedicationRecord> getMedicationRecords(Client client) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());

        HttpEntity<?> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity<List<MedicationRecord>> responseEntity = State.getRestTemplate().exchange
                (State.BASE_URI + "clients/{id}/medications", HttpMethod.GET, entity,
                        new ParameterizedTypeReference<List<MedicationRecord>>() {
                        }, client.getUid());
        State.setClientEtag(responseEntity.getHeaders().getETag());
        client.setMedicationHistory(responseEntity.getBody());
        return responseEntity.getBody();
    }

    @Override
    public TransplantRequest resolveTransplantRequest(Client client, ResolveTransplantRequestView request,
            int transplantRequestIndex) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setIfMatch(State.getClientEtag());
        httpHeaders.set("X-Auth-Token", State.getToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<ResolveTransplantRequestView> entity = new HttpEntity<>(request, httpHeaders);

        ResponseEntity<TransplantRequest> responseEntity = State.getRestTemplate().exchange(
                        State.BASE_URI + "clients/" + client.getUid() + "/transplantRequests/" + transplantRequestIndex,
                        HttpMethod.PATCH, entity, TransplantRequest.class);

        State.setClientEtag(responseEntity.getHeaders().getETag());
        return responseEntity.getBody();
    }

    @Override
    public List<TransplantRequest> createTransplantRequest(Client client, CreateTransplantRequestView request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setIfMatch(State.getClientEtag());
        httpHeaders.set("X-Auth-Token", State.getToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<?> entity = new HttpEntity<>(request, httpHeaders);

        ResponseEntity<List<TransplantRequest>> responseEntity = State.getRestTemplate().exchange(
                State.BASE_URI + "clients/" + client.getUid() + "/transplantRequests", HttpMethod.POST,
                entity, new ParameterizedTypeReference<List<TransplantRequest>>() {
                });

        State.setClientEtag(responseEntity.getHeaders().getETag());
        return responseEntity.getBody();
    }

    @Override
    public Client modifyClientDetails(Client client, ModifyClientObject modifyClientObject) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setIfMatch(State.getClientEtag());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());
        String serialized;
        try {
            serialized = State.customObjectMapper().writeValueAsString(modifyClientObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpEntity<String> entity = new HttpEntity<>(serialized, httpHeaders);

        ResponseEntity<Client> responseEntity = State.getRestTemplate()
                .exchange(
                        State.BASE_URI + "clients/{uid}",
                        HttpMethod.PATCH,
                        entity,
                        Client.class,
                        client.getUid());

        State.setClientEtag(responseEntity.getHeaders().getETag());
        return responseEntity.getBody();
    }
}

