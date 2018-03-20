package seng302;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import seng302.Controller.MainController;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;

import java.io.IOException;
import java.time.LocalDate;

public class AppUI extends Application {
	private static Stage window;

	public static Stage getWindow() {
		return AppUI.window;
	}

	@Override
	public void start(Stage stage) throws Exception {
		window = stage;
		stage.setTitle("Organ Donor Management System");
		stage.setScene(createScene(loadMainPane()));
		stage.show();

		State.init();

        // TODO DELETE TEST DATA
		LocalDate d = LocalDate.of(1997,8,22);
		int uid = State.getDonorManager().getUid();
		Donor deleteme = new Donor("James", "v", "Toohey", d, uid);
		State.getDonorManager().addDonor(deleteme);
		// TODO DELETE TEST DATA
	}

	/**
	 * Loads the main FXML. Sets up the page-switching PageNavigator. Loads the landing page as the initial page.
	 * @return The loaded pane.
	 * @throws IOException Thrown if the pane could not be loaded.
	 */
	private Pane loadMainPane() throws IOException {
		FXMLLoader loader = new FXMLLoader();

		Pane mainPane = (Pane) loader.load(getClass().getResourceAsStream(Page.MAIN.getPath()));
		MainController mainController = loader.getController();

		PageNavigator.setMainController(mainController);
		PageNavigator.loadPage(Page.LANDING.getPath());

		return mainPane;
	}

	/**
	 * Creates the main application scene.
	 * @param mainPane The main application layout.
	 * @return Returns the created scene.
	 */
	private Scene createScene(Pane mainPane) {
		Scene scene = new Scene(mainPane);
		// TODO Add CSS files for styling.
		// scene.getStylesheets().setAll(getClass().getResource("page.css").toExternalForm());
		return scene;
	}
}
