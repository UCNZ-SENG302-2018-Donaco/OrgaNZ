package seng302.Utilities;

public enum Page {
	MAIN("/fxml/main.fxml"),
	LANDING("/fxml/landing.fxml"),
	LOGIN("/fxml/login.fxml"),
	CREATE_USER("/fxml/create_donor.fxml"),
	SIDEBAR("/fxml/sidebar.fxml"),
	CLINICIAN_SIDEBAR("/fxml/clinician_sidebar.fxml"),
	VIEW_DONOR("/fxml/view_donor.fxml"),
	REGISTER_ORGANS("/fxml/register_organs.fxml"),
	HISTORY("/fxml/history.fxml"),
	SEARCH("/fxml/search_donors.fxml");

	private String path;

	Page(String path) {
		this.path = path;
	}

	public String getPath() {
		return this.path;
	}
}
