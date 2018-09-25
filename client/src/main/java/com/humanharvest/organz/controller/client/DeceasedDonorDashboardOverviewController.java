package com.humanharvest.organz.controller.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.DonatedOrgan.OrganState;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.PageNavigator;

import org.apache.commons.io.IOUtils;

public class DeceasedDonorDashboardOverviewController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(DeceasedDonorDashboardOverviewController.class.getName());

    @FXML
    private ImageView profilePicture, spiderWeb;

    @FXML
    private Label nameLabel, organsLabel, deathLabel;

    Client donor;

    public void setup(Client donor) {
        this.donor = donor;

        Image profileImage = getProfilePicture(donor);
        if (profilePicture != null) {
            profilePicture.setImage(profileImage);
        }

        nameLabel.setText(donor.getFullName());

        Collection<DonatedOrgan> availableOrgans = State.getClientResolver().getDonatedOrgans(donor).stream()
                .filter(organ -> organ.getState() == OrganState.CURRENT || organ.getState() == OrganState.NO_EXPIRY)
                .collect(Collectors.toSet());
        organsLabel.setText(String.valueOf(availableOrgans.size()) + " organs available");

        deathLabel.setText(donor.getDateOfDeath().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));

        try (InputStream in = getClass().getResourceAsStream("/images/pages/spiderweb.png")) {
            byte[] spiderWebImageBytes = IOUtils.toByteArray(in);
            spiderWeb.setImage(new Image(new ByteArrayInputStream(spiderWebImageBytes)));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IO Exception when loading image ", e);
        }

    }
    static final Text helper;
    static final double DEFAULT_WRAPPING_WIDTH;
    static final double DEFAULT_LINE_SPACING;
    static final String DEFAULT_TEXT;
    static final TextBoundsType DEFAULT_BOUNDS_TYPE;
    static {
        helper = new Text();
        DEFAULT_WRAPPING_WIDTH = helper.getWrappingWidth();
        DEFAULT_LINE_SPACING = helper.getLineSpacing();
        DEFAULT_TEXT = helper.getText();
        DEFAULT_BOUNDS_TYPE = helper.getBoundsType();
    }

    public static double computeTextWidth(Font font, String text, double help0) {
        // Toolkit.getToolkit().getFontLoader().computeStringWidth(field.getText(),
        // field.getFont());

        helper.setText(text);
        helper.setFont(font);

        helper.setWrappingWidth(0.0D);
        helper.setLineSpacing(0.0D);
        double d = Math.min(helper.prefWidth(-1.0D), help0);
        helper.setWrappingWidth((int) Math.ceil(d));
        d = Math.ceil(helper.getLayoutBounds().getWidth());

        helper.setWrappingWidth(DEFAULT_WRAPPING_WIDTH);
        helper.setLineSpacing(DEFAULT_LINE_SPACING);
        helper.setText(DEFAULT_TEXT);
        return d;
    }

    private Image getProfilePicture(Client client) {
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

        return new Image(new ByteArrayInputStream(bytes));
    }

}
