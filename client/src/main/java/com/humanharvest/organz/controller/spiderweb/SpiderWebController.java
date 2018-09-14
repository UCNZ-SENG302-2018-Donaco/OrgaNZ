package com.humanharvest.organz.controller.spiderweb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.touch.FocusArea;
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

    private Pane canvas;
    private Pane deceasedDonorPane;
    private List<Pane> organNodes = new ArrayList<>();

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
        Stage stage = new Stage();
        stage.setTitle("Organ Spider Web");
        canvas = new TuioFXCanvas();
        Scene scene = new Scene(canvas);

        FXMLLoader loader = new FXMLLoader();

        try {
            Pane backPane = loader.load(PageNavigatorTouch.class.getResourceAsStream(Page.BACKDROP.getPath()));

            canvas.getChildren().add(backPane);
            MultitouchHandler.initialise(canvas);

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
        MainController newMain = PageNavigator.openNewWindow(200, 400);
        PageNavigator.loadPage(Page.RECEIVER_OVERVIEW, newMain);
        deceasedDonorPane = newMain.getPane();

        FocusArea focusArea = ((FocusArea) newMain.getPane().getUserData());
        focusArea.setTransform(new Affine(new Translate(1000, 500)));
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
                Pane organPane = newMain.getPane();
                organNodes.add(organPane);

                FocusArea focusArea = ((FocusArea) organPane.getUserData());
                focusArea.setTransform(new Affine(new Translate(x, y)));
                x += 100;
                y += 50;

                // Create the line
                Line connector = new Line();
                connector.setFill(Color.BLACK);
                connector.setStroke(Color.BLACK);
                deceasedDonorPane.localToParentTransformProperty().addListener((observable, oldValue, newValue) -> {
                    Bounds bounds = deceasedDonorPane.getBoundsInParent();
                    connector.setStartX(bounds.getMinX() + bounds.getWidth()/2);
                    connector.setStartY(bounds.getMinY() + bounds.getHeight()/2);
                });
                organPane.localToParentTransformProperty().addListener((observable, oldValue, newValue) -> {
                    Bounds bounds = organPane.getBoundsInParent();
                    connector.setEndX(bounds.getMinX() + bounds.getWidth()/2);
                    connector.setEndY(bounds.getMinY() + bounds.getHeight()/2);
                });
                // TODO probably remove at some point
                Bounds bounds = deceasedDonorPane.getBoundsInParent();
                connector.setStartX(bounds.getMinX() + bounds.getWidth()/2);
                connector.setStartY(bounds.getMinY() + bounds.getHeight()/2);
                Bounds bounds2 = organPane.getBoundsInParent();
                connector.setEndX(bounds.getMinX() + bounds2.getWidth()/2);
                connector.setEndY(bounds.getMinY() + bounds2.getHeight()/2);
                canvas.getChildren().add(connector);
            }
        }
    }
}
