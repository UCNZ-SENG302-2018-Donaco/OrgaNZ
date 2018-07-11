package com.humanharvest.organz.state;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.RestClient;
import com.humanharvest.organz.TransplantRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class ClientManagerRest implements ClientManager {

    private static final String baseUrl = "http://localhost:8080/";

    @Override
    public List<Client> getClients() {
        ResponseEntity<List<Client>> clientResponse = State.getRestTemplate().exchange
                (baseUrl + "clients", HttpMethod.GET, null, new ParameterizedTypeReference<List<Client>>() {
                });
        List<Client> restClients = clientResponse.getBody();
        if (restClients == null) {
            return null;
        }
        return new ArrayList<>(restClients);
    }

    @Override
    public void setClients(Collection<Client> clients) {

    }

    @Override
    public void addClient(Client client) {
        State.getRestTemplate().postForObject(baseUrl + "clients", new HttpEntity<>(client), Client.class);
    }

    @Override
    public void removeClient(Client client) {

    }

    @Override
    public void applyChangesTo(Client client) {

    }

    @Override
    public Client getClientByID(int id) {

        ResponseEntity<Client> responseEntity = State.getRestTemplate()
                .exchange("http://localhost:8080/clients/{id}", HttpMethod.GET, null, Client.class, id);
        State.setClientEtag(responseEntity.getHeaders().getETag());
        return responseEntity.getBody();
    }

    @Override
    public boolean collisionExists(String firstName, String lastName, LocalDate dateOfBirth) {
        return false;
    }

    @Override
    public int nextUid() {
        return 0;
    }

    @Override
    public Collection<TransplantRequest> getAllTransplantRequests() {
        return null;
    }

    @Override
    public Collection<TransplantRequest> getAllCurrentTransplantRequests() {
        return null;
    }
}
