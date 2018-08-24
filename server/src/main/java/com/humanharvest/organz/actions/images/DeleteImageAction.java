package com.humanharvest.organz.actions.images;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import org.apache.commons.io.IOUtils;

import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DeleteImageAction extends Action {

    private final String imagesDirectory;
    private Client client;
    private byte[] image;

    public DeleteImageAction(Client client, String imagesDirectory) throws IOException {
        this.client = client;
        this.imagesDirectory = imagesDirectory;

        // Load the file to allow it to be redone
        try (InputStream in = new FileInputStream(imagesDirectory + client.getUid() + ".png")) {
            image = IOUtils.toByteArray(in);
        }
    }

    @Override
    protected void execute() throws NotFoundException {
        // Delete the file
        File file = new File(imagesDirectory + client.getUid() + ".png");
        if (!file.delete()) {
            throw new NotFoundException();
        }
    }

    @Override
    protected void unExecute() {
        // Create the directory if it doesn't exist
        File directory = new File(imagesDirectory);
        if (!directory.exists()) {
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
    public String getExecuteText() {
        return String.format("Removed image for Client: %s", client.getUid());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Added image for Client: %s", client.getUid());
    }

    @Override
    public Object getModifiedObject() {
        return client;
    }
}
