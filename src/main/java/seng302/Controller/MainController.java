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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Main controller class for the application window.
 *
 */
public class MainController {
	private Stage stage;
    private String currentFXMLPath;
    private Map<String, Object> pageContext = new HashMap<>();

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

    public String getCurrentFXMLPath() {
        return currentFXMLPath;
    }

    public void setCurrentFXMLPath(String currentFXMLPath) {
        this.currentFXMLPath = currentFXMLPath;
    }

    public Object getPageParam(String key) {
        return pageContext.get(key);
    }

    public void setPageParam(String key, Object value) {
        pageContext.put(key, value);
    }

    public void removePageParam(String key) {
        pageContext.remove(key);
    }

    public void clearPageParams() {
        pageContext.clear();
    }
}
