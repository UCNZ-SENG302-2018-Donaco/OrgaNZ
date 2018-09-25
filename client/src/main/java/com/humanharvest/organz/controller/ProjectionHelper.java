package com.humanharvest.organz.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;

public final class ProjectionHelper {

    private static final Logger LOGGER = Logger.getLogger(ProjectionHelper.class.getName());

    private static Screen thisScreen;
    private static Collection<Screen> otherScreens;

    private ProjectionHelper() {

    }

    public static void initialise(Pane rootPane) {

        List<Screen> screens = Screen.getScreens();

        Bounds rootBounds = rootPane.localToScreen(rootPane.getBoundsInLocal());

        // Assume only one screen - should always be true in touch mode
        thisScreen = Screen.getScreensForRectangle(
                rootBounds.getMinX(), rootBounds.getMinY(),
                rootBounds.getWidth(), rootBounds.getHeight()).get(0);

        otherScreens = screens.stream()
                .filter(screen -> !Objects.equals(screen, thisScreen))
                .collect(Collectors.toList());
    }

    public static void createNewProjection(MainController originalMainController) {

        // TODO: Remove old projection

        for (Screen screen : otherScreens) {

            try {
                Stage newStage = new Stage();
                newStage.setTitle("Organ Client Management System");
                FXMLLoader loader = new FXMLLoader();
                Pane mainPane = loader.load(ProjectionHelper.class.getResourceAsStream(Page.MAIN.getPath()));
                MainController mainController = loader.getController();
                mainController.setStage(newStage);
                mainController.setPane(mainPane);
                State.addMainController(mainController);
                newStage.setOnCloseRequest(e -> {
                    State.deleteMainController(mainController);
                });

                Scene scene = new Scene(mainPane);

                Rectangle2D bounds = screen.getVisualBounds();
                newStage.setX(bounds.getMinX());
                newStage.setY(bounds.getMinY());
                newStage.setMinWidth(bounds.getWidth());
                newStage.setWidth(bounds.getWidth());
                mainPane.setPrefWidth(bounds.getWidth());
                newStage.setMinHeight(bounds.getHeight());
                newStage.setHeight(bounds.getHeight());
                mainPane.setPrefHeight(bounds.getHeight());

                newStage.setScene(scene);
                newStage.initStyle(StageStyle.UNDECORATED);
                newStage.show();

                mainController.setWindowContext(originalMainController.getWindowContext());
                PageNavigator.loadPage(originalMainController.getCurrentPage(), mainController);

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error loading new window\n", e);
            }
        }
    }
}
