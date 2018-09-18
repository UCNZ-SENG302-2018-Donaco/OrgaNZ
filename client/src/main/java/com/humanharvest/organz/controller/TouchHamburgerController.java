package com.humanharvest.organz.controller;

import javafx.fxml.FXML;

import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXDrawer;

public class TouchHamburgerController extends SubController {

    @FXML
    private JFXHamburger hamburger;

    /**
     * Redirects the GUI to the Register Organs page.
     */
    @FXML
    private void goToRegisterOrganDonation() {
        PageNavigator.loadPage(Page.REGISTER_ORGAN_DONATIONS, mainController);
    }

}
