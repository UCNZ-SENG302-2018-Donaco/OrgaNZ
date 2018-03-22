package seng302.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Main controller class for the application window.
 *
 */
public class MainController {
	private Stage stage;

	public void setStage(Stage stage){
		this.stage = stage;
	}

	public Stage getStage() {
		return this.stage;
	}

	/** Holder of a switchable page. */
	@FXML
	private StackPane pageHolder;

	/**
	 * Replaces the page displayed in the page holder with a new page.
	 * @param node the page node to be swapped in.
	 */
	public void setPage(Node node) {
		pageHolder.getChildren().setAll(node);
	}

	/**
	 * Closes the window.
	 * @param event when the exit button is clicked.
	 */
	@FXML
	private void closeWindow(ActionEvent event) {
		Platform.exit();
	}
}
