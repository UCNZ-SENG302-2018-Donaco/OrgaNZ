package com.humanharvest.organz.controller.spiderweb;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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

import org.apache.commons.io.IOUtils;
import org.tuiofx.internal.base.TuioFXCanvas;

/**
 * The Spider web controller which handles everything to do with displaying panes in the spider web stage.
 */
public class SpiderWebController {

    private Client client;
    private ArrayList<Pane> paneCollection;

    public SpiderWebController(Client client) {
        this.client = client;
        this.paneCollection = new ArrayList<>();
        setupNewStage();
        displayDonatingClient();
        displayOrgans();
    }

    /**
     * Create a new stage to display all of the pane in.
     */
    private void setupNewStage() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Organ Spider Web");
            Pane root = new TuioFXCanvas();
            Scene scene = new Scene(root);

            FXMLLoader loader = new FXMLLoader();
            Pane backPane = loader.load(PageNavigatorTouch.class.getResourceAsStream(Page.BACKDROP.getPath()));

            root.getChildren().add(backPane);
            MultitouchHandler.initialise(root);

            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setOnCloseRequest(event -> {
                MultitouchHandler.stageClosing();
            });
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private void displayDonatingClient() {
        // Display donating client page here. Waiting on C4.
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
