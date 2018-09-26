package com.humanharvest.organz.controller.components;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.controller.client.ReceiverOverviewController;
import com.humanharvest.organz.utilities.view.Page;

public class PotentialRecipientCell extends ListCell<TransplantRequest> {

    private static final Logger LOGGER = Logger.getLogger(PotentialRecipientCell.class.getName());

    private final List<TransplantRequest> potentialRecipientList;
    private final Client donor;
    private TransplantRequest transplantRequest;
    private Timeline refresher;

    public PotentialRecipientCell(List<TransplantRequest> potentialRecipientList, Client donor, Timeline refresher) {
        this.potentialRecipientList = potentialRecipientList;
        this.donor = donor;
        this.refresher = refresher;
    }

    @Override
    protected void updateItem(TransplantRequest request, boolean empty) {
        super.updateItem(request, empty);
        transplantRequest = request;

        if (!empty && request != null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        PotentialRecipientCell.class.getResource(Page.RECEIVER_OVERVIEW.getPath()));
                Node recipientPane = loader.load();
                ReceiverOverviewController recipientOverview = loader.getController();
                recipientOverview.setup(request, donor, refresher);
                recipientOverview.setPriority(potentialRecipientList.indexOf(request) + 1);
                setGraphic(recipientPane);

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public TransplantRequest getTransplantRequest() {
        return transplantRequest;
    }
}
