package com.humanharvest.organz.state;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;

/**
 * Deals with all image handling in with the server for the client.
 */
public class ImageManager {

    protected ImageManager() {}

    private HttpHeaders getHeaders() {
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
        HttpHeaders httpHeaders = getHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity responseEntity;
        try {
            System.out.println("resources");
            responseEntity = State.getRestTemplate()
                    .exchange(State.BASE_URI + "/clients/{uid}/image", HttpMethod.GET,
                            entity, byte[].class, uid);
        } catch (ResourceAccessException ex) {
//            ex.printStackTrace();
            throw ex;
        } catch (Exception ex) {
            System.out.println("resources");
            ex.printStackTrace();
            throw ex;
        }

        return (byte[]) responseEntity.getBody();
    }

    public byte[] getDefaultImage() throws IOException {
        try {
            InputStream in = new FileInputStream("./images/default.png");
            return IOUtils.toByteArray(in);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw ex;
        }
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

    /**
     * Deletes the image of a client so that it is set back to the default image.
     * @param uid id of the client
     * @return true if the image is successfully deleted.
     */
    public boolean deleteClientImage(int uid) {
        HttpHeaders httpHeaders = getHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity responseEntity = State.getRestTemplate().exchange(State.BASE_URI + "clients/{uid}/image",
                HttpMethod.DELETE, entity, Object.class, uid);

        return responseEntity.getStatusCode() == HttpStatus.OK;
    }

}
