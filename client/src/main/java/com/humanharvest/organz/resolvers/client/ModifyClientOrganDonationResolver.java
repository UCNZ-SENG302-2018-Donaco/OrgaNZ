package com.humanharvest.organz.resolvers.client;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class ModifyClientOrganDonationResolver {

    private Client client;
    private Map<Organ, Boolean> changes;


    public ModifyClientOrganDonationResolver(Client client, Map<Organ, Boolean> changes) {
        this.client = client;
        this.changes = changes;
    }

    public ModifyClientOrganDonationResolver(Client client) {
        this.client = client;
        this.changes = new HashMap<>();
    }

    /**
     * Add a organ change to the client. Should check the value is not already set before adding the change
     * @param organ The organ to be updated
     * @param newValue The new value
     */
    public void addChange(Organ organ, Boolean newValue) {
        changes.put(organ, newValue);
    }

    public Map<Organ, Boolean> execute() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setIfMatch(State.getClientEtag());

        HttpEntity<Map<Organ, Boolean>> entity = new HttpEntity<>(changes, httpHeaders);

        ParameterizedTypeReference<Map<Organ, Boolean>> mapRef = new ParameterizedTypeReference<Map<Organ, Boolean>>
                () {};

        ResponseEntity<Map<Organ, Boolean>> responseEntity = State.getRestTemplate()
                .exchange(
                        State.BASE_URI + "clients/{uid}/donationStatus",
                        HttpMethod.PATCH,
                        entity,
                        mapRef,
                        client.getUid());


        State.setClientEtag(responseEntity.getHeaders().getETag());
        return responseEntity.getBody();
    }

    private String formatChange(Organ organ, boolean newValue) {
        if (newValue) {
            return String.format("Registered %s for donation.", organ.toString());
        } else {
            return String.format("Deregistered %s for donation.", organ.toString());
        }
    }

    public String toString() {
        String changesText = changes.entrySet().stream()
                .map(entry -> formatChange(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));

        return String.format("Changed organ donation registration for client %d: %s:\n\n%s",
                client.getUid(), client.getFullName(), changesText);
    }

}
