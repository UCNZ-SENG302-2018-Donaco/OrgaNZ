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
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.controller.spiderweb.SpiderWebController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.state.State.UiType;
import com.humanharvest.organz.utilities.DurationFormatter;
import com.humanharvest.organz.utilities.DurationFormatter.DurationFormat;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext;

import org.apache.commons.io.IOUtils;

public class DonatedOrganOverviewController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(DeceasedDonorDashboardOverviewController.class.getName());
    private static final Image spiderWebImage = getPageImage("spiderweb");
    private static final Image donateOrgansImage = getPageImage("donate_organs");

    @FXML
    private ImageView organPicture, linkImage;
    @FXML
    private Label nameLabel, timeToExpiryLabel, donorNameLabel;

    private DonatedOrgan donatedOrgan;

    private static Image getPageImage(String page) {
        try (InputStream in = DonatedOrganOverviewController.class
                .getResourceAsStream(String.format("/images/pages/%s.png", page))) {
            byte[] spiderWebImageBytes = IOUtils.toByteArray(in);
            return new Image(new ByteArrayInputStream(spiderWebImageBytes));
        } catch (IOException exc) {
            LOGGER.log(Level.SEVERE, "IOException when loading page image ", exc);
            return null;
        }
    }

    public void setup(DonatedOrgan organ, Map<Organ, Image> organPictureStore) {
        donatedOrgan = organ;
        nameLabel.setText(organ.getOrganType().toString());
        donorNameLabel.setText(donatedOrgan.getDonor().getFullName());
        updateTime();
        updateOrganImage(organ.getOrganType(), organPictureStore);

        switch (State.getUiType()) {
            case TOUCH:
                linkImage.setImage(spiderWebImage);
                break;
            case STANDARD:
                linkImage.setImage(donateOrgansImage);
                break;
        }
    }

    @Override
    public void refresh() {
        // Currently do nothing
    }

    @FXML
    private void goToLinkPage() {
        if (State.getUiType() == UiType.TOUCH) {
            new SpiderWebController(donatedOrgan.getDonor());
        } else { //standard
            MainController newMain = PageNavigator.openNewWindow();
            newMain.setWindowContext(new WindowContext.WindowContextBuilder()
                    .setAsClinicianViewClientWindow()
                    .viewClient(donatedOrgan.getDonor()).build());
            PageNavigator.loadPage(Page.REGISTER_ORGAN_DONATIONS, newMain);
        }

    }

    private void updateOrganImage(Organ organ, Map<Organ, Image> organPictureStore) {
        // See if it has already been retrieved in the store
        if (organPictureStore.containsKey(organ)) {
            organPicture.setImage(organPictureStore.get(organ));
        } else {
            Task<byte[]> task = new Task<byte[]>() {
                @Override
                protected byte[] call() throws IOException {
                    try (InputStream in = getClass().getResourceAsStream("/images/" + organ.toString() + ".png")) {
                        return IOUtils.toByteArray(in);
                    }
                }
            };

            task.setOnSucceeded(event -> {
                Image organIcon = new Image(new ByteArrayInputStream(task.getValue()));
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
        timeToExpiryLabel.setText("Expires in " + DurationFormatter.getFormattedDuration(timeToExpiry,
                DurationFormat.X_HRS_Y_MINS_SECS));
    }
}
