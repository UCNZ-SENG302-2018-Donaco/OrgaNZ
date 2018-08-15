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
import org.tuiofx.TuioFX;
import org.tuiofx.internal.base.TuioFXCanvas;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

/**
 * The main class that runs the JavaFX GUI.
 */
public class AppTUIO extends Application {
    public static final Pane root = new TuioFXCanvas();
    private static MultitouchHandler multitouchHandler;

    public static void main(String[] args) {
        TuioFX.enableJavaFXTouchProperties();
        launch(args);
    }

    /**
     * Loads a backdrop page.
     */
    private static void loadBackPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Pane backPane = loader.load(PageNavigatorTouch.class.getResourceAsStream(Page.BACKDROP.getPath()));

        TuioFXUtils.setupPaneWithTouchFeatures(backPane);
        root.getChildren().add(backPane);
    }

    /**
     * Loads the landing page as the initial page.
     */
    private static void loadMainPane() {
        MainController mainController = PageNavigator.openNewWindow();
        mainController.setWindowContext(WindowContext.defaultContext());
        PageNavigator.loadPage(Page.LANDING, mainController);
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
        State.setUiType(State.UiType.TOUCH);
        PageNavigator.setPageNavigator(new PageNavigatorTouch());

        Scene scene = new Scene(root, 1920, 1080);

        // Instead of tuioFX.enableMTWidgets(true)
        // We set our own stylesheet that contains less style changes but still loads the skins required for multi touch
        Application.setUserAgentStylesheet("MODENA");
        StyleManager.getInstance().addUserAgentStylesheet("/css/multifocus.css");

        loadBackPane();
        multitouchHandler = new MultitouchHandler(root);

        loadMainPane();

        scene.getStylesheets().add(AppUI.class.getResource("/css/touch.css").toExternalForm());

        primaryStage.setTitle("Test");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
//        primaryStage.setWidth(1024);
//        primaryStage.setHeight(768);

        State.init(DataStorageType.REST);
        State.setPrimaryStage(primaryStage);

        Map<String, String> parameters = getParameters().getNamed();

        if (parameters.containsKey("host")) {
            State.setBaseUri(parameters.get("host"));
        } else if (System.getenv("HOST") != null) {
            State.setBaseUri(System.getenv("HOST"));
        }
    }

    public static Pane getRoot() {
        return root;
    }

    public static MultitouchHandler getMultitouchHandler() {
        return multitouchHandler;
    }
}
