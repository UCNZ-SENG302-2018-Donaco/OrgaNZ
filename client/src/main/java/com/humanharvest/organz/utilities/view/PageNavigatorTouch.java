package com.humanharvest.organz.utilities.view;

import com.humanharvest.organz.MultitouchHandler;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.controller.components.TouchAlertController;
import com.humanharvest.organz.controller.components.TouchAlertTextController;
import com.humanharvest.organz.state.State;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Utility class for controlling navigation between pages.
 * All methods on the navigator are static to facilitate simple access from anywhere in the application.
 */
public class PageNavigatorTouch implements IPageNavigator {

    private static final Logger LOGGER = Logger.getLogger(PageNavigatorTouch.class.getName());

    /**
     * Loads the given page in the given MainController.
     *
     * @param page       the Page (enum including path to fxml file) to be loaded.
     * @param controller the MainController to load this page on to.
     */
    @Override
    public void loadPage(Page page, MainController controller) {
        try {
            LOGGER.info("Loading page: " + page);
            FXMLLoader loader = new FXMLLoader(PageNavigatorTouch.class.getResource(page.getPath()));
            Node loadedPage = loader.load();
            SubController subController = loader.getController();
            subController.setup(controller);
            controller.setSubController(subController);

            controller.setPage(page, loadedPage);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Couldn't load the page", e);
            showAlert(Alert.AlertType.ERROR, "Could not load page: " + page,
                    "The page loader failed to load the layout for the page.", controller.getStage());
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
     * Opens a new window.
     *
     * @return The MainController for the new window, or null if the new window could not be created.
     */
    @Override
    public MainController openNewWindow(int width, int height) {
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

            MultitouchHandler.addPane(mainPane);
            MultitouchHandler.setupPaneListener(mainPane);

            return mainController;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading new window\n", e);
            // Will throw if MAIN's fxml file could not be loaded.
            showAlert(Alert.AlertType.ERROR, "New window could not be created",
                    "The page loader failed to load the layout for the new window.", null);
            return null;
        }
    }

    /**
     * Shows a pop-up alert of the given type, and awaits user input to dismiss it (blocking).
     *
     * @param alertType the type of alert to show (can determine its style and button options).
     * @param title     the text to show as the title and heading of the alert.
     * @param bodyText  the text to show within the body of the alert.
     * @return an Optional for the button that was clicked to dismiss the alert.
     */
    @Override
    public Property<Boolean> showAlert(Alert.AlertType alertType, String title, String bodyText, Window window) {
        LOGGER.info("Opening new window");
        try {
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(PageNavigatorTouch.class.getResource(Page.TOUCH_ALERT.getPath()));
            Pane mainPane = loader.load();

            TouchAlertController controller = loader.getController();
            controller.setup(alertType, title, bodyText, newStage, mainPane);

            MultitouchHandler.addPane(mainPane);
            MultitouchHandler.setupPaneListener(mainPane);

            // Set the positioning based off the calling window if it is valid.
            getWindowTransform(window).ifPresent(transform -> {
                mainPane.getTransforms().add(new Affine(transform));
            });

            return controller.getResultProperty();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading new window\n", e);
            Property<Boolean> result = new SimpleBooleanProperty();
            result.setValue(false);
            return result;
        }
    }

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
            MultitouchHandler.setupPaneListener(mainPane);

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
