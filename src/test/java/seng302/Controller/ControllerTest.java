package seng302.Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import seng302.Utilities.View.Page;

import org.junit.BeforeClass;
import org.testfx.framework.junit.ApplicationTest;

public abstract class ControllerTest extends ApplicationTest {

    protected MainController mainController;
    protected SubController pageController;
    protected Node pageNode;

    @BeforeClass
    public static void initialise() {
        String disableHeadless = System.getProperty("java.disable.headless");
        if (disableHeadless == null || disableHeadless.isEmpty()) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Load main pane and controller
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource(Page.MAIN.getPath()));
        Pane mainPane = mainLoader.load();
        mainController = mainLoader.getController();

        initState();

        // Load page's node and controller
        FXMLLoader pageLoader = new FXMLLoader(getClass().getResource(getPage().getPath()));
        pageNode = pageLoader.load();
        pageController = pageLoader.getController();

        // Setup main controller and set stage's scene to its main pane, then show stage
        mainController.setStage(stage);
        stage.setScene(new Scene(mainPane));
        stage.show();

        // Setup both main and page controller
        pageController.setup(mainController);
        mainController.setPage(getPage(), pageNode);
    }

    protected abstract Page getPage();

    protected abstract void initState();
}
