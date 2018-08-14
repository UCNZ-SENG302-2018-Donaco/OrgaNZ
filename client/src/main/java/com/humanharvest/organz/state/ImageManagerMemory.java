package com.humanharvest.organz.state;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import org.apache.commons.io.IOUtils;

public class ImageManagerMemory implements ImageManager{

    private Map<Integer, byte[]> imageMap = new HashMap<>();

    public ImageManagerMemory() {
    }

    /**
     * Retrieves the image of the clients profile
     * @param uid id of the client
     * @return a byte array of the clients image
     */
    public byte[] getClientImage(int uid) {
        if (imageMap.get(uid) == null) {
            throw new NotFoundException();
        } else {
            return imageMap.get(uid);
        }
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
        if (State.getClientManager().getClientByID(uid).isPresent()) {
            imageMap.put(uid, image);
            return true;

        } else {
            throw new NotFoundException();
        }


    }

    /**
     * Deletes the image of a client so that it is set back to the default image.
     * @param uid id of the client
     * @return true if the image is successfully deleted.
     */
    public boolean deleteClientImage(int uid) {
        if (State.getClientManager().getClientByID(uid).isPresent()) {
            imageMap.remove(uid);
            return true;
        } else {
            throw new NotFoundException();
        }

    }

}
