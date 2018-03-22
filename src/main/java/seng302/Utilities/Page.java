package seng302.Utilities;

/**
 * An enum to represent pages in the GUI application.
 * Includes a link to the FXML file for each page.
 */
public enum Page {
	MAIN("/fxml/main.fxml"),
	LANDING("/fxml/landing.fxml"),
	LOGIN("/fxml/login.fxml"),
	CREATE_USER("/fxml/create_donor.fxml"),
	SIDEBAR("/fxml/sidebar.fxml"),
	CLINICIAN_SIDEBAR("/fxml/clinician_sidebar.fxml"),
	VIEW_DONOR("/fxml/view_donor.fxml"),
	VIEW_CLINICIAN("/fxml/view_clinician.fxml"),
	REGISTER_ORGANS("/fxml/register_organs.fxml"),
	HISTORY("/fxml/history.fxml"),
	SEARCH("/fxml/search_donors.fxml"),
	CLINICIAN_LOGIN("/fxml/clinician_login.fxml"),
	CREATE_CLINICIAN("/fxml/create_clinician.fxml");

	private String path;

	Page(String path) {
		this.path = path;
	}

	public String getPath() {
		return this.path;
	}
}
