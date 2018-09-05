package com.humanharvest.organz.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.controller.client.ViewClientController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;

public class OrganContainer {

    @FXML
    private ImageView organImage;
    private static final Logger LOGGER = Logger.getLogger(ViewClientController.class.getName());
    private DonatedOrgan donatedOrgan;

    public OrganContainer(DonatedOrgan donatedOrgan) {
        this.donatedOrgan = donatedOrgan;
        MainController newMain = PageNavigator.openNewWindow();
        PageNavigator.loadPage(Page.ORGAN_CONTAINER, newMain);
        System.out.println(donatedOrgan.getOrganType() + " has been loaded");
        loadImage();
    }

    private void loadImage() {
        byte[] bytes;
        switch (donatedOrgan.getOrganType()) {
            case BONE:
                //set bone image...
            case HEART:
                //set heart image...
            default:
                // set default image
                try {
                    bytes = State.getImageManager().getDefaultImage();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "IO Exception when loading image ", e);
                    return;
                }

        }
        Image image = new Image(new ByteArrayInputStream(bytes));
        organImage.setImage(image);

    }

}
