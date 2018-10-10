package com.humanharvest.organz.utilities.view;

import java.util.function.Consumer;

import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.stage.Window;

import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;

public interface IPageNavigator {

    /**
     * Sets the alert window at the right size so that all the text can be read.
     * If the alert, its dialog pane, or its dialog pane's scene is null, it does nothing. (This will happen
     * if the alert has been closed by the user, so we don't need to resize it anyway).
     */
    static void resizeAlert(Alert alert) {
        if (alert != null && alert.getDialogPane() != null && alert.getDialogPane().getScene() != null) {
            alert.getDialogPane().getScene().getWindow().sizeToScene();
        }
    }

    /**
     * Loads the given page in the given MainController.
     *
     * @param page the Page (enum including path to fxml file) to be loaded.
     * @param controller the MainController to load this page on to.
     * @return The SubController for the new age, or null if the new page could not be loaded.
     */
    SubController loadPage(Page page, MainController controller);

    /**
     * Refreshes all windows, to be used when an update occurs. Only refreshes titles and sidebars
     */
    void refreshAllWindows();

    /**
     * Open a new window with the given size as min and current
     *
     * @param width The width to set
     * @param height The height to set
     * @return The MainController for the new window, or null if the new window could not be created.
     */
    MainController openNewWindow(int width, int height);

    /**
     * Open a new window using the transform of the current window to spawn it near the window it is being created from.
     *
     * @param prevMainController the main controller of the window the new window is being spawned from
     * @return The MainController for the new window, or null if the new window could not be created.
     */
    MainController openNewWindow(MainController prevMainController);

    /**
     * Open a new window with the given size as min and current, and may use the transform of the current window to
     * spawn it near the window it is being created from.
     *
     * @param width The width to set
     * @param height The height to set
     * @param prevMainController The main controller for the window the new window is being spawned from
     * @return The MainController for the new window, or null if the new window could not be created.
     */
    MainController openNewWindow(int width, int height, MainController prevMainController);

    /**
     * Generates a pop-up alert of the given type.
     *
     * @param alertType the type of alert to show (can determine its style and button options).
     * @param title the text to show as the title and heading of the alert.
     * @param bodyText the text to show within the body of the alert.
     * @return The generated alert.
     */
    default Alert generateAlert(Alert.AlertType alertType, String title, String bodyText) {
        Alert alert = new Alert(alertType);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.contentTextProperty().addListener(observable -> resizeAlert(alert));

        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(bodyText);
        return alert;
    }

    /**
     * Shows a pop-up alert of the given type, and awaits user input to dismiss it.
     *
     * @param alertType the type of alert to show (can determine its style and button options).
     * @param title the text to show as the title and heading of the alert.
     * @param bodyText the text to show within the body of the alert.
     * @param window the window to spawn the popup relative to.
     * @param onOk a callback for when the ok button is clicked.
     */
    void showAlert(Alert.AlertType alertType, String title, String bodyText, Window window, Runnable onOk);

    /**
     * Shows a pop-up alert with a text entry box, and awaits user input to input and confirm it.
     *
     * @param title the text to show as the title and heading of the alert.
     * @param bodyText the text to show within the body of the alert.
     * @param window the window to spawn the popup relative to.
     * @param onSubmit Callback to return the input string to once the user clicks ok.
     */
    void showAlertWithText(String title, String bodyText, Window window, Consumer<String> onSubmit);
}