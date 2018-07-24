package com.humanharvest.organz.state;

import com.humanharvest.organz.Client;
import org.springframework.http.*;

public class ImageManager {

    protected ImageManager() {}

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Auth-Token", State.getToken());
        return httpHeaders;
    }

    public byte[] getClientImage(int uid) {
        HttpHeaders httpHeaders = getHeaders();
        HttpEntity<Client> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity responseEntity = State.getRestTemplate().exchange(State.BASE_URI + "/clients/{uid}/image", HttpMethod.GET,
                entity, byte[].class, uid);
        return (byte[]) responseEntity.getBody();
    }

    public boolean postClientImage(int uid, byte[] image) {
        HttpHeaders httpHeaders = getHeaders();
        HttpEntity<Client> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity responseEntity = State.getRestTemplate().exchange(State.BASE_URI + "/clients/{uid}/image",
                HttpMethod.POST, entity, boolean.class, uid);
        return responseEntity.getStatusCode() == HttpStatus.OK;

    }
}
