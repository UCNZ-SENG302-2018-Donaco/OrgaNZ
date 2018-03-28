package seng302.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seng302.Utilities.Page;
import seng302.Utilities.WindowContext;

import java.io.IOException;

/**
 * Main controller class for the application window.
 *
 */
public class MainController {
	private Stage stage;
    private Page currentPage;
    private WindowContext windowContext;

	/** Holder of a switchable page. */
	@FXML
	private StackPane pageHolder;

    public Stage getStage() {
        return this.stage;
    }

    public Page getCurrentPage() {
        return currentPage;
    }

    public WindowContext getWindowContext() {
        return windowContext;
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

	/**
	 * Replaces the page displayed in the page holder with a new page.
     * @param page the new current Page.
	 * @param node the page node to be swapped in.
	 */
	public void setPage(Page page, Node node) {
        currentPage = page;
		pageHolder.getChildren().setAll(node);
	}

	public void setWindowContext(WindowContext context) {
	    this.windowContext = context;
    }

    public void resetWindowContext() {
	    this.windowContext = WindowContext.defaultContext();
    }

	/**
	 * Closes the window.
	 * @param event when the exit button is clicked.
	 */
	@FXML
	private void closeWindow(ActionEvent event) {
		Platform.exit();
	}

    /**
     * Method that can be called from other controllers to load the sidebar into that page.
     * Will set the sidebar as the child of the pane given.
     * @param sidebarPane The container pane for the sidebar, given by the importer.
     */
    public void loadDonorSidebar(Pane sidebarPane) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Page.SIDEBAR.getPath()));
            VBox sidebar = loader.load();
            SubController subController = loader.getController();
            subController.setMainController(this);
            sidebarPane.getChildren().setAll(sidebar);
        } catch (IOException exc) {
            System.err.println("Couldn't load sidebar from fxml file.");
            exc.printStackTrace();
        }
    }

    /**
     * Method that can be called from other controllers to load the sidebar into that page.
     * Will set the sidebar as the child of the pane given.
     * @param sidebarPane The container pane for the sidebar, given by the importer.
     */
    public void loadClinicianSidebar(Pane sidebarPane) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Page.CLINICIAN_SIDEBAR.getPath()));
            VBox sidebar = loader.load();
            SubController subController = loader.getController();
            subController.setMainController(this);
            sidebarPane.getChildren().setAll(sidebar);
        } catch (IOException exc) {
            System.err.println("Couldn't load sidebar from fxml file.");
            exc.printStackTrace();
        }
    }
}
