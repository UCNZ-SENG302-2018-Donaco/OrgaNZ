package com.humanharvest.organz.actions.images;

import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;

public class AddImageAction extends Action {

    private static final Logger LOGGER = Logger.getLogger(AddImageAction.class.getName());

    private Client client;
    private byte[] image;
    private String imagesDirectory;

    public AddImageAction(Client client, byte[] image, String imagesDirectory) {
        this.client = client;
        this.image = image;
        this.imagesDirectory = imagesDirectory;
    }

    @Override
    protected void execute() throws ImagingOpException {

        // Create the directory if it doesn't exist
        File directory = new File(imagesDirectory);
        if (!directory.exists()) {
            directory.mkdir();
        }

        // Write the file
        try (OutputStream out = new FileOutputStream(imagesDirectory + client.getUid() + ".png")) {
            out.write(image);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new ImagingOpException(e.getMessage());
        }
    }

    @Override
    protected void unExecute() {
        // Delete the file
        File file = new File(imagesDirectory + client.getUid() + ".png");
        if (!file.delete()) {
            throw new NotFoundException();
        }
    }

    @Override
    public String getExecuteText() {
        return String.format("Added image for Client: %s", client.getUid());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Removed image for Client: %s", client.getUid());
    }

    @Override
    public Object getModifiedObject() {
        return client;
    }
}
