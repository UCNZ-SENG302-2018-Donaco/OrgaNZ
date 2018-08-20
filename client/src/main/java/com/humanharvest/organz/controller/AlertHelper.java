package com.humanharvest.organz.controller;

import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.PageNavigator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert.AlertType;

public final class AlertHelper {

    private AlertHelper() {
    }

    public static void showNotFoundAlert(Logger logger, NotFoundException e, MainController mainController) {
        logger.log(Level.WARNING, "Client not found", e);
        PageNavigator.showAlert(AlertType.WARNING,
            "Client not found",
            "The client could not be found on the server, it may have been deleted",
            mainController.getStage());
    }

    public static void showRestAlert(Logger logger, ServerRestException e, MainController mainController) {
        logger.log(Level.WARNING, e.getMessage(), e);
        PageNavigator.showAlert(AlertType.ERROR,
            "Server error",
            "Could not apply changes on the server, please try again later", mainController.getStage());
    }

    public static void showIfMatchAlert(Logger logger, IfMatchFailedException e, MainController mainController) {
        logger.log(Level.INFO, "If-Match did not match", e);
        PageNavigator.showAlert(AlertType.INFORMATION,
            "Outdated Data",
            "The client has been modified since you retrieved the data. "
                + "If you would still like to apply these changes please submit again, "
                + "otherwise refresh the page to update the data.", mainController.getStage());
    }
}
