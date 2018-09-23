package com.humanharvest.organz.controller.spiderweb;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.utilities.enums.Organ;

/**
 * A controller to display the image of an organ depending on its organ type.
 */
public class OrganImageController extends SubController {

    @FXML
    private ImageView organImage;
    private static final Logger LOGGER = Logger.getLogger(OrganImageController.class.getName());

    /**
     * Loads the organs icon based on what type of organ it is.
     */
    public void loadImage(Organ organ) {

        try (InputStream in = getClass().getResourceAsStream("/images/" + organ + ".png")) {
            Image image = new Image(in);
            organImage.setImage(image);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Organ image failed to load", ex);
        }
    }
}
