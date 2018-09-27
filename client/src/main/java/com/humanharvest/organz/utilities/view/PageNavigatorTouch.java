package com.humanharvest.organz.utilities.view;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.stage.Window;

import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.controller.components.TouchAlertController;
import com.humanharvest.organz.controller.components.TouchAlertTextController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.touch.FocusArea;
import com.humanharvest.organz.touch.MultitouchHandler;
import com.humanharvest.organz.touch.PointUtils;

/**
 * Utility class for controlling navigation between pages.
 * All methods on the navigator are static to facilitate simple access from anywhere in the application.
 */
public class PageNavigatorTouch implements IPageNavigator {

    private static final Logger LOGGER = Logger.getLogger(PageNavigatorTouch.class.getName());

    private static Optional<Transform> getWindowTransform(Window window) {
        if (window == null) {
            return Optional.empty();
        }

        Scene scene = window.getScene();

        if (scene == null) {
            return Optional.empty();
        }

        Parent root = scene.getRoot();
        if (root == null) {
            return Optional.empty();
        }

        List<Transform> transforms = root.getTransforms();
        if (transforms.size() != 1) {
            return Optional.empty();
        }

        return Optional.of(transforms.get(0));
    }

    /**
     * Loads the given page in the given MainController.
     *
     * @param page the Page (enum including path to fxml file) to be loaded.
     * @param controller the MainController to load this page on to.
     * @return The SubController for the new age, or null if the new page could not be loaded.
     */
    @Override
    public SubController loadPage(Page page, MainController controller) {
        try {
            LOGGER.info("Loading page: " + page);
            FXMLLoader loader = new FXMLLoader(PageNavigatorTouch.class.getResource(page.getPath()));
            Node loadedPage = loader.load();
            SubController subController = loader.getController();
            subController.setup(controller);
            controller.setSubController(subController);

            controller.setPage(page, loadedPage);
            return subController;

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Couldn't load the page", e);
            showAlert(Alert.AlertType.ERROR, "Could not load page: " + page,
                    "The page loader failed to load the layout for the page.", controller.getStage(), null);
            return null;
        }
    }

    /**
     * Refreshes all windows, to be used when an update occurs. Only refreshes titles and sidebars
     */
    @Override
    public void refreshAllWindows() {
        LOGGER.info("Refreshing all windows");
        for (MainController controller : State.getMainControllers()) {
            controller.refresh();
        }
    }

    /**
     * Open a new window using the transform of the current window to spawn it near the window it is being created from.
     *
     * @param prevMainController the main controller of the window the new window is being spawned from
     * @return The MainController for the new window, or null if the new window could not be created.
     */
    @Override
    public MainController openNewWindow(MainController prevMainController) {
        return openNewWindow(1016, 639, prevMainController);
    }

    /**
     * Open a new window with the given size as min and current, and may use the transform of the current window to
     * spawn it near the window it is being created from.
     *
     * @param width The width to set
     * @param height The height to set
     * @param prevMainController The main controller for the window the new window is being spawned from
     * @return The MainController for the new window, or null if the new window could not be created.
     */
    @Override
    public MainController openNewWindow(int width, int height, MainController prevMainController) {
        FocusArea focusArea = (FocusArea) prevMainController.getPane().getUserData();
        return openNewWindow(focusArea.getTransform(), width, height, 0.1);
    }

    /**
     * Opens a new window.
     *
     * @return The MainController for the new window, or null if the new window could not be created.
     */
    @Override
    public MainController openNewWindow(int width, int height) {
        return openNewWindow(width, height, FocusArea::new);
    }

    /**
     * Open a new window and apply the given transform to the new window to spawn it on top of that position.
     *
     * @param transform The transform to apply to the new window once it's created
     * @return The MainController for the new window, or null if the new window could not be created.
     */
    public MainController openNewWindow(Affine transform) {
        return openNewWindow(transform, 1016, 639, 0.5);
    }

    /**
     * Open a new window and apply the given transform to the new window to spawn it on top of that position. Also set
     * its width and height to the given values.
     *
     * @param transform The transform to apply to the new window once it's created
     * @param width The width of the new window
     * @param height The height of the new window
     * @param offsetScale How much to scale the offset by
     * @return The MainController for the new window, or null if the new window could not be created.
     */
    public MainController openNewWindow(Affine transform, int width, int height, double offsetScale) {
        MainController mainController = openNewWindow(width, height);
        if (mainController == null) {
            return null;
        }

        FocusArea focusArea = (FocusArea) mainController.getPane().getUserData();

        Affine newTransform = transform.clone();

        double scaleX = PointUtils.length(transform.getMxx(), transform.getMyx(), transform.getMzx());
        double scaleY = PointUtils.length(transform.getMxy(), transform.getMyy(), transform.getMzy());

        double deltaX = -scaleX * (width * offsetScale);
        double deltaY = -scaleY * (height * offsetScale);

        newTransform.append(new Translate(deltaX, deltaY));
        focusArea.setTransform(newTransform);
        return mainController;
    }

    /**
     * Opens a new window.
     *
     * @return The MainController for the new window, or null if the new window could not be created.
     */
    public MainController openNewWindow(int width, int height, Function<Pane, FocusArea> focusAreaCreator) {
        LOGGER.info("Opening new window");
        try {
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Pane mainPane = loader.load(PageNavigatorTouch.class.getResourceAsStream(Page.TOUCH_MAIN.getPath()));
            MainController mainController = loader.getController();

            Scene scene = new Scene(mainPane);
            newStage.setScene(scene);

            mainController.setStage(newStage);
            mainController.setPane(mainPane);
            State.addMainController(mainController);
            newStage.setOnCloseRequest(e -> {
                State.deleteMainController(mainController);
                MultitouchHandler.removePane(mainPane);
            });

            newStage.setWidth(width);
            newStage.setHeight(height);
            newStage.setMinWidth(width);
            newStage.setMinHeight(height);

            mainPane.setPrefWidth(width);
            mainPane.setPrefHeight(height);

            StackPane stackPane = (StackPane) mainPane.getChildren().get(0);
            stackPane.setPrefWidth(width);
            stackPane.setPrefHeight(height);

            FocusArea focusArea = focusAreaCreator.apply(mainPane);
            MultitouchHandler.addPane(mainPane, focusArea);

            return mainController;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading new window\n", e);
            // Will throw if MAIN's fxml file could not be loaded.
            showAlert(Alert.AlertType.ERROR, "New window could not be created",
                    "The page loader failed to load the layout for the new window.", null, null);
            return null;
        }
    }

    /**
     * Shows a pop-up alert of the given type, and awaits user input to dismiss it (blocking).
     *
     * @param alertType the type of alert to show (can determine its style and button options).
     * @param title the text to show as the title and heading of the alert.
     * @param bodyText the text to show within the body of the alert.
     * @param onResponse a callback for when an ok/cancel button is clicked.
     */
    @Override
    public void showAlert(Alert.AlertType alertType, String title, String bodyText, Window window,
            Consumer<Boolean> onResponse) {
        LOGGER.info("Opening new window");
        try {
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(PageNavigatorTouch.class.getResource(Page.TOUCH_ALERT.getPath()));
            Pane mainPane = loader.load();

            TouchAlertController controller = loader.getController();
            controller.setup(alertType, title, bodyText, newStage, mainPane, onResponse);

            MultitouchHandler.addPane(mainPane);

            // Set the positioning based off the calling window if it is valid.
            getWindowTransform(window).ifPresent(transform -> {
                mainPane.getTransforms().add(new Affine(transform));
            });

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading new window\n", e);
            if (onResponse != null) {
                onResponse.accept(false);
            }
        }
    }

    @Override
    public TouchAlertTextController showAlertWithText(String title, String bodyText, Window window) {
        LOGGER.info("Opening new window");
        try {
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(PageNavigatorTouch.class.getResource(Page.TOUCH_ALERT_TEXT.getPath()));
            Pane mainPane = loader.load();

            TouchAlertTextController controller = loader.getController();
            controller.setup(title, bodyText, newStage, mainPane);

            MultitouchHandler.addPane(mainPane);

            // Set the positioning based off the calling window if it is valid.
            getWindowTransform(window).ifPresent(transform -> {
                mainPane.getTransforms().add(new Affine(transform));
            });

            return controller;

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading new window\n", e);
            return null;
        }
    }
}
