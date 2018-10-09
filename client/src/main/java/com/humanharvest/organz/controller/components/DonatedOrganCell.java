package com.humanharvest.organz.controller.components;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.controller.client.DonatedOrganOverviewController;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.view.Page;

public class DonatedOrganCell extends ListCell<DonatedOrgan> {

    private static final Logger LOGGER = Logger.getLogger(DonatedOrganCell.class.getName());

    private DonatedOrgan donatedOrgan;
    private Map<Organ, Image> organImageMap;
    private DonatedOrganOverviewController controller;

    public DonatedOrganCell(Map<Organ, Image> organImageMap) {
        setPrefWidth(0);
        this.organImageMap = organImageMap;
    }

    @Override
    protected void updateItem(DonatedOrgan donatedOrgan, boolean empty) {
        super.updateItem(donatedOrgan, empty);

        if (empty || donatedOrgan == null) {
            setText(null);
            setStyle(null);
            setGraphic(null);
            return;
        }

        Client donor = donatedOrgan.getDonor();
        if (donatedOrgan == this.donatedOrgan && donor == this.donatedOrgan.getDonor()) {
            // the same organ, we just need to refresh the time
            controller.updateTime();
        } else {
            this.donatedOrgan = donatedOrgan;
            this.donatedOrgan.setDonor(donor);

            try {
                FXMLLoader loader = new FXMLLoader(
                        DonatedOrganCell.class.getResource(Page.DONATED_ORGAN_OVERVIEW.getPath()));
                Node node = loader.load();

                controller = loader.getController();
                controller.setup(this.donatedOrgan, organImageMap);
                setGraphic(node);

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }
}
