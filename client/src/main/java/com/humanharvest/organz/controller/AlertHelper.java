package com.humanharvest.organz.controller;

import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.PageNavigator;
import javafx.scene.control.Alert.AlertType;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class AlertHelper {

    private AlertHelper() {
    }

    /**
     * Logs a warning, and shows an alert to the user about the client not being found.
     *
     * @param logger         the logger to use
     * @param e              the exception that was thrown to cause this error
     * @param mainController the main controller, whose stage is passed to PageNavigator.showAlert()
     */
    public static void showNotFoundAlert(Logger logger, NotFoundException e, MainController mainController) {
        logger.log(Level.WARNING, "Client not found", e);
        PageNavigator.showAlert(AlertType.WARNING,
                "Client not found",
                "The client could not be found on the server, it may have been deleted",
                mainController.getStage());
    }


    /**
     * Logs a warning, and shows an alert to the user about not being able to apply changes on the server.
     *
     * @param logger         the logger to use
     * @param e              the exception that was thrown to cause this error
     * @param mainController the main controller, whose stage is passed to PageNavigator.showAlert()
     */
    public static void showRestAlert(Logger logger, ServerRestException e, MainController mainController) {
        logger.log(Level.WARNING, e.getMessage(), e);
        PageNavigator.showAlert(AlertType.ERROR,
                "Server error",
                "Could not apply changes on the server, please try again later", mainController.getStage());
    }

    /**
     * Logs a warning, and shows an alert to the user about the client being modified since you retrieved the data.
     *
     * @param logger         the logger to use
     * @param e              the exception that was thrown to cause this error
     * @param mainController the main controller, whose stage is passed to PageNavigator.showAlert()
     */
    public static void showIfMatchAlert(Logger logger, IfMatchFailedException e, MainController mainController) {
        logger.log(Level.INFO, "If-Match did not match", e);
        PageNavigator.showAlert(AlertType.INFORMATION,
                "Outdated Data",
                "The client has been modified since you retrieved the data. "
                        + "If you would still like to apply these changes please submit again, "
                        + "otherwise refresh the page to update the data.", mainController.getStage());
    }
}
