package com.humanharvest.organz.utilities.resolvers.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.ModifyClientObject;
import com.humanharvest.organz.state.State;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ModifyClientDetailsResolver {

    private static final String baseUrl = "http://localhost:8080/";

    private Client client;
    private ModifyClientObject modifyClientObject;

    public ModifyClientDetailsResolver(Client client, ModifyClientObject modifyClientObject) {
        this.client = client;
        this.modifyClientObject = modifyClientObject;
    }

    public void execute() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setIfMatch(State.getClientEtag());
        //httpHeaders.add("If-Match", State.getClientEtag());
        System.out.println(State.getClientEtag());

        HttpEntity<ModifyClientObject> entity = new HttpEntity<>(modifyClientObject, httpHeaders);

        ResponseEntity<Client> responseEntity = State.getRestTemplate()
                .exchange(
                        baseUrl + "clients/{uid}",
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
