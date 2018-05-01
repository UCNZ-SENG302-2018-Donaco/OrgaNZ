package seng302.Utilities.View;

/**
 * An enum to represent pages in the GUI application.
 * Includes a link to the FXML file for each page.
 */
public enum Page {
    MAIN("/fxml/main.fxml"),
    LANDING("/fxml/landing.fxml"),
    LOGIN_CLIENT("/fxml/login_client.fxml"),
    CREATE_CLIENT("/fxml/create_client.fxml"),
    SIDEBAR("/fxml/sidebar.fxml"),
    VIEW_CLIENT("/fxml/view_client.fxml"),
    VIEW_CLINICIAN("/fxml/view_clinician.fxml"),
    REGISTER_ORGAN_DONATIONS("/fxml/register_organ_donation.fxml"),
    REQUEST_ORGANS("/fxml/request_organs.fxml"),
    HISTORY("/fxml/history.fxml"),
    SEARCH("/fxml/search_clients.fxml"),
    TRANSPLANTS("/fxml/transplants.fxml"),
    LOGIN_CLINICIAN("/fxml/login_clinician.fxml"),
    CREATE_CLINICIAN("/fxml/create_clinician.fxml"),
    VIEW_MEDICATIONS("/fxml/view_medications.fxml"),
    VIEW_MEDICAL_HISTORY("/fxml/view_medical_history.fxml");

    private String path;

    Page(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
