package com.humanharvest.organz.controller.components;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.client.ReceiverOverviewController;
import com.humanharvest.organz.utilities.view.Page;

public class PotentialRecipientCell extends ListCell<Client> {

    private static final Logger LOGGER = Logger.getLogger(PotentialRecipientCell.class.getName());

    public PotentialRecipientCell() {
    }

    @Override
    protected void updateItem(Client item, boolean empty) {
        super.updateItem(item, empty);

        if (!empty && item != null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        PotentialRecipientCell.class.getResource(Page.RECEIVER_OVERVIEW.getPath()));
                Node node = loader.load();
                ReceiverOverviewController controller = loader.getController();
                controller.setup(item);
                setGraphic(node);

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

}
