package seng302.Utilities.View;

/**
 * An enum to represent pages in the GUI application.
 * Includes a link to the FXML file for each page.
 */
public enum Page {
    MAIN("/fxml/main.fxml"),
    LANDING("/fxml/landing.fxml"),
    LOGIN_DONOR("/fxml/login_donor.fxml"),
    CREATE_DONOR("/fxml/create_donor.fxml"),
    SIDEBAR("/fxml/sidebar.fxml"),
    VIEW_DONOR("/fxml/view_donor.fxml"),
    VIEW_CLINICIAN("/fxml/view_clinician.fxml"),
    REGISTER_ORGANS("/fxml/register_organs.fxml"),
    HISTORY("/fxml/history.fxml"),
    SEARCH("/fxml/search_donors.fxml"),
    LOGIN_CLINICIAN("/fxml/login_clinician.fxml"),
    CREATE_CLINICIAN("/fxml/create_clinician.fxml"),
    VIEW_MEDICATIONS("/fxml/view_medications.fxml");

    private String path;

    Page(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
