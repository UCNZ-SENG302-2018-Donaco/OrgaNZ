package seng302.Utilities.View;

import java.io.IOException;
import java.util.Optional;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;

import seng302.Controller.MainController;
import seng302.Controller.SubController;

/**
 * Utility class for controlling navigation between pages.
 * All methods on the navigator are static to facilitate simple access from anywhere in the application.
 */
public class PageNavigator {

    /**
     * Loads the given page in the given MainController.
     * @param page the Page (enum including path to fxml file) to be loaded.
     * @param controller the MainController to load this page on to.
     */
    public static void loadPage(Page page, MainController controller) {
        try {
            FXMLLoader loader = new FXMLLoader(PageNavigator.class.getResource(page.getPath()));
            Node loadedPage = loader.load();
            SubController subController = loader.getController();
            subController.setup(controller);
            controller.setPage(page, loadedPage);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Could not load page: " + page.toString(),
                    "The page loader failed to load the layout for the page.");
        }
    }

    /**
     * Refreshes the current page in the given MainController.
     * @param controller the MainController to refresh.
     */
    public static void refreshPage(MainController controller) {
        Page page = controller.getCurrentPage();
        loadPage(page, controller);
    }

    /**
     * Opens a new window.
     * @return The MainController for the new window, or null if the new window could not be created.
     */
    public static MainController openNewWindow() {
        try {
            Stage newStage = new Stage();
            newStage.setTitle("Organ Donor Management System");

            FXMLLoader loader = new FXMLLoader();
            Pane mainPane = loader.load(PageNavigator.class.getResourceAsStream(Page.MAIN.getPath()));
            MainController mainController = loader.getController();
            mainController.setStage(newStage);

            newStage.setScene(new Scene(mainPane));
            newStage.show();

            return mainController;
        } catch (IOException exc) {
            // Will throw if MAIN's fxml file could not be loaded.
            showAlert(Alert.AlertType.ERROR, "New window could not be created",
                    "The page loader failed to load the layout for the new window.");
            return null;
        }
    }

    /**
     * Sets the alert window at the right size so that all the text can be read.
     * This function is adapted from https://stackoverflow.com/a/31208445/8355496
     * Licenced under cc by-sa 3.0 with attribution required https://creativecommons.org/licenses/by-sa/3.0/
     */
    public static void resizeAlert(Alert alert) {
        DialogPane dialogPane;
        Scene scene;
        Window window;

        if (alert != null) {
            dialogPane = alert.getDialogPane();
            if (dialogPane != null) {
                scene = dialogPane.getScene();
                if (scene != null) {
                    window = scene.getWindow();
                    if (window != null) {
                        window.sizeToScene();
                    }
                }
            }
        }
    }

    /**
     * Shows a pop-up alert of the given type.
     * @param alertType the type of alert to show (can determine its style and button options).
     * @param title the text to show as the title and heading of the alert.
     * @param bodyText the text to show within the body of the alert.
     * @return an Optional for the button that was clicked to dismiss the alert.
     */
    public static Alert generateAlert(Alert.AlertType alertType, String title, String bodyText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(bodyText);
        alert.setResizable(true);
        resizeAlert(alert);
        return alert;
    }

    /**
     * Shows a pop-up alert of the given type.
     * @param alertType the type of alert to show (can determine its style and button options).
     * @param title the text to show as the title and heading of the alert.
     * @param bodyText the text to show within the body of the alert.
     * @return an Optional for the button that was clicked to dismiss the alert.
     */
    public static Optional<ButtonType> showAlert(Alert.AlertType alertType, String title, String bodyText) {
        Alert alert = generateAlert(alertType, title, bodyText);
        return alert.showAndWait();
    }
}
