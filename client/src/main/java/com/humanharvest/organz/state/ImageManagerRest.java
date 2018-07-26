package com.humanharvest.organz.state;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Deals with all image handling in with the server for the client.
 */
public class ImageManagerRest implements ImageManager{

    protected ImageManagerRest() {
    }


    private HttpHeaders generateHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Auth-Token", State.getToken());
        httpHeaders.setIfMatch(State.getClientEtag());
        return httpHeaders;
    }

    /**
     * Retrieves the image of the clients profile
     * @param uid id of the client
     * @return a byte array of the clients image
     */
    public byte[] getClientImage(int uid) {
        HttpHeaders httpHeaders = generateHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity responseEntity;
        responseEntity = State.getRestTemplate()
                .exchange(State.BASE_URI + "/clients/{uid}/image", HttpMethod.GET,
                        entity, byte[].class, uid);

        return (byte[]) responseEntity.getBody();
    }

    public byte[] getDefaultImage() throws IOException {
        InputStream in = new FileInputStream("./images/default.png");
        byte[] res = IOUtils.toByteArray(in);
        in.close();
        return res;
    }

    /**
     * Posts an image to the clients profile to replace their existing one (which may be the default one)
     * @param uid id of the client
     * @param image image the client is posting to the server
     * @return true if the image is successfully posted. false otherwise.
     */
    public boolean postClientImage(int uid, byte[] image) {
        HttpHeaders httpHeaders = generateHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(image, httpHeaders);

        ResponseEntity responseEntity = State.getRestTemplate().exchange(State.BASE_URI + "/clients/{uid}/image",
                HttpMethod.POST, entity, boolean.class, uid);
        return responseEntity.getStatusCode() == HttpStatus.CREATED;
    }

    /**
     * Deletes the image of a client so that it is set back to the default image.
     * @param uid id of the client
     * @return true if the image is successfully deleted.
     */
    public boolean deleteClientImage(int uid) {
        System.out.println("here");
        HttpHeaders httpHeaders = generateHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity responseEntity = State.getRestTemplate().exchange(State.BASE_URI + "clients/{uid}/image",
                HttpMethod.DELETE, entity, Object.class, uid);

        return responseEntity.getStatusCode() == HttpStatus.CREATED;
    }

}
