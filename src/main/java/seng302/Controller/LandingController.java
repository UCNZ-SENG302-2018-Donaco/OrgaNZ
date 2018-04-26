package seng302.Controller;

import javafx.fxml.FXML;

import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;

/**
 * Controls the Landing Screen View.
 */
public class LandingController extends SubController {

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
     * Redirects the GUI to create donor page
     */
    @FXML
    private void goToCreateDonor() {
        PageNavigator.loadPage(Page.CREATE_DONOR, mainController);
    }

    /**
     * Redirects the GUI to donor login page
     */
    @FXML
    private void goToDonorLogin() {
        PageNavigator.loadPage(Page.LOGIN_DONOR, mainController);
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
