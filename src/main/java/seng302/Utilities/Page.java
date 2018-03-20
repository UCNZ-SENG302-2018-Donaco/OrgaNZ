package seng302.Utilities;

public enum Page {
	MAIN("/fxml/main.fxml"),
	LANDING("/fxml/landing.fxml"),
	LOGIN("/fxml/login.fxml"),
	CREATE_USER("/fxml/create_user.fxml"),
	SIDEBAR("/fxml/sidebar.fxml"),
	VIEW_DONOR("/fxml/view_donor.fxml"),
	REGISTER_ORGANS("/fxml/register_organs.fxml"),
	HISTORY("/fxml/history.fxml");

	private String path;

	Page(String path) {
		this.path = path;
	}

	public String getPath() {
		return this.path;
	}
}
