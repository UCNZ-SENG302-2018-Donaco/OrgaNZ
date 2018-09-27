package com.humanharvest.organz.controller.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.DonatedOrgan.OrganState;
import com.humanharvest.organz.controller.ProjectionHelper;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.controller.spiderweb.SpiderWebController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.DurationFormatter;
import com.humanharvest.organz.utilities.DurationFormatter.DurationFormat;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.PageNavigator;

import org.apache.commons.io.IOUtils;

public class DeceasedDonorDashboardOverviewController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(DeceasedDonorDashboardOverviewController.class.getName());

    @FXML
    private ImageView profilePicture, spiderWeb;

    @FXML
    private Label nameLabel, deathLabel, organCount;

    private Client donor;

    public void setup(Client donor, Map<Client, Image> profilePictureStore) {
        this.donor = donor;

        Image profileImage = getProfilePicture(donor, profilePictureStore);
        if (profilePicture != null) {
            profilePicture.setImage(profileImage);
        }

        nameLabel.setText(donor.getFullName());

        Collection<DonatedOrgan> availableOrgans = State.getClientResolver().getDonatedOrgans(donor).stream()
                .filter(organ -> organ.getState() == OrganState.CURRENT || organ.getState() == OrganState.NO_EXPIRY)
                .collect(Collectors.toSet());

        if (availableOrgans.size() == 1) {
            organCount.setText("1 organ available ");
        } else {
            organCount.setText(String.valueOf(availableOrgans.size()) + " organs available");
        }
        Duration daysSinceDeath = Duration.between(LocalDateTime.now(), donor.getDatetimeOfDeath());
        deathLabel.setText("Died " + DurationFormatter.getFormattedDuration(daysSinceDeath, DurationFormat.DAYS));

        try (InputStream in = getClass().getResourceAsStream("/images/pages/spiderweb.png")) {
            byte[] spiderWebImageBytes = IOUtils.toByteArray(in);
            spiderWeb.setImage(new Image(new ByteArrayInputStream(spiderWebImageBytes)));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IO Exception when loading image ", e);
        }
    }

    @Override
    public void refresh() {
        // Currently does nothing
    }

    @FXML
    private void openSpiderWeb() {
        ProjectionHelper.stageClosing();
        new SpiderWebController(donor);
    }

    private Image getProfilePicture(Client client, Map<Client, Image> profilePictureStore) {

        // Try and get it from the store
        Image profilePicture = profilePictureStore.get(client);

        if (profilePicture == null) {

            byte[] bytes;
            try {
                bytes = State.getImageManager().getClientImage(client.getUid());
            } catch (NotFoundException ignored) {
                try {
                    bytes = State.getImageManager().getDefaultImage();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "IO Exception when loading image ", e);
                    return null;
                }
            } catch (ServerRestException e) {
                PageNavigator
                        .showAlert(AlertType.ERROR, "Server Error", "Something went wrong with the server. "
                                + "Please try again later.", mainController.getStage());
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                return null;
            }

            profilePicture = new Image(new ByteArrayInputStream(bytes));

            // Save it in the store
            profilePictureStore.put(client, profilePicture);

        }
        return profilePicture;
    }

}
