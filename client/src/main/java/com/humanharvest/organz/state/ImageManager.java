package com.humanharvest.organz.state;

import org.springframework.http.*;

/**
 * Deals with all image handling in with the server for the client.
 */
public class ImageManager {

    protected ImageManager() {}

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Auth-Token", State.getToken());
        return httpHeaders;
    }

    /**
     * Retrieves the image of the clients profile
     * @param uid id of the client
     * @return a byte array of the clients image
     */
    public byte[] getClientImage(int uid) {
        HttpHeaders httpHeaders = getHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity responseEntity = State.getRestTemplate().exchange(State.BASE_URI + "/clients/{uid}/image", HttpMethod.GET,
                entity, byte[].class, uid);
        return (byte[]) responseEntity.getBody();
    }

    /**
     * Posts an image to the clients profile to replace their existing one (which may be the default one)
     * @param uid id of the client
     * @param image image the client is posting to the server
     * @return true if the image is successfully posted. false otherwise.
     */
    public boolean postClientImage(int uid, byte[] image) {
        HttpHeaders httpHeaders = getHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(image, httpHeaders);

        ResponseEntity responseEntity = State.getRestTemplate().exchange(State.BASE_URI + "/clients/{uid}/image",
                HttpMethod.POST, entity, boolean.class, uid);
        return responseEntity.getStatusCode() == HttpStatus.OK;

    }

    public boolean deleteClientImage(int uid) {
        HttpHeaders httpHeaders = getHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity responseEntity = State.getRestTemplate().exchange(State.BASE_URI + "clients/{uid}/image",
                HttpMethod.DELETE, entity, Object.class, uid);

        return responseEntity.getStatusCode() == HttpStatus.OK;
    }

}
