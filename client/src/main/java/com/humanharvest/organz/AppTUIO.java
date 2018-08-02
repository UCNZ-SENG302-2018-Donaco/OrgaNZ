package com.humanharvest.organz;

import TUIO.TuioCursor;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.state.State.DataStorageType;
import com.humanharvest.organz.utilities.LoggerSetup;
import com.humanharvest.organz.utilities.view.*;
import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.tuiofx.Configuration;
import org.tuiofx.TuioFX;
import org.tuiofx.internal.base.TuioInputService;

import java.io.IOException;
import java.lang.reflect.Field;
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


//        primaryStage.addEventFilter(MouseEvent.ANY, event -> {
//            event.consume();
//
//            EventType<TouchEvent> type = null;
//            TouchPoint.State state = null;
//            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
//                type = TouchEvent.TOUCH_PRESSED;
//                state = TouchPoint.State.PRESSED;
//            } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
//                type = TouchEvent.TOUCH_RELEASED;
//                state = TouchPoint.State.RELEASED;
//            } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
//                type = TouchEvent.TOUCH_MOVED;
//                state = TouchPoint.State.MOVED;
//            } else if (event.getEventType() == MouseEvent.MOUSE_MOVED) {
////                System.out.println(event);
//            }
//
//            if (type != null) {
//                TouchPoint touchPoint = new TouchPoint(1,
//                        state,
//                        event.getX(),
//                        event.getY(),
//                        event.getScreenX(),
//                        event.getScreenY(),
//                        event.getTarget(),
//                        event.getPickResult());
//                primaryStage.fireEvent(new TouchEvent(
//                        event.getSource(),
//                        event.getTarget(),
//                        type,
//                        touchPoint,
//                        Collections.singletonList(touchPoint),
//                        1,
//                        event.isShiftDown(),
//                        event.isControlDown(),
//                        event.isAltDown(),
//                        event.isMetaDown()));
//            }
//        });
//
//        primaryStage.addEventFilter(TouchEvent.ANY, System.out::println);

//        TouchHandler touchHandler = tuioFX.getTouchHandler();



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

//        initialiseFakeTUIO(tuioFX, primaryStage);

        State.init(DataStorageType.REST);

        if (System.getenv("HOST") != null) {
            State.setBaseUri(System.getenv("HOST"));
        }
    }

    private static void initialiseFakeTUIO(TuioFX tuioFX, Stage primaryStage) {
        TuioInputService inputService = null;

        try {
            Field inputServiceField = tuioFX.getClass().getDeclaredField("tuioInputService");
            inputServiceField.setAccessible(true);
            inputService = (TuioInputService)inputServiceField.get(tuioFX);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        primaryStage.addEventFilter(MouseEvent.ANY, new TuioMouseEventFilter(inputService));
    }

    private static double withinRange(double min, double max, double value) {
        return Math.min(Math.max(value, min), max);
    }

    private static class TuioMouseEventFilter implements EventHandler<MouseEvent> {

        private final TuioInputService inputService;
        int sessionId = 0;

        public TuioMouseEventFilter(TuioInputService inputService) {
            this.inputService = inputService;
        }

        @Override
        public void handle(MouseEvent event) {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                inputService.addTuioCursor(new TuioCursor(0, sessionId++, (float)event.getX(), (float)event.getY()));

            } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                inputService.updateTuioCursor(new TuioCursor(0, sessionId++, (float)event.getX(), (float)event.getY()));

            } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                inputService.removeTuioCursor(new TuioCursor(0, sessionId++, (float)event.getX(), (float)event.getY()));
            }
        }
    }
}
