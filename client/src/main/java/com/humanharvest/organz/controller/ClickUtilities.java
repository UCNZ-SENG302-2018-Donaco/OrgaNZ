package com.humanharvest.organz.controller;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext;

public final class ClickUtilities {

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private ClickUtilities() {
        throw new IllegalStateException("Utility class");
    }

    public static void openClientOnDoubleClick(
            MouseEvent mouseEvent, Client client, MainController mainController) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            if (client != null) {
                MainController newMain = PageNavigator.openNewWindow(mainController);
                if (newMain != null) {
                    newMain.setWindowContext(new WindowContext.WindowContextBuilder()
                            .setAsClinicianViewClientWindow()
                            .viewClient(client)
                            .build());
                    PageNavigator.loadPage(Page.VIEW_CLIENT, newMain);
                }
            }
        }
    }
}
