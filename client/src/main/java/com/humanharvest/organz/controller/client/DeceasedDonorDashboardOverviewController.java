package com.humanharvest.organz.controller.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.Notifications;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.utilities.DurationFormatter;
import com.humanharvest.organz.utilities.DurationFormatter.DurationFormat;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;

public class DeceasedDonorDashboardOverviewController extends DashboardOverviewController {

    private static final Logger LOGGER = Logger.getLogger(DeceasedDonorDashboardOverviewController.class.getName());

    @FXML
    private ImageView profilePictureView, linkImage;
    @FXML
    private Label nameLabel, deathLabel, organCount;

    private Client donor;

    public void setup(Client donor, Map<Client, Image> profilePictureStore) {
        this.donor = donor;
        updateProfilePicture(donor, profilePictureStore);
        nameLabel.setText(donor.getFullName());
        updateOrgansToDonateCount();
        Duration daysSinceDeath = Duration.between(LocalDateTime.now(), donor.getDatetimeOfDeath());
        deathLabel.setText("Died " + DurationFormatter.getFormattedDuration(daysSinceDeath, DurationFormat.DAYS));
        setLinkImage(linkImage);
    }

    @Override
    public void refresh() {
        // Currently does nothing
    }

    @FXML
    private void goToLinkPage() {
        goToLinkPage(donor);
    }

    private void updateOrgansToDonateCount() {
        Task<Collection<DonatedOrgan>> task = new Task<Collection<DonatedOrgan>>() {
            @Override
            protected Collection<DonatedOrgan> call() throws ServerRestException {
                return com.humanharvest.organz.state.State.getClientResolver().getDonatedOrgans(donor).stream()
                        .filter(DonatedOrgan::isAvailable)
                        .collect(Collectors.toSet());
            }
        };

        task.setOnSucceeded(success -> {
            Collection<DonatedOrgan> availableOrgans = task.getValue();
            if (availableOrgans.size() == 1) {
                organCount.setText("1 organ available");
            } else {
                organCount.setText(String.format("%s organs available", availableOrgans.size()));
            }
        });

        task.setOnFailed(fail -> {
            try {
                throw task.getException();
            } catch (ServerRestException exc) {
                LOGGER.log(Level.SEVERE, "", exc);
                Notifications.create()
                        .title("Server Error")
                        .text("A client's organ count could not be retrieved from the server.")
                        .showError();
            } catch (Throwable exc) {
                LOGGER.log(Level.SEVERE, exc.getMessage(), exc);
            }
        });

        new Thread(task).start();
    }

    private void updateProfilePicture(Client client, Map<Client, Image> profilePictureStore) {
        // Try and get it from the store
        if (profilePictureStore.containsKey(client)) {
            profilePictureView.setImage(profilePictureStore.get(client));
        } else {
            // Retrieve the picture from the server in a new thread
            Task<Image> task = new Task<Image>() {
                @Override
                protected Image call() throws ServerRestException, IOException {
                    try {
                        return new Image(new ByteArrayInputStream(
                                com.humanharvest.organz.state.State.getImageManager().getClientImage(client.getUid())));
                    } catch (NotFoundException exc) {
                        return new Image(new ByteArrayInputStream(
                                com.humanharvest.organz.state.State.getImageManager().getDefaultImage()));
                    }
                }
            };

            task.setOnSucceeded(event -> {
                Image profilePicture = task.getValue();
                profilePictureView.setImage(profilePicture);
                // Save it in the cache for future use without needing to retrieve it again
                profilePictureStore.put(client, profilePicture);
            });

            task.setOnFailed(event -> {
                try {
                    throw task.getException();
                } catch (IOException exc) {
                    LOGGER.log(Level.SEVERE, "IOException when loading default image.", exc);
                } catch (ServerRestException exc) {
                    LOGGER.log(Level.SEVERE, "", exc);
                    Notifications.create()
                            .title("Server Error")
                            .text("A client's profile picture could not be retrieved from the server.")
                            .showError();
                } catch (Throwable exc) {
                    LOGGER.log(Level.SEVERE, exc.getMessage(), exc);
                }
            });

            new Thread(task).start();
        }
    }
}
