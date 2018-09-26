package com.humanharvest.organz.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.controlsfx.control.Notifications;

import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.views.ActionResponseView;


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
    private Button exitButton;

    @FXML
    private Button hamburger;

    @FXML
    private Pane entireMenubarPane;

    private static final Logger LOGGER = Logger.getLogger(TouchActionsBarController.class.getName());

    /**
     * Setup the menu bar colours, buttons, and hamburger.
     * @param controller the controller to setup
     */
    @Override
    public void setup(MainController controller) {
        super.setup(controller);
        mainController = controller;
        if (State.getSession().getLoggedInUserType() == UserType.CLIENT) {
            homeButton.setVisible(false);
            duplicateButton.setVisible(false);
            entireMenubarPane.setStyle("-fx-background-color: rgb(137, 186, 255)");

        } else {
            entireMenubarPane.setStyle("-fx-background-color: rgb(137, 186, 255)");
        }

        if (windowContext.isClinViewClientWindow()) {
            homeButton.setDisable(true);
            entireMenubarPane.setStyle("-fx-background-color: #D9B3FF");
        }
        hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> toggleSidebar(controller.getDrawer()));
        refresh();
    }

    /**
     * Open the drawer if closed. Close the drawer if open
     * @param drawer the item to toggle
     */
    private void toggleSidebar(Pane drawer) {
        if (drawer.isVisible()) {
            closeSidebar(drawer);
        } else {
            openSidebar(drawer);
        }

    }

    /**
     * If the draw item is open, it will be closed.
     * @param drawer the item to close
     */
    private void closeSidebar(Pane drawer) {
        drawer.setDisable(true);
        drawer.setVisible(false);
    }

    /**
     * If the draw item is closed, it will be opened.
     * @param drawer the item to open
     */
    private void openSidebar(Pane drawer) {
        drawer.setDisable(false);
        drawer.setVisible(true);
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
        ActionResponseView responseView = State.getActionResolver().executeUndo(State.getRecentEtag());
        Notifications.create().title("Undo").text(responseView.getResultText()).showInformation();
        PageNavigator.refreshAllWindows();
    }

    /**
     * Redo the last action
     */
    @FXML
    private void redoAction() {
        ActionResponseView responseView = State.getActionResolver().executeRedo(State.getRecentEtag());
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

        // Disable exit button if this is the last clinician window
        if (!windowContext.isClinViewClientWindow() && State.getMainControllers().stream()
                .filter(controller -> !controller.getWindowContext().isClinViewClientWindow())
                .count() <= 1) {
            exitButton.setDisable(true);
        } else {
            exitButton.setDisable(false);
        }
    }

    @FXML
    private void refreshWindow() {
        mainController.refresh();
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
            State.getMainControllers().forEach(MainController::refreshNavigation);
        } else {
            PageNavigator.showAlert(AlertType.ERROR, "Error duplicating page",
                    "The new page could not be created", mainController.getStage());
            LOGGER.log(Level.SEVERE, "Unable to duplicate page");
        }
    }

    /**
     * Log out of the currently logged in user and take them back to the landing page if they're a client,
     * otherwise take them back to the staff login page.
     */
    public void logout() {
        UserType userType = State.getSession().getLoggedInUserType();
        State.logout();
        List<MainController> toClose = State.getMainControllers().stream()
                .filter(controller -> controller != mainController)
                .collect(Collectors.toList());
        toClose.forEach(MainController::closeWindow);

        State.clearMainControllers();
        State.addMainController(mainController);
        mainController.resetWindowContext();

        closeSidebar(mainController.getDrawer());
        if (userType == UserType.CLIENT) {
            PageNavigator.loadPage(Page.LANDING, mainController);
        } else {
            PageNavigator.loadPage(Page.LOGIN_STAFF, mainController);
        }
    }

    /**
     * Exit the pane that is currently open
     */
    public void exit() {
        mainController.closeWindow();
        State.getMainControllers().forEach(MainController::refreshNavigation);
    }
}
