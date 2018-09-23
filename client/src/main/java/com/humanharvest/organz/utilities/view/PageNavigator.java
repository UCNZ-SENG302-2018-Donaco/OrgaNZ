package com.humanharvest.organz.utilities.view;

import javafx.beans.property.Property;
import javafx.scene.control.Alert;
import javafx.stage.Window;

import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.controller.components.TouchAlertTextController;

/**
 * Utility class for controlling navigation between pages.
 * All methods on the navigator are static to facilitate simple access from anywhere in the application.
 */
public class PageNavigator {

    private static IPageNavigator pageNavigator = new PageNavigatorStandard();

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private PageNavigator() {
        throw new IllegalStateException("Utility class");
    }

    public static void setPageNavigator(IPageNavigator navigator) {
        pageNavigator = navigator;
    }

    /**
     * Loads the given page in the given MainController.
     *
     * @param page the Page (enum including path to fxml file) to be loaded.
     * @param controller the MainController to load this page on to.
     * @return The SubController for the new age, or null if the new page could not be loaded.
     */
    public static SubController loadPage(Page page, MainController controller) {
        return pageNavigator.loadPage(page, controller);
    }

    /**
     * Refreshes all windows, to be used when an update occurs. Only refreshes titles and sidebars
     */
    public static void refreshAllWindows() {
        pageNavigator.refreshAllWindows();
    }

    /**
     * Opens a new window.
     *
     * @return The MainController for the new window, or null if the new window could not be created.
     */
    public static MainController openNewWindow(int width, int height) {
        return pageNavigator.openNewWindow(width, height);
    }

    /**
     * Opens a new window with default width and height.
     *
     * @return The MainController for the new window, or null if the new window could not be created.
     */
    public static MainController openNewWindow() {
        return openNewWindow(1016, 639);
    }

    /**
     * Generates a pop-up alert of the given type.
     *
     * @param alertType the type of alert to show (can determine its style and button options).
     * @param title the text to show as the title and heading of the alert.
     * @param bodyText the text to show within the body of the alert.
     * @return The generated alert.
     */
    public static Alert generateAlert(Alert.AlertType alertType, String title, String bodyText) {
        return pageNavigator.generateAlert(alertType, title, bodyText);
    }

    /**
     * Shows a pop-up alert of the given type, and awaits user input to dismiss it (blocking).
     *
     * @param alertType the type of alert to show (can determine its style and button options).
     * @param title the text to show as the title and heading of the alert.
     * @param bodyText the text to show within the body of the alert.
     * @return an Optional for the button that was clicked to dismiss the alert.
     */
    public static Property<Boolean> showAlert(Alert.AlertType alertType, String title, String bodyText, Window window) {
        return pageNavigator.showAlert(alertType, title, bodyText, window);
    }

    public static TouchAlertTextController showTextAlert(String title, String bodyText, Window window) {
        return pageNavigator.showAlertWithText(title, bodyText, window);
    }
}
