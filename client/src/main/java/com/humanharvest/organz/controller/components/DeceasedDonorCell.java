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
import com.humanharvest.organz.controller.client.DeceasedDonorDashboardOverviewController;
import com.humanharvest.organz.utilities.view.Page;

public class DeceasedDonorCell extends ListCell<Client> {

    private static final Logger LOGGER = Logger.getLogger(DeceasedDonorCell.class.getName());

    private Client donor;
    private Map<Client, Image> profilePictureStore;

    public DeceasedDonorCell(Map<Client, Image> profilePictureStore) {
        setPrefWidth(0);
        this.profilePictureStore = profilePictureStore;
    }

    @Override
    protected void updateItem(Client donor, boolean empty) {
        super.updateItem(donor, empty);
//        System.out.println(String.format("Old value: %s, New value: %s, Empty: %s",
//                this.donor != null ? this.donor.getFullName() : null,
//                donor != null ? donor.getFullName() : null,
//                empty));

        if (empty || donor == null) {
            setText(null);
            setStyle(null);
            return;
        }

        if (donor != this.donor || donor.getOrganDonationStatus() != this.donor.getOrganDonationStatus()) {
            this.donor = donor;
            try {
                FXMLLoader loader = new FXMLLoader(
                        DeceasedDonorCell.class.getResource(Page.DECEASED_DONOR_DASHBOARD_OVERVIEW.getPath()));
                Node node = loader.load();

                DeceasedDonorDashboardOverviewController controller = loader.getController();
                controller.setup(donor, profilePictureStore);
                setGraphic(node);

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

}
