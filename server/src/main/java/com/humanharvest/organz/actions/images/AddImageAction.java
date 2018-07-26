package com.humanharvest.organz.actions.images;

import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;

public class AddImageAction extends Action {

    private Client client;
    private byte[] image;

    public AddImageAction(Client client, byte[] image) {
        this.client = client;
        this.image = image;
    }

    @Override
    protected void execute() throws ImagingOpException {
        String imagesDirectory = System.getProperty("user.home") + "/.organz/images/";

        // Create the directory if it doesn't exist
        File directory = new File(imagesDirectory);
        if (!directory.exists()) {
            new File(System.getProperty("user.home") + "/.organz/").mkdir();
            directory.mkdir();
        }

        // Write the file
        try (OutputStream out = new FileOutputStream(imagesDirectory + client.getUid() + ".png")) {
            out.write(image);
        } catch (IOException e) {
            throw new ImagingOpException(e.getMessage());
        }
    }

    @Override
    protected void unExecute() {
        String imagesDirectory = System.getProperty("user.home") + "/.organz/images/";

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
