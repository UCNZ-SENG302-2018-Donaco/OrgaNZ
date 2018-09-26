package com.humanharvest.organz.controller.components;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.controller.client.DonatedOrganOverviewController;
import com.humanharvest.organz.utilities.view.Page;

public class DonatedOrganCell extends ListCell<DonatedOrgan> {

    private static final Logger LOGGER = Logger.getLogger(DeceasedDonorCell.class.getName());

    private DonatedOrgan donatedOrgan;
    private DonatedOrganOverviewController controller;

    public DonatedOrganCell() {
        setPrefWidth(0);
    }

    @Override
    protected void updateItem(DonatedOrgan donatedOrgan, boolean empty) {
        super.updateItem(donatedOrgan, empty);

        if (empty || donatedOrgan == null) {
            setText(null);
            setStyle(null);
            return;
        }

        if (donatedOrgan == this.donatedOrgan) {
            // the same organ, we just need to refresh the time
            controller.updateTime();
        } else {

            try {
                FXMLLoader loader =
                        new FXMLLoader(DeceasedDonorCell.class.getResource(Page.DONATED_ORGAN_OVERVIEW.getPath()));
                Node node = loader.load();

                controller = loader.getController();
                controller.setup(donatedOrgan);
                setGraphic(node);

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }

    }

}
