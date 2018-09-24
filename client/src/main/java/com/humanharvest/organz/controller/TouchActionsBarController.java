package com.humanharvest.organz.controller;

import java.util.logging.Logger;


import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import org.controlsfx.control.Notifications;

import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.state.State.UiType;
import com.humanharvest.organz.touch.MultitouchHandler;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.views.ActionResponseView;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;

/**
 * A class to handle everything to do with the navigation of both clients using the desktop application and
 * admins/clinicians who are using the touch application.
 */
public class TouchActionsBarController extends SubController {

    @FXML
    private Button homeButton;

    @FXML
    private Button undoButton;

    @FXML
    private Button redoButton;

    @FXML
    private Button duplicateButton;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private Pane entireMenubarPane;


    private static final Logger LOGGER = Logger.getLogger(TouchActionsBarController.class.getName());

    /**
     * Setup the menu bar colours, buttons, and hamburger.
     * @param controller
     */
    @Override
    public void setup(MainController controller) {
        super.setup(controller);
        mainController = controller;
        if (State.getSession().getLoggedInUserType() == UserType.CLIENT) {
            homeButton.setVisible(false);
            duplicateButton.setVisible(false);
            entireMenubarPane.setStyle("-fx-background-color: rgb(176, 255, 137)");
        } else {
            entireMenubarPane.setStyle("-fx-background-color: rgb(137, 186, 255)");
        }

        if (windowContext.isClinViewClientWindow()) {
            homeButton.setDisable(true);
            entireMenubarPane.setStyle("-fx-background-color: rgb(255, 217, 137)");
        }

        hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> toggleSidebar(controller.getDrawer()));
        refresh();
    }

    /**
     * Open the drawer if closed. Close the drawer if open
     * @param drawer the item to toggle
     */
    private void toggleSidebar(JFXDrawer drawer) {
        if (drawer.isOpened()) {
            closeSidebar(drawer);
        } else {
            openSidebar(drawer);
        }

    }

    /**
     * If the draw item is open, it will be closed.
     * @param drawer the item to close
     */
    private void closeSidebar(JFXDrawer drawer) {
        drawer.close();
        drawer.setDisable(true);
        drawer.setVisible(false);
    }

    /**
     * If the draw item is closed, it will be opened.
     * @param drawer the item to open
     */
    private void openSidebar(JFXDrawer drawer) {
        drawer.open();
        drawer.setDisable(false);
    }

    /**
     * Navigates the user to the home screen
     */
    @FXML
    private void navigateHome() {
        // We need to navigate to our dashboard (for now we're just loading the search clients page).
        PageNavigator.loadPage(Page.SEARCH, mainController);
    }

    /**
     * Undo the last action
     */
    @FXML
    private void undoAction() {
        ActionResponseView responseView = State.getActionResolver().executeUndo(null);
        Notifications.create().title("Undo").text(responseView.getResultText()).showInformation();
        PageNavigator.refreshAllWindows();
    }

    /**
     * Redo the last action
     */
    @FXML
    private void redoAction() {
        ActionResponseView responseView = State.getActionResolver().executeRedo(null);
        Notifications.create().title("Redo").text(responseView.getResultText()).showInformation();
        PageNavigator.refreshAllWindows();
    }

    /**
     * Refresh the page
     */
    @Override
    public void refresh() {
        ActionResponseView responseView = State.getActionResolver().getUndo();
        undoButton.setDisable(!responseView.isCanUndo());
        redoButton.setDisable(!responseView.isCanRedo());
    }

    /**
     * Open another instance of the currently opened window
     */
    @FXML
    private void duplicateWindow() {
        MainController newMain = PageNavigator.openNewWindow();
        if (newMain != null) {
            newMain.setWindowContext(mainController.getWindowContext());
            PageNavigator.loadPage(mainController.getCurrentPage(), newMain);
        } else {
            PageNavigator.showAlert(AlertType.ERROR, "Error duplicating page",
                    "The new page could not be created", mainController.getStage());
        }
    }

    /**
     * Log out of the currently logged in user and take them back to the landing page
     */
    public void logout() {
        State.logout();
        for (MainController controller : State.getMainControllers()) {
            if (controller != mainController) {
                controller.closeWindow();
            }
        }
        State.clearMainControllers();
        State.addMainController(mainController);
        mainController.resetWindowContext();
        PageNavigator.loadPage(Page.LOGIN_STAFF, mainController);
        closeSidebar(mainController.getDrawer());
    }

    /**
     * Exit the pane that is currently open
     */
    public void exit() {
        if (State.getUiType() == UiType.TOUCH) {
            MultitouchHandler.removePane(mainController.getPane());
        } else {
            Stage stage = (Stage) mainController.getStage().getScene().getWindow();
            stage.close();
        }
    }
}