package seng302.Utilities;

public enum Page {
	MAIN("/fxml/main.fxml"),
	LANDING("/fxml/landing.fxml"),
	LOGIN("/fxml/login.fxml"),
	CREATE_USER("/fxml/create_user.fxml");

	private String path;

	Page(String path) {
		this.path = path;
	}

	public String getPath() {
		return this.path;
	}
}
