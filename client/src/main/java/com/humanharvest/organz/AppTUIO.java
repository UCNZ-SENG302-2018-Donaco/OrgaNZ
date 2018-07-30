package com.humanharvest.organz;

import java.io.IOException;
import java.util.logging.Level;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.state.State.DataStorageType;
import com.humanharvest.organz.utilities.LoggerSetup;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext;
import org.tuiofx.Configuration;
import org.tuiofx.TuioFX;

/**
 * The main class that runs the JavaFX GUI.
 */
public class AppTUIO extends Application {

    private static Stage window;
    private TitledPane pane = null;
    public static final Pane root = new Pane();
    public static double startDragX;
    public static double startDragY;

    public static Stage getWindow() {
        return window;
    }

    /**
     * Starts the JavaFX GUI. Sets up the main stage and initialises the state of the system.
     * Loads from the save file or creates one if one does not yet exist.
     * @param primaryStage The stage given by the JavaFX launcher.
     * @throws IOException If the save file cannot be loaded/created.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        LoggerSetup.setup(Level.INFO);

        final Scene scene = new Scene(root, 1920, 1080);

        pane = new TitledPane("Test", loadMainPane(new Stage()));
        pane.getProperties().put("focusArea", "true");

        root.getChildren().add(pane);

//        pane.setStyle("   -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 10, 10);"
//                + "-fx-background-color: derive(-fx-background,0%);");

        pane.setOnMousePressed(event -> {
            pane.toFront();
            startDragX = event.getSceneX();
            startDragY = event.getSceneY();
        });

        pane.setOnMouseDragged(event -> {
            pane.toFront();
            //TODO: Not hardcode res and not have static startDrag vars
            pane.setTranslateX(withinRange(0, 1920 - pane.getWidth(), pane.getTranslateX() + event.getSceneX() - startDragX));
            pane.setTranslateY(withinRange(0, 1080 - pane.getHeight(), pane.getTranslateY() + event.getSceneY() - startDragY));
            startDragX = event.getSceneX();
            startDragY = event.getSceneY();
        });

        pane.setOnScroll(event -> {
            pane.toFront();
            //TODO: not hardcode res and not have static startDrag vars
            pane.setTranslateX(withinRange(0, 1920 - pane.getWidth(), pane.getTranslateX() + event.getDeltaX()));
            pane.setTranslateY(withinRange(0, 1080 - pane.getHeight(), pane.getTranslateY() + event.getDeltaY()));

        });
        pane.setOnRotate(event -> {
            pane.toFront();
            pane.setRotate(pane.getRotate() + event.getAngle());
        });



        TuioFX tuioFX = new TuioFX(primaryStage, Configuration.debug());
        tuioFX.enableMTWidgets(true);
        tuioFX.start();

        primaryStage.setTitle("Test");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

//        primaryStage.setMinHeight(639);
//        primaryStage.setMinWidth(1016);

        State.init(DataStorageType.REST);

        if (System.getenv("HOST") != null) {
            State.setBaseUri(System.getenv("HOST"));
        }
    }

    /**
     * Loads the main FXML. Sets up the page-switching PageNavigator. Loads the landing page as the initial page.
     * @param stage The stage to set the window to
     * @return The loaded pane.
     * @throws IOException Thrown if the pane could not be loaded.
     */
    private Pane loadMainPane(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();

        Pane mainPane = loader.load(getClass().getResourceAsStream(Page.MAIN.getPath()));
        MainController mainController = loader.getController();
        mainController.setStage(stage);
        mainController.setWindowContext(WindowContext.defaultContext());

        State.addMainController(mainController);

        PageNavigator.loadPage(Page.LANDING, mainController);

        return mainPane;
    }

    /**
     * Creates the main application scene.
     * @param mainPane The main application layout.
     * @return Returns the created scene.
     */
    private static Scene createScene(Pane mainPane) {
        Scene scene = new Scene(mainPane);
        addCss(scene);
        return scene;
    }

    public static void addCss(Scene scene) {
        scene.getStylesheets().add(AppUI.class.getResource("/css/validation.css").toExternalForm());
    }

    public static void main(String[] args) {
        TuioFX.enableJavaFXTouchProperties();
        launch(args);
    }

    private static double withinRange(double min, double max, double value) {
        return Math.min(Math.max(value, min), max);
    }
}
