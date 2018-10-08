package com.humanharvest.organz.utilities.view;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.State;

/**
 * Utility class for controlling navigation between pages.
 * All methods on the navigator are static to facilitate simple access from anywhere in the application.
 */
public class PageNavigatorStandard implements IPageNavigator {

    private static final Logger LOGGER = Logger.getLogger(PageNavigatorStandard.class.getName());

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
            FXMLLoader loader = new FXMLLoader(PageNavigatorStandard.class.getResource(page.getPath()));
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
        return openNewWindow(width, height);
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
            newStage.setTitle("Organ Client Management System");
            FXMLLoader loader = new FXMLLoader();
            Pane mainPane = loader.load(PageNavigatorStandard.class.getResourceAsStream(Page.MAIN.getPath()));
            MainController mainController = loader.getController();
            mainController.setStage(newStage);
            mainController.setPane(mainPane);
            State.addMainController(mainController);
            newStage.setOnCloseRequest(e -> {
                State.deleteMainController(mainController);
            });

            Scene scene = new Scene(mainPane);
            newStage.setScene(scene);
            newStage.show();

            newStage.setMinWidth(width);
            newStage.setMinHeight(height);
            newStage.setWidth(width);
            newStage.setHeight(height);

            mainPane.setPrefWidth(width);
            mainPane.setPrefHeight(height);

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
     * @param window the window to spawn the popup relative to.
     * @param onOk a callback for when the ok button is clicked.
     */
    @Override
    public void showAlert(Alert.AlertType alertType, String title, String bodyText, Window window,
            Runnable onOk) {
        Alert alert = generateAlert(alertType, title, bodyText);
        Optional<ButtonType> result = alert.showAndWait();

        // If there is a callback, and the user clicked OK, respond
        if (result.isPresent() && result.get() == ButtonType.OK && onOk != null) {
            onOk.run();
        }
    }

    /**
     * Shows a pop-up alert with a text entry box, and awaits user input to input and confirm it (blocking).
     *
     * @param title the text to show as the title and heading of the alert.
     * @param bodyText the text to show within the body of the alert.
     * @param window the window to spawn the popup relative to.
     * @param onSubmit Callback to return the input string to once the user clicks ok.
     */
    @Override
    public void showAlertWithText(String title, String bodyText, Window window, Consumer<String> onSubmit) {
        TextInputDialog popup = new TextInputDialog();
        popup.setTitle(title);
        popup.setHeaderText(bodyText);
        popup.setContentText("Reason:");
        popup.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
        popup.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            popup.getDialogPane().lookupButton(ButtonType.OK).setDisable(newValue.isEmpty());
        });

        Optional<String> response = popup.showAndWait();

        // If they submitted, callback with the text
        response.ifPresent(onSubmit);
    }
}