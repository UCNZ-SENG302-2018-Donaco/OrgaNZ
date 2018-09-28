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

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.Notifications;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.DonatedOrgan.OrganState;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.ProjectionHelper;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.controller.spiderweb.SpiderWebController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.state.State.UiType;
import com.humanharvest.organz.utilities.DurationFormatter;
import com.humanharvest.organz.utilities.DurationFormatter.DurationFormat;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext;

import org.apache.commons.io.IOUtils;

public class DeceasedDonorDashboardOverviewController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(DeceasedDonorDashboardOverviewController.class.getName());

    @FXML
    private ImageView profilePictureView, spiderWeb;

    @FXML
    private Label nameLabel, deathLabel, organCount;

    private Client donor;

    public void setup(Client donor, Map<Client, Image> profilePictureStore) {
        this.donor = donor;

        updateProfilePicture(donor, profilePictureStore);

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

        String imageName;
        if(State.getUiType() == UiType.TOUCH) {
            imageName = "spiderweb";
        } else { //standard
            imageName = "donate_organs";
        }
        try (InputStream in = getClass().getResourceAsStream("/images/pages/" + imageName + ".png")) {
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
    private void openSpiderWebOrOrgansToDonatePage() {
        if(State.getUiType() == UiType.TOUCH) {
            ProjectionHelper.stageClosing();
            new SpiderWebController(donor);
        } else { //standard
            MainController newMain = PageNavigator.openNewWindow();
            newMain.setWindowContext(new WindowContext.WindowContextBuilder()
                    .setAsClinicianViewClientWindow()
                    .viewClient(donor).build());
            PageNavigator.loadPage(Page.REGISTER_ORGAN_DONATIONS, newMain);
        }
    }

    private void updateProfilePicture(Client client, Map<Client, Image> profilePictureStore) {
        // Try and get it from the store
        if (profilePictureStore.containsKey(client)) {
            profilePictureView.setImage(profilePictureStore.get(client));
        } else {
            // Retrieve the picture from the server in another thread
            Task<byte[]> task = new Task<byte[]>() {
                @Override
                protected byte[] call() throws ServerRestException, IOException {
                    try {
                        return com.humanharvest.organz.state.State.getImageManager().getClientImage(client.getUid());
                    } catch (NotFoundException exc) {
                        return com.humanharvest.organz.state.State.getImageManager().getDefaultImage();
                    }
                }
            };

            task.setOnSucceeded(event -> {
                Image profilePicture = new Image(new ByteArrayInputStream(task.getValue()));
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
