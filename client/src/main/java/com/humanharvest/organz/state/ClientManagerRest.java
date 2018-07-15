package com.humanharvest.organz.state;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class ClientManagerRest implements ClientManager {

    @Override
    public List<Client> getClients() {
        ResponseEntity<List<Client>> clientResponse = State.getRestTemplate().exchange
                (State.BASE_URI + "clients", HttpMethod.GET, null, new ParameterizedTypeReference<List<Client>>() {
                });
        List<Client> restClients = clientResponse.getBody();
        if (restClients == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(restClients);
    }

    @Override
    public void setClients(Collection<Client> clients) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addClient(Client client) {
        State.getRestTemplate().postForObject(State.BASE_URI + "clients", new HttpEntity<>(client), Client.class);
    }

    @Override
    public void removeClient(Client client) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setIfMatch(State.getClientEtag());
        HttpEntity entity = new HttpEntity<>(null, httpHeaders);


        ResponseEntity response = State.getRestTemplate().exchange(State.BASE_URI + "clients/{uid}", HttpMethod.DELETE,
                entity,
                String.class, client.getUid());

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("worked");
        } else if (response.getStatusCode().is5xxServerError()) {
            System.out.println("Server error");
        }
    }

    @Override
    public void applyChangesTo(Client client) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Client getClientByID(int id) {
        ResponseEntity<Client> responseEntity = State.getRestTemplate()
                .exchange(State.BASE_URI + "clients/{id}", HttpMethod.GET, null, Client.class, id);
        State.setClientEtag(responseEntity.getHeaders().getETag());
        return responseEntity.getBody();
    }

    @Override
    public boolean doesClientExist(String firstName, String lastName, LocalDate dateOfBirth) {
        // TODO?
        return false;
    }

    @Override
    public int nextUid() {
        return 0;
    }

    @Override
    public Collection<TransplantRequest> getAllTransplantRequests() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<TransplantRequest> getAllCurrentTransplantRequests() {
        throw new UnsupportedOperationException();
    }
}
