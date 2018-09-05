package com.humanharvest.organz.controller;

import java.io.IOException;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.controller.client.ViewClientController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.touch.MultitouchHandler;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigatorStandard;
import com.humanharvest.organz.utilities.view.PageNavigatorTouch;

import org.tuiofx.internal.base.TuioFXCanvas;

/**
 * The Spider web controller which handles everything to do with displaying panes in the spider web stage.
 */
public class SpiderWebController extends SubController {

    private Client client;

    public SpiderWebController(Client client) {
        this.client = client;
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

    private void displayOrgans() {
        // wrap organs in container to display on screen

        for (Organ organ: client.getCurrentlyDonatedOrgans()) {
//            OrganContainer organContainer = new OrganContainer(organ);
        }
    }

}
