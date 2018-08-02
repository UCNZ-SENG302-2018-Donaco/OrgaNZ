package com.humanharvest.organz;

import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.state.State.DataStorageType;
import com.humanharvest.organz.utilities.LoggerSetup;
import com.humanharvest.organz.utilities.view.*;
import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.tuiofx.Configuration;
import org.tuiofx.TuioFX;

import java.io.IOException;
import java.util.logging.Level;

/**
 * The main class that runs the JavaFX GUI.
 */
public class AppTUIO extends Application {

    public static final Pane root = new Pane();

    public static void main(String[] args) {
        TuioFX.enableJavaFXTouchProperties();
        launch(args);
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
     * Starts the JavaFX GUI. Sets up the main stage and initialises the state of the system.
     * Loads from the save file or creates one if one does not yet exist.
     *
     * @param primaryStage The stage given by the JavaFX launcher.
     * @throws IOException If the save file cannot be loaded/created.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        LoggerSetup.setup(Level.INFO);
        PageNavigator.setPageNavigator(new PageNavigatorTouch());

        final Scene scene = new Scene(root, 1920, 1080);

        TuioFX tuioFX = new TuioFX(primaryStage, Configuration.debug());
        //Instead of tuioFX.enableMTWidgets(true);
        // We set our own stylesheet that contains less style changes but still loads the skins required for multi touch
        Application.setUserAgentStylesheet("MODENA");
        StyleManager.getInstance().addUserAgentStylesheet("/css/multifocus.css");
        tuioFX.start();

        Pane pane = loadMainPane(new Stage());
//        pane.setMaxWidth(600);
//        pane.setMaxHeight(800);
        TuioFXUtils.setupPaneWithTouchFeatures(pane);

        root.getChildren().add(pane);


        scene.getStylesheets().add(AppUI.class.getResource("/css/touch.css").toExternalForm());

        primaryStage.setTitle("Test");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

        State.init(DataStorageType.REST);

        if (System.getenv("HOST") != null) {
            State.setBaseUri(System.getenv("HOST"));
        }
    }

    private static double withinRange(double min, double max, double value) {
        return Math.min(Math.max(value, min), max);
    }
}
