package com.humanharvest.organz.controller.components;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.controller.client.DonatedOrganOverviewController;
import com.humanharvest.organz.state.State;
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
//        System.out.println("updating cell do");
        super.updateItem(donatedOrgan, empty);

        if (empty || donatedOrgan == null) {
            setText(null);
            setStyle(null);
            return;
        }

        Optional<Client> optionalDonor = State.getClientManager().getClientByID(donatedOrgan.getDonor().getUid());
        if (optionalDonor.isPresent()) {
            Client donor = optionalDonor.get();
            if (donatedOrgan == this.donatedOrgan && donor == this.donatedOrgan.getDonor()) {
                // the same organ, we just need to refresh the time
                controller.updateTime();
            } else {
//            System.out.println(String.format("Old value: %s, New value: %s, Empty: %s",
//                    this.donatedOrgan != null ? this.donatedOrgan.getDonor().getFullName() : null,
//                    donatedOrgan.getDonor().getFullName(),
//                    empty));
                this.donatedOrgan = donatedOrgan;
                this.donatedOrgan.setDonor(optionalDonor.get());

                try {
                    FXMLLoader loader =
                            new FXMLLoader(DonatedOrganCell.class.getResource(Page.DONATED_ORGAN_OVERVIEW.getPath()));
                    Node node = loader.load();

                    controller = loader.getController();
                    controller.setup(this.donatedOrgan, organImageMap);
                    setGraphic(node);

                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        } else {
            System.out.println("np");
            setGraphic(null);
        }
    }

}


