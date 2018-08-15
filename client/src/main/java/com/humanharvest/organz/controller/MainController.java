package com.humanharvest.organz.controller;

import com.humanharvest.organz.MultitouchHandler;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main controller class for the application window.
 */
public class MainController {

    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    private Stage stage;
    private Page currentPage;
    private Pane pane;
    private WindowContext windowContext;
    private String title;
    private SidebarController sidebarController;
    private MenuBarController menuBarController;
    private SubController subController;

    /**
     * Holder of a switchable page.
     */
    @FXML
    private StackPane pageHolder;

    @FXML
    public void initialize() {
        pageHolder.getStyleClass().add("window");
    }

    public Stage getStage() {
        return stage;
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

    public void setPane(Pane pane) {
        this.pane = pane;
    }

    public Pane getPane() {
        return pane;
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
        windowContext = context;
    }

    void resetWindowContext() {
        windowContext = WindowContext.defaultContext();
    }

    /**
     * Closes the window.
     */
    @FXML
    void closeWindow() {
        stage.close();
        if (State.getUiType() == State.UiType.TOUCH) {
            MultitouchHandler.removePane(pane);
        }
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
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Couldn't load sidebar from fxml file.", e);
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
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Couldn't load sidebar from fxml file.", e);
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
        refreshNavigation();
    }

    /**
     * Refreshes only the navigation bar
     */
    public void refreshNavigation() {
        if (menuBarController != null) {
            menuBarController.refresh();
        } else if (sidebarController != null) {
            sidebarController.refresh();
        }
    }

    public String getTitle() {return title;}

    /**
     * Sets the title of the window to the given text.
     * @param title The new title of the window.
     */
    public void setTitle(String title) {
        this.title = title;
        updateTitle();
    }

    /**
     * Updates the title of the window, inserting an asterisk if there are unsaved changes.
     */
    private void updateTitle() {
        if (State.isUnsavedChanges()) {
            stage.setTitle("*" + title);
        } else {
            stage.setTitle(title);
        }
    }
}
