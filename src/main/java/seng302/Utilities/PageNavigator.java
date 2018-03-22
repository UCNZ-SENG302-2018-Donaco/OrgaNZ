package seng302.Utilities;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import seng302.AppUI;
import seng302.Controller.MainController;
import seng302.Controller.SubController;

import java.io.IOException;
import java.util.Optional;

/**
 * Utility class for controlling navigation between pages.
 *
 * All methods on the navigator are static to facilitate simple access from anywhere in the application.
 */
public class PageNavigator {
	/** The main application layout controller. */
	private static MainController mainController;
	private static String currentFxmlPath;

	/**
	 * Stores the main controller for later use in navigation tasks.
	 * @param mainController The main application layout controller.
	 */
	public static void setMainController(MainController mainController) {
		PageNavigator.mainController = mainController;
	}

    public static void loadPage(String fxmlPath, MainController controller) {
        try {
            currentFxmlPath = fxmlPath;
            FXMLLoader loader = new FXMLLoader(PageNavigator.class.getResource(currentFxmlPath));
            Node loadedPage = loader.load();
            SubController subController = loader.getController();
            subController.setMainController(controller);
            controller.setPage(loadedPage);
        } catch (IOException e) {
            // TODO probably do better error handling than this
            e.printStackTrace();
        }
    }

	public static void refreshPage() {
        try {
            mainController.setPage(FXMLLoader.load(PageNavigator.class.getResource(currentFxmlPath)));
        } catch (IOException e) {
            // TODO probably do better error handling than this
            e.printStackTrace();
        }
    }

    public static void openNewWindow(String fxmlPath) throws IOException {
		Stage newStage = new Stage();
		newStage.setTitle("Organ Donor Management System");

		FXMLLoader loader = new FXMLLoader();
		Pane mainPane = (Pane) loader.load(PageNavigator.class.getResourceAsStream(Page.MAIN.getPath()));
		MainController mainController = loader.getController();
		mainController.setStage(newStage);

		Pane newPane = FXMLLoader.load(PageNavigator.class.getResource(fxmlPath));
		newStage.setScene(new Scene(mainPane));
		mainController.setPage(newPane);
		newStage.show();
	}

	public static Optional<ButtonType> showAlert(Alert.AlertType alertType, String title, String bodyText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(bodyText);
        return alert.showAndWait();
    }
}
