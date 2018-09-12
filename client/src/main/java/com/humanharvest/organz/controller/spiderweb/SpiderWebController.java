package com.humanharvest.organz.controller.spiderweb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.touch.MultitouchHandler;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.PageNavigatorTouch;

import org.tuiofx.internal.base.TuioFXCanvas;

/**
 * The Spider web controller which handles everything to do with displaying panes in the spider web stage.
 */
public class SpiderWebController {

    private static final Logger LOGGER = Logger.getLogger(SpiderWebController.class.getName());

    private final Client client;
    private final List<Pane> paneCollection;

    public SpiderWebController(Client client) {
        this.client = client;
        paneCollection = new ArrayList<>();
        setupNewStage();
        displayDonatingClient();
        displayOrgans();
    }

    /**
     * Create a new stage to display all of the pane in.
     */
    private void setupNewStage() {
        Stage stage = new Stage();
        stage.setTitle("Organ Spider Web");
        Pane root = new TuioFXCanvas();
        Scene scene = new Scene(root);

        FXMLLoader loader = new FXMLLoader();

        try {
            Pane backPane = loader.load(PageNavigatorTouch.class.getResourceAsStream(Page.BACKDROP.getPath()));

            root.getChildren().add(backPane);
            MultitouchHandler.initialise(root);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception when setting up stage", e);
        }

        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setOnCloseRequest(event -> {
            MultitouchHandler.stageClosing();
        });
        stage.show();
    }

    private void displayDonatingClient() {
//        MainController newMain = PageNavigator.openNewWindow(200, 400);
//        PageNavigator.loadPage(Page.RECEIVER_OVERVIEW, newMain);
//        paneCollection.add(newMain.getPane());
//
//        newMain.getPane().setTranslateX(500);
//        newMain.getPane().setTranslate(500);
    }

    /**
     * Loads a window for each non expired organ.
     */
    private void displayOrgans() {
        Collection<DonatedOrgan> donatedOrgans = State.getClientResolver().getDonatedOrgans(client);

        int x = 0;
        int y = 0;
        for (DonatedOrgan organ: donatedOrgans) {
            if (!organ.hasExpired()) {

                State.setOrganToDisplay(organ);
                MainController newMain = PageNavigator.openNewWindow(80, 80);
                PageNavigator.loadPage(Page.ORGAN_IMAGE, newMain);
                paneCollection.add(newMain.getPane());

                newMain.getPane().setTranslateX(x);
                newMain.getPane().setTranslateY(y);

                x += 100;
            }
        }
    }
}
