package com.humanharvest.organz.controller.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.controller.spiderweb.SpiderWebController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.state.State.UiType;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext;

import org.apache.commons.io.IOUtils;

public abstract class DashboardOverviewController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(DashboardOverviewController.class.getName());

    private static final Image spiderWebImage = getPageImage("spiderweb");
    private static final Image donateOrgansImage = getPageImage("donate_organs");

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

    protected void setLinkImage(ImageView linkImageView) {
        switch (State.getUiType()) {
            case TOUCH:
                linkImageView.setImage(spiderWebImage);
                break;
            case STANDARD:
                linkImageView.setImage(donateOrgansImage);
                break;
        }
    }

    protected void goToLinkPage(Client client) {
        if (State.getUiType() == UiType.TOUCH) {
            new SpiderWebController(client);
        } else { //standard
            MainController newMain = PageNavigator.openNewWindow();
            newMain.setWindowContext(new WindowContext.WindowContextBuilder()
                    .setAsClinicianViewClientWindow()
                    .viewClient(client)
                    .build());
            PageNavigator.loadPage(Page.REGISTER_ORGAN_DONATIONS, newMain);
        }
    }

}
