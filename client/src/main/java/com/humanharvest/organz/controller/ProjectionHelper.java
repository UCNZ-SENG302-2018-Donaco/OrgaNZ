package com.humanharvest.organz.controller;

import java.io.IOException;
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

    /**
     * The non primary screens.
     */
    private static List<Screen> otherScreens;

    /**
     * The stages of the projection views.
     */
    private static Stage[] stages;

    private static Pane mainPane;

    private static MainController mainController;

    private ProjectionHelper() {
    }

    /**
     * Sets up the projection helper to keep track of what screens are non-primary.
     */
    public static void initialise(Pane rootPane) {

        List<Screen> screens = Screen.getScreens();

        Bounds rootBounds = rootPane.localToScreen(rootPane.getBoundsInLocal());

        // Assume only one screen - should always be true in touch mode
        Screen thisScreen = Screen.getScreensForRectangle(
                rootBounds.getMinX(), rootBounds.getMinY(),
                rootBounds.getWidth(), rootBounds.getHeight()).get(0);

        otherScreens = screens.stream()
                .filter(screen -> !Objects.equals(screen, thisScreen))
                .collect(Collectors.toList());

        stages = new Stage[otherScreens.size()];
    }

    /**
     * Projects the controller to the non-primary screens.
     */
    public static void createNewProjection(MainController originalMainController) {

        if (mainController != null) {
            mainController.closeWindow();
        }

        for (int i = 0; i < stages.length; i++) {
            if (stages[i] != null) {
                stages[i].close();
                stages[i] = null;
            }

            Screen screen = otherScreens.get(i);

            try {
                Stage newStage = new Stage();
                newStage.setTitle("Organ Client Management System");
                FXMLLoader loader = new FXMLLoader();
                mainPane = loader.load(ProjectionHelper.class.getResourceAsStream(Page.MAIN.getPath()));
                mainController = loader.getController();
                mainController.setStage(newStage);
                mainController.setPane(mainPane);
                State.addMainController(mainController);
                newStage.setOnCloseRequest(e -> mainController.closeWindow());

                Scene scene = new Scene(mainPane);

                Rectangle2D bounds = screen.getBounds();
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

                stages[i] = newStage;

                mainController.setWindowContext(originalMainController.getWindowContext());
                PageNavigator.loadPage(originalMainController.getCurrentPage(), mainController);

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error loading new window\n", e);
            }
        }
    }

    public static void updateProjection(MainController originalMainController) {
        if (originalMainController != null && mainController != null && mainController.getCurrentPage() != null) {
            mainController.setWindowContext(originalMainController.getWindowContext());
            PageNavigator.loadPage(originalMainController.getCurrentPage(), mainController);
        }
    }

    /**
     * Closes down all the projection screens.
     */
    public static void stageClosing() {
        if (mainController != null) {
            mainController.closeWindow();
        }

        for (int i = 0; i < stages.length; i++) {
            if (stages[i] != null) {
                stages[i].close();
                stages[i] = null;
            }
        }
    }

    /**
     * Returns true if there are other screens to project to.
     */
    public static boolean canProject() {
        return otherScreens != null && !otherScreens.isEmpty();
    }
}
