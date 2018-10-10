package com.humanharvest.organz.utilities.view;

import java.util.function.Consumer;

import javafx.scene.control.Alert;
import javafx.stage.Window;

import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;

/**
 * Utility class for controlling navigation between pages.
 * All methods on the navigator are static to facilitate simple access from anywhere in the application.
 */
public final class PageNavigator {

    private static IPageNavigator instance = new PageNavigatorStandard();

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private PageNavigator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Loads the given page in the given MainController.
     *
     * @param page the Page (enum including path to fxml file) to be loaded.
     * @param controller the MainController to load this page on to.
     * @return The SubController for the new age, or null if the new page could not be loaded.
     */
    public static SubController loadPage(Page page, MainController controller) {
        return instance.loadPage(page, controller);
    }

    /**
     * Refreshes all windows, to be used when an update occurs.
     */
    public static void refreshAllWindows() {
        instance.refreshAllWindows();
    }

    /**
     * Open a new window using the transform of the current window to spawn it near the window it is being created from.
     *
     * @param prevMainController the main controller of the window the new window is being spawned from
     * @return The MainController for the new window, or null if the new window could not be created.
     */
    public static MainController openNewWindow(MainController prevMainController) {
        return instance.openNewWindow(prevMainController);
    }

    /**
     * Open a new window with the given size as min and current, and may use the transform of the current window to
     * spawn it near the window it is being created from.
     *
     * @param width The width to set
     * @param height The height to set
     * @param prevMainController the main controller of the window the new window is being spawned from
     * @return The MainController for the new window, or null if the new window could not be created.
     */
    public static MainController openNewWindow(int width, int height, MainController prevMainController) {
        return instance.openNewWindow(width, height, prevMainController);
    }

    /**
     * Opens a new window.
     *
     * @return The MainController for the new window, or null if the new window could not be created.
     */
    public static MainController openNewWindow(int width, int height) {
        return instance.openNewWindow(width, height);
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
        return instance.generateAlert(alertType, title, bodyText);
    }

    /**
     * Shows a pop-up alert of the given type, and awaits user input to dismiss it (blocking).
     *
     * @param alertType the type of alert to show (can determine its style and button options).
     * @param title the text to show as the title and heading of the alert.
     * @param bodyText the text to show within the body of the alert.
     */
    public static void showAlert(Alert.AlertType alertType, String title, String bodyText, Window window) {
        instance.showAlert(alertType, title, bodyText, window, null);
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
    public static void showAlert(Alert.AlertType alertType, String title, String bodyText, Window window,
            Runnable onOk) {
        instance.showAlert(alertType, title, bodyText, window, onOk);
    }

    /**
     * Shows a pop-up alert with a text entry box, and awaits user input to input and confirm it.
     *
     * @param title the text to show as the title and heading of the alert.
     * @param bodyText the text to show within the body of the alert.
     * @param window the window to spawn the popup relative to.
     * @param onSubmit Callback to return the input string to once the user clicks ok.
     */
    public static void showTextAlert(String title, String bodyText, String instructions,
            Window window, Consumer<String> onSubmit) {
        showTextAlert(title, bodyText, instructions, null, window, onSubmit);
    }

    /**
     * Shows a pop-up alert with a text entry box, and awaits user input to input and confirm it.
     *
     * @param title the text to show as the title and heading of the alert.
     * @param bodyText the text to show within the body of the alert.
     * @param window the window to spawn the popup relative to.
     * @param onSubmit Callback to return the input string to once the user clicks ok.
     */
    public static void showTextAlert(String title, String bodyText, String instructions, String prefilledText,
            Window window, Consumer<String> onSubmit) {
        instance.showAlertWithText(title, bodyText, instructions, prefilledText, window, onSubmit);
    }

    public static IPageNavigator getInstance() {
        return instance;
    }

    public static void setInstance(IPageNavigator navigator) {
        instance = navigator;
    }
}
