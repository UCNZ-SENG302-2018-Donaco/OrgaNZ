package seng302.Utilities;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
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

    public static void loadPage(Page page, MainController controller) {
        try {
            FXMLLoader loader = new FXMLLoader(PageNavigator.class.getResource(page.getPath()));
            Node loadedPage = loader.load();
            SubController subController = loader.getController();
            subController.setMainController(controller);
            controller.setPage(loadedPage);
            controller.setCurrentPage(page);
        } catch (IOException e) {
            // TODO probably do better error handling than this
            e.printStackTrace();
        }
    }

	public static void refreshPage(MainController controller) {
        try {
            FXMLLoader loader = new FXMLLoader(PageNavigator.class.getResource(controller.getCurrentPage().getPath()));
            Node loadedPage = loader.load();
            SubController subController = loader.getController();
            subController.setMainController(controller);
            controller.setPage(loadedPage);
        } catch (IOException e) {
            // TODO probably do better error handling than this
            e.printStackTrace();
        }
    }

    public static void openNewWindow(Page page) throws IOException {
		Stage newStage = new Stage();
		newStage.setTitle("Organ Donor Management System");

		FXMLLoader loader = new FXMLLoader();
		Pane mainPane = loader.load(PageNavigator.class.getResourceAsStream(Page.MAIN.getPath()));
		MainController mainController = loader.getController();
		mainController.setStage(newStage);

		newStage.setScene(new Scene(mainPane));
		newStage.show();

		loadPage(page, mainController);
	}

    public static void openNewWindow(Page page, String pageParam, Object value) throws IOException {
        Stage newStage = new Stage();
        newStage.setTitle("Organ Donor Management System");

        FXMLLoader loader = new FXMLLoader();
        Pane mainPane = loader.load(PageNavigator.class.getResourceAsStream(Page.MAIN.getPath()));
        MainController mainController = loader.getController();
        mainController.setStage(newStage);
        mainController.setPageParam(pageParam, value);

        newStage.setScene(new Scene(mainPane));
        newStage.show();

        loadPage(page, mainController);
    }

	public static Optional<ButtonType> showAlert(Alert.AlertType alertType, String title, String bodyText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(bodyText);
        return alert.showAndWait();
    }
}
