package com.humanharvest.organz.controller;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import com.jfoenix.controls.JFXHamburger;

public class TouchActionsBarController extends SubController {

    @FXML
    private JFXHamburger hamburger;
    private SidebarController sidebarController;

    @Override
    public void setup(MainController controller) {

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

    }


}
