package seng302.Utilities.View;

/**
 * An enum to represent pages in the GUI application.
 * Includes a link to the FXML file for each page.
 */
public enum Page {
    MAIN("/fxml/main.fxml"),
    LANDING("/fxml/landing.fxml"),
    LOGIN_PERSON("/fxml/login_person.fxml"),
    CREATE_PERSON("/fxml/create_person.fxml"),
    SIDEBAR("/fxml/sidebar.fxml"),
    VIEW_PERSON("/fxml/view_person.fxml"),
    VIEW_CLINICIAN("/fxml/view_clinician.fxml"),
    REGISTER_ORGAN_DONATIONS("/fxml/register_organ_donation.fxml"),
    HISTORY("/fxml/history.fxml"),
    SEARCH("/fxml/search_persons.fxml"),
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
