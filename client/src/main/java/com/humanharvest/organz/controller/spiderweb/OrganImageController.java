package com.humanharvest.organz.controller.spiderweb;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.utilities.enums.Organ;

/**
 * A controller to display the image of an organ depending on its organ type.
 */
public class OrganImageController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(OrganImageController.class.getName());

    @FXML
    private ImageView organImage;

    @FXML
    private Label matchCount;

    @FXML
    private Circle countCircle;

    @FXML
    private void initialize() {
        countCircle.setVisible(false);
        matchCount.setVisible(false);
    }

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

    /**
     * Sets the match count and show the notification
     *
     * @param count The number of matches to show
     */
    public void setMatchCount(int count) {
        matchCount.setText(String.valueOf(count));
    }

    /**
     * Enable or disable the match count icon
     *
     * @param visible If the matches should be shown or hidden
     */
    public void matchCountIsVisible(boolean visible) {
        countCircle.setVisible(visible);
        matchCount.setVisible(visible);
    }
}
