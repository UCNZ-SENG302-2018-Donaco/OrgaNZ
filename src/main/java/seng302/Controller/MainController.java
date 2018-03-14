package seng302.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Main controller class for the application window.
 *
 */
public class MainController {

/*----------------------------------------------------
Initial Pane
 ---------------------------------------------------*/

	@FXML
	public Pane InitialPane;
	@FXML
	public Button createUserBtn;
	@FXML
	public Button loginBtn;
	@FXML
	public Label initialLbl;

/*----------------------------------------------------
Create User Pane
---------------------------------------------------*/

	@FXML
	public Pane CreateUserPane;


	/**
	 *  Allows the switching between panes to that the Create User Pane is shown
	 */
	public void viewCreateUserPane() {
		InitialPane.setVisible(false);
		InitialPane.setDisable(true);

		CreateUserPane.setVisible(true);
		CreateUserPane.setDisable(false);

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
