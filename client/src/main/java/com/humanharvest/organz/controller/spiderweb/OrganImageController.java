package com.humanharvest.organz.controller.spiderweb;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.controller.client.ViewClientController;
import com.humanharvest.organz.state.State;

import org.apache.commons.io.IOUtils;

/**
 * A controller to display the image of an organ depending on its organ type.
 */
public class OrganImageController extends SubController {

    @FXML
    private ImageView organImage;
    private static final Logger LOGGER = Logger.getLogger(ViewClientController.class.getName());
    private DonatedOrgan donatedOrgan;

    @FXML
    private void initialize() {
        donatedOrgan = State.getOrganToDisplay();
        loadImage();
    }

    /**
     * Loads the organs icon based on what type of organ it is.
     */
    private void loadImage() {

        byte[] bytes;

        try (InputStream in = getClass().getResourceAsStream("/images/" + donatedOrgan.getOrganType().toString() + ".png")) {
            bytes = IOUtils.toByteArray(in);

            Image image = new Image(new ByteArrayInputStream(bytes));
            organImage.setImage(image);
        }

        catch (Exception ex) {
            System.out.println(ex.getMessage());
            LOGGER.log(Level.SEVERE, "Organ image failed to load");
        }

    }

}
