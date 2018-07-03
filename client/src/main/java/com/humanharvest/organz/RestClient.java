package com.humanharvest.organz;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class RestClient extends Client {

    private static final String baseUrl = "http://localhost:8080/";

    @Override
    public Map<Organ, Boolean> getOrganDonationStatus() {
        ResponseEntity<Map<Organ, Boolean>> responseEntity = State.getRestTemplate().exchange
                (baseUrl + "clients/{id}/donationStatus", HttpMethod.GET, null, new
                        ParameterizedTypeReference<Map<Organ, Boolean>>() {
                }, getUid());
        return responseEntity.getBody();
    }

}
