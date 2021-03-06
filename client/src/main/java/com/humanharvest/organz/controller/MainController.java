package com.humanharvest.organz.controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.state.State.UiType;
import com.humanharvest.organz.touch.MultitouchHandler;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;

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
    private MenuBarController menuBarController;
    private TouchActionsBarController touchActionsBarController;
    private SubController subController;
    private boolean isProjecting;
    private boolean isAProjection;
    /**
     * Holder of a switchable page.
     */
    @FXML
    private StackPane pageHolder;
    @FXML
    private Pane drawer;

    @FXML
    public void initialize() {
        pageHolder.getStyleClass().add("window");
        drawer.setDisable(true);
        drawer.setVisible(false);
//        drawer.setOnDrawerOpened(EventListener -> drawer.setVisible(true));
        drawer.toFront();
        pageHolder.setPickOnBounds(false);
        isProjecting = false;
    }

    public Pane getDrawer() {
        return drawer;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Page getCurrentPage() {
        return currentPage;
    }

    WindowContext getWindowContext() {
        return windowContext;
    }

    public void setWindowContext(WindowContext context) {
        windowContext = context;
    }

    public Pane getPane() {
        return pane;
    }

    public void setPane(Pane pane) {
        this.pane = pane;
    }

    /**
     * Replaces the page displayed in the page holder with a new page.
     *
     * @param page the new current Page.
     * @param node the page node to be swapped in.
     */
    public void setPage(Page page, Node node) {
        currentPage = page;
        pageHolder.getChildren().setAll(node);
//        pageHolder.setOnMouseClicked(event -> touchActionsBarController.closeSidebar(drawer));
    }

    void resetWindowContext() {
        windowContext = WindowContext.defaultContext();
    }

    /**
     * Closes the window.
     */
    @FXML
    public void closeWindow() {
        stage.close();
        if (State.getUiType() == State.UiType.TOUCH) {
            MultitouchHandler.removePane(pane);
        }
        State.deleteMainController(this);
    }

    /**
     * Shows the window.
     */
    public void showWindow() {
        State.addMainController(this);
        if (State.getUiType() == State.UiType.TOUCH) {
            MultitouchHandler.addPane(pane);
        }
    }


    /**
     * Method that can be called from other controllers to load the sidebar into that page.
     * Will set the sidebar as the child of the pane given.
     */
    private void loadSidebar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Page.SIDEBAR.getPath()));
            VBox sidebar = loader.load();
            SidebarController sidebarController = loader.getController();
            sidebarController.setup(this);
            drawer.getChildren().setAll(sidebar);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Couldn't load sidebar from fxml file.", e);
        }
    }

    /**
     * Method that can be called from other controllers to load the sidebar into that page.
     * Will set the sidebar as the child of the pane given.
     *
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

    /**
     * Loads the touch actions bar and uses the sidebars fxml as the content
     * @param pane the pane to load the touch actions bar for
     */
    public void loadTouchActionsBar(Pane pane) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Page.TOUCH_ACTIONS_BAR.getPath()));
            HBox touchActionBar = loader.load();
            touchActionsBarController = loader.getController();
            touchActionsBarController.setup(this);
            pane.getChildren().setAll(touchActionBar);
            loadSidebar();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Couldn't load touch actions bar from fxml file.", e);
        }
    }

    /**
     * Closes the touch actions bar
     * (So that it can be closed once navigated to a new page)
     */
    public void closeTouchActionsBar() {
        touchActionsBarController.closeSidebar(drawer);
    }

    /**
     * Sets up the navigation type for the given pane. The menu bar is given for admins/clinicians using the
     * desktop application. Otherwise a touch actions bar is used.
     * @param pane type of pane to setup navigation for
     */
    public void loadNavigation(Pane pane) {
        if (State.getUiType() == UiType.TOUCH || State.getSession().getLoggedInUserType() == UserType.CLIENT) {
            loadTouchActionsBar(pane);
        } else {
            loadMenuBar(pane);
        }
    }

    public SubController getSubController() {
        return subController;
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
        } else if (touchActionsBarController != null) {
            touchActionsBarController.refresh();
            loadSidebar();
        }
    }

    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the window to the given text.
     *
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
        String titleValue;
        if (State.isUnsavedChanges()) {
            titleValue = "*" + title;
        } else {
            titleValue = title;
        }

        if (touchActionsBarController != null) {
            touchActionsBarController.setTitle(titleValue);
        }
        stage.setTitle(titleValue);
    }

    public List<String> getStyles() {
        return pageHolder.getStyleClass();
    }

    public boolean isProjecting() {
        return isProjecting;
    }

    public void setProjecting(boolean projecting) {
        isProjecting = projecting;
    }

    public boolean isAProjection() {
        return isAProjection;
    }

    public void setIsAProjection(boolean aProjection) {
        isAProjection = aProjection;
    }
}
