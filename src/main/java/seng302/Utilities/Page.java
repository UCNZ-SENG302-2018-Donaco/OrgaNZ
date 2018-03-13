package seng302.Utilities;

public enum Page {
	MAIN("/fxml/main.fxml"),
	LANDING("/fxml/landing.fxml");

	private String path;

	Page(String path) {
		this.path = path;
	}

	public String getPath(Page page) {
		return page.path;
	}
}