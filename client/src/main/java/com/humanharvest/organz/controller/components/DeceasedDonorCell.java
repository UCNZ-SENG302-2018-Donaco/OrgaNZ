package com.humanharvest.organz.controller.components;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.client.DeceasedDonorDashboardOverviewController;
import com.humanharvest.organz.utilities.view.Page;

public class DeceasedDonorCell extends ListCell<Client> {

    private static final Logger LOGGER = Logger.getLogger(DeceasedDonorCell.class.getName());

    public DeceasedDonorCell() {
        setPrefWidth(0);
    }

    @Override
    protected void updateItem(Client donor, boolean empty) {
        super.updateItem(donor, empty);

        if (empty || donor == null) {
            setText(null);
            setStyle(null);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(DeceasedDonorCell.class.getResource(Page.DECEASED_DONOR_DASHBOARD_OVERVIEW.getPath()));
            Node node = loader.load();

            DeceasedDonorDashboardOverviewController controller = loader.getController();
            controller.setup(donor);
            setGraphic(node);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }

}
