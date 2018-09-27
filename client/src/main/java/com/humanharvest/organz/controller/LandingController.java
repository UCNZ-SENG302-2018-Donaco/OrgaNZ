package com.humanharvest.organz.controller;

import javafx.fxml.FXML;

import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;

/**
 * Controls the Landing Screen View.
 */
public class LandingController extends SubController {

    /**
     * Override so we can set the page title.
     *
     * @param mainController The MainController
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Home");
    }

    /**
     * Does not do anything as page doesn't render anything that could have changed
     */
    @Override
    public void refresh() {
        //Do not need to do anything as page doesn't render anything that could have changed
    }

    /**
     * Redirects the GUI to create client page
     */
    @FXML
    private void goToCreateClient() {
        PageNavigator.loadPage(Page.CREATE_CLIENT, mainController);
    }

    /**
     * Redirects the GUI to client login page
     */
    @FXML
    private void goToClientLogin() {
        PageNavigator.loadPage(Page.LOGIN_CLIENT, mainController);
    }

    /**
     * Redirects the GUI to staff login page
     */
    @FXML
    private void goToStaffLogin() {
        PageNavigator.loadPage(Page.LOGIN_STAFF, mainController);
    }

}
