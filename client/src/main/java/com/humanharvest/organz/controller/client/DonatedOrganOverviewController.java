package com.humanharvest.organz.controller.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    @FXML
    private ImageView organPicture, spiderWeb;

    @FXML
    private Label nameLabel, timeToExpiryLabel, donorNameLabel;

    private Duration timeToExpiry;

    private DonatedOrgan donatedOrgan;

    public void setup(DonatedOrgan organ, Map<Organ, Image> organPictureStore) {
        donatedOrgan = organ;

        Image organImage = getOrganImage(organ.getOrganType(), organPictureStore);
        if (organPicture != null) {
            organPicture.setImage(organImage);
        }

        nameLabel.setText(organ.getOrganType().toString());

        timeToExpiry = donatedOrgan.getDurationUntilExpiry();

        updateTime();

        donorNameLabel.setText(donatedOrgan.getDonor().getFullName());
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
        // Currently do nothing
    }

    @FXML
    private void openSpiderWebOrDonateOrgansPage() {
        if(State.getUiType() == UiType.TOUCH) {
            new SpiderWebController(donatedOrgan.getDonor());
        } else { //standard
            MainController newMain = PageNavigator.openNewWindow(mainController);
            newMain.setWindowContext(new WindowContext.WindowContextBuilder()
                    .setAsClinicianViewClientWindow()
                    .viewClient(donatedOrgan.getDonor()).build());
            PageNavigator.loadPage(Page.REGISTER_ORGAN_DONATIONS, newMain);
        }

    }

    private Image getOrganImage(Organ organ, Map<Organ, Image> organPictureStore) {

        // Get it from the store
        return organPictureStore.get(organ);
    }

    public void updateTime() {

        timeToExpiry = donatedOrgan.getDurationUntilExpiry();
        timeToExpiryLabel.setText("Expires in " + DurationFormatter.getFormattedDuration(timeToExpiry,
                DurationFormat.X_HRS_Y_MINS_SECS));

    }

}
