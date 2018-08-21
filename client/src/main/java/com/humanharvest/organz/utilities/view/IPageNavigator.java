package com.humanharvest.organz.utilities.view;

import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.components.TouchAlertTextController;
import javafx.beans.property.Property;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.stage.Window;

public interface IPageNavigator {

    /**
     * Loads the given page in the given MainController.
     *
     * @param page       the Page (enum including path to fxml file) to be loaded.
     * @param controller the MainController to load this page on to.
     */
    void loadPage(Page page, MainController controller);

    /**
     * Refreshes all windows, to be used when an update occurs. Only refreshes titles and sidebars
     */
    void refreshAllWindows();


    /**
     * Open a new window with the given size as min and current
     * @param width The width to set
     * @param height The height to set
     * @return The MainController for the new window, or null if the new window could not be created.
     */
    MainController openNewWindow(int width, int height);

    /**
     * Sets the alert window at the right size so that all the text can be read.
     */
    static void resizeAlert(Alert alert) {
        alert.getDialogPane().getScene().getWindow().sizeToScene();
    }

    /**
     * Generates a pop-up alert of the given type.
     *
     * @param alertType the type of alert to show (can determine its style and button options).
     * @param title     the text to show as the title and heading of the alert.
     * @param bodyText  the text to show within the body of the alert.
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
     * Shows a pop-up alert of the given type, and awaits user input to dismiss it (blocking).
     *
     * @param alertType the type of alert to show (can determine its style and button options).
     * @param title     the text to show as the title and heading of the alert.
     * @param bodyText  the text to show within the body of the alert.
     * @return an Optional for the button that was clicked to dismiss the alert.
     */
    Property<Boolean> showAlert(Alert.AlertType alertType, String title, String bodyText, Window window);

    TouchAlertTextController showAlertWithText(String title, String bodyText, Window window);
}