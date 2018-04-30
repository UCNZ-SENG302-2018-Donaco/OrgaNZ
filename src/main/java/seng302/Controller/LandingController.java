package seng302.Controller;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;

/**
 * Controls the Landing Screen View.
 */
public class LandingController extends SubController {

    @FXML
    Button createClientButton, loginClientButton, createClinicianButton, loginClinicianButton;

    /**
     * Override so we can set the page title.
     * @param mainController The MainController
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Home");
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
     * Redirects the GUI to clinician login page
     */
    @FXML
    private void goToClinicianLogin() {
        PageNavigator.loadPage(Page.LOGIN_CLINICIAN, mainController);
    }

    /**
     * Redirects the GUI to create clinician page
     */
    @FXML
    private void goToCreateClinician() {
        PageNavigator.loadPage(Page.CREATE_CLINICIAN, mainController);
    }
}
