package com.humanharvest.organz.controller.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.utilities.DurationFormatter;
import com.humanharvest.organz.utilities.DurationFormatter.DurationFormat;
import com.humanharvest.organz.utilities.enums.Organ;

import org.apache.commons.io.IOUtils;

public class DonatedOrganOverviewController extends DashboardOverviewController {

    private static final Logger LOGGER = Logger.getLogger(DeceasedDonorDashboardOverviewController.class.getName());

    @FXML
    private ImageView organPicture, linkImage;
    @FXML
    private Label nameLabel, timeToExpiryLabel, donorNameLabel;

    private DonatedOrgan donatedOrgan;

    public void setup(DonatedOrgan organ, Map<Organ, Image> organPictureStore) {
        donatedOrgan = organ;
        nameLabel.setText(organ.getOrganType().toString());
        donorNameLabel.setText(donatedOrgan.getDonor().getFullName());
        updateTime();
        updateOrganImage(organ.getOrganType(), organPictureStore);
        setLinkImage(linkImage);
    }

    @Override
    public void refresh() {
        // Currently do nothing
    }

    @FXML
    private void goToLinkPage() {
        goToLinkPage(donatedOrgan.getDonor());
    }

    private void updateOrganImage(Organ organ, Map<Organ, Image> organPictureStore) {
        // See if it has already been retrieved in the store
        if (organPictureStore.containsKey(organ)) {
            organPicture.setImage(organPictureStore.get(organ));
        } else {
            Task<Image> task = new Task<Image>() {
                @Override
                protected Image call() throws IOException {
                    try (InputStream in = getClass().getResourceAsStream("/images/" + organ.toString() + ".png")) {
                        return new Image(new ByteArrayInputStream(IOUtils.toByteArray(in)));
                    }
                }
            };

            task.setOnSucceeded(event -> {
                Image organIcon = task.getValue();
                organPicture.setImage(organIcon);
                organPictureStore.put(organ, organIcon);
            });

            task.setOnFailed(event -> {
                try {
                    throw task.getException();
                } catch (IOException exc) {
                    LOGGER.log(Level.SEVERE, "IOException when loading default image.", exc);
                } catch (Throwable exc) {
                    LOGGER.log(Level.SEVERE, exc.getMessage(), exc);
                }
            });

            new Thread(task).start();
        }
    }

    public void updateTime() {
        Duration timeToExpiry = donatedOrgan.getDurationUntilExpiry();
        if (timeToExpiry == null) {
            timeToExpiryLabel.setText("Never expires");
        } else {
            timeToExpiryLabel.setText("Expires in " + DurationFormatter.getFormattedDuration(timeToExpiry,
                    DurationFormat.X_HRS_Y_MINS_SECS));
        }
    }
}
