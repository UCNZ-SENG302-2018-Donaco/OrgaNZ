package seng302.Controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import seng302.State.State;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

/**
 * Main controller class for the application window.
 */
public class MainController {

    private Stage stage;
    private Page currentPage;
    private WindowContext windowContext;
    private String windowTitle;
    private SidebarController sidebarController;
    private menuBarController menuBarController;
    private SubController subController;

    /**
     * Holder of a switchable page.
     */
    @FXML
    private StackPane pageHolder;

    public Stage getStage() {
        return this.stage;
    }

    public Page getCurrentPage() {
        return currentPage;
    }

    WindowContext getWindowContext() {
        return windowContext;
    }

    public void setStage(Stage stage) {
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

    void resetWindowContext() {
        this.windowContext = WindowContext.defaultContext();
    }

    /**
     * Closes the window.
     */
    @FXML
    void closeWindow() {
        stage.close();
    }

    /**
     * Method that can be called from other controllers to load the sidebar into that page.
     * Will set the sidebar as the child of the pane given.
     * @param sidebarPane The container pane for the sidebar, given by the importer.
     */
    public void loadSidebar(Pane sidebarPane) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Page.SIDEBAR.getPath()));
            VBox sidebar = loader.load();
            sidebarController = loader.getController();
            sidebarController.setup(this);
            sidebarPane.getChildren().setAll(sidebar);
        } catch (IOException exc) {
            System.err.println("Couldn't load sidebar from fxml file.");
            exc.printStackTrace();
        }
    }


    /**
     * Method that can be called from other controllers to load the sidebar into that page.
     * Will set the sidebar as the child of the pane given.
     * @param menuBarPane The container pane for the menu bar, given by the importer.
     */
    public void loadMenuBar(Pane menuBarPane) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Page.MENU_BAR.getPath()));
            HBox menuBar = loader.load();
            menuBarController = loader.getController();
            menuBarController.setup(this);
            menuBarPane.getChildren().setAll(menuBar);
        } catch (IOException exc) {
            System.err.println("Couldn't load sidebar from fxml file.");
            exc.printStackTrace();
        }
    }

    public void setSubController(SubController subController) {
        this.subController = subController;
    }

    /**
     * Refreshes the title of the window, and calls the current page's {@link SubController#refresh()} method.
     */
    public void refresh() {
        updateTitle();
        subController.refresh();
        if (sidebarController != null) {
            sidebarController.refresh();
        }
    }

    public String getTitle() {return windowTitle;}

    /**
     * Sets the title of the window to the given text.
     * @param title The new title of the window.
     */
    public void setTitle(String title) {
        windowTitle = title;
        updateTitle();
    }

    /**
     * Updates the title of the window, inserting an asterisk if there are unsaved changes.
     */
    private void updateTitle() {
        if (State.isUnsavedChanges()) {
            stage.setTitle("*" + windowTitle);
        } else {
            stage.setTitle(windowTitle);
        }
    }
}
