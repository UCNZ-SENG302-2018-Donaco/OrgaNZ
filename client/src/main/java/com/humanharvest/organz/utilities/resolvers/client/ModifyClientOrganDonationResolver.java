package com.humanharvest.organz.utilities.resolvers.client;

import java.util.HashMap;
import java.util.Map;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.ModifyClientObject;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.exceptions.OrganAlreadyRegisteredException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ModifyClientOrganDonationResolver {

    private static final String baseUrl = "http://localhost:8080/";

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

    public void execute() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setIfMatch(State.getClientEtag());
        //httpHeaders.add("If-Match", State.getClientEtag());

        HttpEntity<Map<Organ, Boolean>> entity = new HttpEntity<>(changes, httpHeaders);

        ResponseEntity<Client> responseEntity = State.getRestTemplate()
                .exchange(
                        baseUrl + "clients/{uid}/donationStatus",
                        HttpMethod.PATCH,
                        entity,
                        Client.class,
                        client.getUid());

        State.setClientEtag(responseEntity.getHeaders().getETag());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            System.err.println(responseEntity.toString());
        }
    }

}
