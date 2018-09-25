package com.humanharvest.organz.controller.spiderweb;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.controller.client.ViewClientController;
import com.humanharvest.organz.utilities.enums.Organ;

import org.apache.commons.io.IOUtils;

/**
 * A controller to display the image of an organ depending on its organ type.
 */
public class OrganImageController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(ViewClientController.class.getName());
    @FXML
    private ImageView organImage;

    /**
     * Loads the organs icon based on what type of organ it is.
     */
    public void loadImage(Organ organ) {

        byte[] bytes;

        try (InputStream in = getClass().getResourceAsStream("/images/" + organ.toString() + ".png")) {
            bytes = IOUtils.toByteArray(in);

            Image image = new Image(new ByteArrayInputStream(bytes));
            organImage.setImage(image);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Organ image failed to load");
        }

    }

}
