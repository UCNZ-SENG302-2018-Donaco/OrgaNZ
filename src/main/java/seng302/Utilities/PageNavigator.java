package seng302.Utilities;

import javafx.fxml.FXMLLoader;
import java.io.IOException;
import seng302.Controller.MainController;

/**
 * Utility class for controlling navigation between pages.
 *
 * All methods on the navigator are static to facilitate simple access from anywhere in the application.
 */
public class PageNavigator {
	/** The main application layout controller. */
	private static MainController mainController;

	/**
	 * Stores the main controller for later use in navigation tasks.
	 * @param mainController The main application layout controller.
	 */
	public static void setMainController(MainController mainController) {
		PageNavigator.mainController = mainController;
	}

	/**
	 * @param fxmlPath the fxml file to be loaded.
	 */
	public static void loadPage(String fxmlPath) {
		try {
			mainController.setPage(FXMLLoader.load(PageNavigator.class.getResource(fxmlPath)));
		} catch (IOException e) {
			// TODO probably do better error handling than this
			e.printStackTrace();
		}
	}
}
