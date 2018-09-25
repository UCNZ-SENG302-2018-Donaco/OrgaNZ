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

    public DonatedOrganCell() {
        setPrefWidth(0);
    }

    @Override
    protected void updateItem(DonatedOrgan organ, boolean empty) {
        super.updateItem(organ, empty);

        if (empty || organ == null) {
            setText(null);
            setStyle(null);
            return;
        }

        try {
            FXMLLoader loader =
                    new FXMLLoader(DeceasedDonorCell.class.getResource(Page.DONATED_ORGAN_OVERVIEW.getPath()));
            Node node = loader.load();

            DonatedOrganOverviewController controller = loader.getController();
            controller.setup(organ);
            setGraphic(node);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }

}
