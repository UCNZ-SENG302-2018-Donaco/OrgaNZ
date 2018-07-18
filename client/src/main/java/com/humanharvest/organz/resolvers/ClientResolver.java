package com.humanharvest.organz.resolvers;

import java.util.List;
import java.util.Map;

import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class ClientResolver {

    private static final String baseUrl = "http://localhost:8080/";

    public static Map<Organ, Boolean> getOrganDonationStatus(int uid) {
        ResponseEntity<Map<Organ, Boolean>> responseEntity = State.getRestTemplate().exchange
                (baseUrl + "clients/{id}/donationStatus", HttpMethod.GET, null, new
                        ParameterizedTypeReference<Map<Organ, Boolean>>() {
                        }, uid);
        State.setClientEtag(responseEntity.getHeaders().getETag());
        State.getClientManager()
                .getClientByID(uid)
                .orElseThrow(IllegalArgumentException::new)
                .setOrganDonationStatus(responseEntity.getBody());
        return responseEntity.getBody();
    }

    public static List<TransplantRequest> getTransplantRequests(int uid) {
        ResponseEntity<List<TransplantRequest>> responseEntity = State.getRestTemplate().exchange
                (baseUrl + "clients/{id}/transplantRequests", HttpMethod.GET, null, new
                        ParameterizedTypeReference<List<TransplantRequest>>() {
                        }, uid);
        State.setClientEtag(responseEntity.getHeaders().getETag());
        State.getClientManager()
                .getClientByID(uid)
                .orElseThrow(IllegalArgumentException::new)
                .setTransplantRequests(responseEntity.getBody());
        return responseEntity.getBody();
    }
}
