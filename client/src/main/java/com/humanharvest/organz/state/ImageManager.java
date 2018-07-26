package com.humanharvest.organz.state;

import java.io.IOException;

public interface ImageManager {

    /**
     * Retrieves the image of the clients profile
     * @param uid id of the client
     * @return a byte array of the clients image
     */
    byte[] getClientImage(int uid);

    /**
     * Retrive the default image for the clients profile
     * @return a byte array of the default image
     * @throws IOException if there is an error loading default image
     */
    byte[] getDefaultImage() throws IOException;

    /**
     * Posts an image to the clients profile to replace their existing one (which may be the default one)
     * @param uid id of the client
     * @param image image the client is posting to the server
     * @return true if the image is successfully posted. false otherwise.
     */
    boolean postClientImage(int uid, byte[] image);

    /**
     * Deletes the image of a client so that it is set back to the default image.
     * @param uid id of the client
     * @return true if the image is successfully deleted.
     */
    boolean deleteClientImage(int uid);

}


