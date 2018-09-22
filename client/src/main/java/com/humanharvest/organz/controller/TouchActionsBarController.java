package com.humanharvest.organz.controller;

import java.util.logging.Logger;


import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import org.controlsfx.control.Notifications;

import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.state.State.UiType;
import com.humanharvest.organz.touch.MultitouchHandler;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext;
import com.humanharvest.organz.views.ActionResponseView;

import com.jfoenix.controls.JFXHamburger;


public class TouchActionsBarController extends SubController {

    @FXML
    private Button homeButton;

    @FXML
    private Button undoButton;

    @FXML
    private Button redoButton;

    @FXML
    private JFXHamburger hamburger;

    private static final Logger LOGGER = Logger.getLogger(TouchActionsBarController.class.getName());

    @Override
    public void setup(MainController controller) {
        super.setup(controller);
        if (State.getSession().getLoggedInUserType() == UserType.CLIENT) {
            homeButton.setVisible(false);
        }

        if (windowContext.isClinViewClientWindow()) {
            homeButton.setDisable(true);
        }

        hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {

            if (controller.getDrawer().isShown()) {

                controller.loadSidebar(null);

                controller.getDrawer().close();
                controller.getDrawer().setDisable(true);
            } else {
                controller.getDrawer().open();
                controller.getDrawer().setDisable(false);
            }
        });
        refreshPage();
    }

    @FXML
    private void navigateHome() {
        // We need to navigate to our dashboard (for now we're just loading the search clients page).
        PageNavigator.loadPage(Page.SEARCH, mainController);
    }

    @FXML
    private void undoAction() {
        ActionResponseView responseView = State.getActionResolver().executeUndo(null);
        Notifications.create().title("Undo").text(responseView.getResultText()).showInformation();
        PageNavigator.refreshAllWindows();
    }

    @FXML
    private void redoAction() {
        ActionResponseView responseView = State.getActionResolver().executeRedo(null);
        Notifications.create().title("Redo").text(responseView.getResultText()).showInformation();
        PageNavigator.refreshAllWindows();
    }

    @FXML
    private void refreshPage() {
        ActionResponseView responseView = State.getActionResolver().getUndo();
        undoButton.setDisable(!responseView.isCanUndo());
        redoButton.setDisable(!responseView.isCanRedo());
    }

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
        PageNavigator.loadPage(Page.LANDING, mainController);
    }


    public void exit() {
        if (State.getUiType() == UiType.TOUCH) {
            MultitouchHandler.removePane(mainController.getPane());
        } else {
            Stage stage = (Stage) mainController.getStage().getScene().getWindow();
            stage.close();
        }
    }
}
