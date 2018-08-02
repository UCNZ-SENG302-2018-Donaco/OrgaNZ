package com.humanharvest.organz;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

import TUIO.TuioCursor;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.state.State.DataStorageType;
import com.humanharvest.organz.utilities.LoggerSetup;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.PageNavigatorTouch;
import com.humanharvest.organz.utilities.view.TuioFXUtils;
import com.humanharvest.organz.utilities.view.WindowContext;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.scene.input.PickResultChooser;
import org.tuiofx.Configuration;
import org.tuiofx.TuioFX;
import org.tuiofx.internal.base.CoordinatesMapping;
import org.tuiofx.internal.base.GestureHandler;
import org.tuiofx.internal.base.TargetSelection;
import org.tuiofx.internal.base.TouchHandler;
import org.tuiofx.internal.base.TuioFXCanvas;
import org.tuiofx.internal.base.TuioInputService;
import org.tuiofx.internal.gesture.TuioTouchPoint;
import org.tuiofx.internal.util.Util;

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

        Scene scene = new Scene(root, 1920, 1080);

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
//        primaryStage.setFullScreen(true);
        primaryStage.show();

        initialiseTUIOHook(tuioFX);
        initialiseFakeTUIO(tuioFX, primaryStage);

        State.init(DataStorageType.REST);

        if (System.getenv("HOST") != null) {
            State.setBaseUri(System.getenv("HOST"));
        }
    }

    private static void initialiseTUIOHook(TuioFX tuioFX) {
        try {
            AppTUIO.<TuioInputService>getField(tuioFX, "tuioInputService").disconnectTUIO();

            Configuration config = getField(tuioFX, "config");
            GestureHandler gestureHandler = new GestureHandler(config);
            TargetSelection targetSelection = new TargetSelectionHook(getField(tuioFX, "scene"), config);
            CoordinatesMapping coordinatesMapping = new CoordinatesMapping(getField(tuioFX, "stage"), config);
            TouchHandler touchHandler = new TouchHandler(gestureHandler, targetSelection, coordinatesMapping, config);
            TuioInputService inputService = new TuioInputServiceHook(coordinatesMapping, touchHandler, config);

            setField(tuioFX, "gestureHandler", gestureHandler);
            setField(tuioFX, "targetSelection", targetSelection);
            setField(tuioFX, "coordinatesMapping", coordinatesMapping);
            setField(tuioFX, "touchHandler", touchHandler);
            setField(tuioFX, "tuioInputService", inputService);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T getField(Object o, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = o.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T)field.get(o);
    }

    private static <T> void setField(Object o, String fieldName, T value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = o.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(o, value);
    }

    private static void initialiseFakeTUIO(TuioFX tuioFX, Stage primaryStage) {
        TuioInputService inputService;

        try {
            inputService = getField(tuioFX, "tuioInputService");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        primaryStage.addEventFilter(MouseEvent.ANY, new TuioMouseEventFilter(inputService, primaryStage));
    }

    private static double withinRange(double min, double max, double value) {
        return Math.min(Math.max(value, min), max);
    }

    private static class TuioMouseEventFilter implements EventHandler<MouseEvent> {

        private final TuioInputService inputService;
        private final Stage primaryStage;
        private int currentSessionId = 1;

        public TuioMouseEventFilter(TuioInputService inputService, Stage primaryStage) {
            this.inputService = inputService;
            this.primaryStage = primaryStage;
        }

        @Override
        public void handle(MouseEvent event) {
            if (event.isSynthesized()) {
                return;
            }

            float x = (float)(event.getSceneX() / primaryStage.getScene().getWidth());
            float y = (float)(event.getSceneY() / primaryStage.getScene().getHeight());
            TuioCursor cursor = new TuioCursor(currentSessionId, 0, x, y);
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                inputService.addTuioCursor(cursor);
            } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                inputService.removeTuioCursor(cursor);
                ++currentSessionId;
            } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                inputService.updateTuioCursor(cursor);
            }
        }
    }

    private static class TuioInputServiceHook extends TuioInputService {

        public TuioInputServiceHook(CoordinatesMapping coordinatesMapping,
                TouchHandler touchHandler, Configuration config) {
            super(coordinatesMapping, touchHandler, config);
        }

        @Override
        public void addTuioCursor(TuioCursor tuioCursor) {
            print(tuioCursor);
            super.addTuioCursor(tuioCursor);
        }

        @Override
        public void updateTuioCursor(TuioCursor tuioCursor) {
            print(tuioCursor);
            super.updateTuioCursor(tuioCursor);
        }

        @Override
        public void removeTuioCursor(TuioCursor tuioCursor) {
            print(tuioCursor);
            super.removeTuioCursor(tuioCursor);
        }

        private void print(TuioCursor tuioCursor) {
            System.out.printf("SessionID: %d, CursorID: %d, X: %f, Y: %f %n",
                    tuioCursor.getSessionID(),
                    tuioCursor.getCursorID(),
                    tuioCursor.getX(),
                    tuioCursor.getY());
        }
    }

    private static class TargetSelectionHook extends TargetSelection {

        private final Scene scene;
        private Configuration config;

        public TargetSelectionHook(Scene scene, Configuration config) {
            super(scene, config);
            this.scene = scene;
            this.config = config;
        }

        @Override
        public EventTarget selectTargetNode(TuioTouchPoint touchPoint) {
            double screenX = touchPoint.getScreenX();
            double screenY = touchPoint.getScreenY();
            double sceneX = touchPoint.getSceneX();
            double sceneY = touchPoint.getSceneY();
            Parent startNode = scene.getRoot();
            Iterator<Window> windows = Window.impl_getWindows();

            while(windows.hasNext()) {
                Window window = windows.next();
                if (scene.getWindow() != window) {
                    double touchScreenMinX = screenX;
                    double touchScreenMinY = screenY;
                    if (config.isUseIndirectInputDevice()) {
                        touchScreenMinX = screenX + Util.getOffsetX(scene);
                        touchScreenMinY = screenY + Util.getOffsetY(scene);
                    }

                    double screenMinX = window.getX() + window.getScene().getX();
                    double screenMinY = window.getY() + window.getScene().getY();
                    double screenMaxX = screenMinX + window.getWidth();
                    double screenMaxY = screenMinY + window.getHeight();
                    if (Util.isWithIn(touchScreenMinX, screenMinX, screenMaxX)
                            && Util.isWithIn(touchScreenMinY, screenMinY, screenMaxY)) {
                        sceneX = touchScreenMinX - Util.getOffsetX(window.getScene());
                        sceneY = touchScreenMinY - Util.getOffsetY(window.getScene());
                        startNode = window.getScene().getRoot();
                        if (!(startNode instanceof TuioFXCanvas)) {
                            Platform.runLater(() -> addTuioFXCanvas(window.getScene()));
                        }
                        break;
                    }
                }
            }

            return hitTest(startNode, startNode, sceneX, sceneY);
        }

//        private Node hitTest(Parent root, Node node, double x, double y) {
//            Node targetNode;
//            Task<PickResultChooser> task = new Task<PickResultChooser>() {
//                protected PickResultChooser call() {
//                    PickRay pickRay = new PickRay(x, y, 1.0D, -1.0D / 0.0, 1.0D / 0.0);
//                    PickResultChooser result = new PickResultChooser();
//                    root.impl_pickNode(pickRay, result);
//                    pickRay.getDirectionNoClone().normalize();
//                    return result;
//                }
//            };
//            Platform.runLater(task);
//            PickResultChooser result = null;
//
//            try {
//                result = task.get();
//            } catch (InterruptedException ignored) {
//            } catch (ExecutionException var12) {
//                var12.printStackTrace();
//            }
//
//            targetNode = result.getIntersectedNode();
//            if (targetNode != null) {
//                targetNode = getPickableNode(targetNode);
//            }
//
//            return targetNode;
//        }

        private Node hitTest(Parent root, Node node, double x, double y) {

            PickRay pickRay = new PickRay(x, y, 1.0D, -1.0D / 0.0, 1.0D / 0.0);
            PickResultChooser result = new PickResultChooser();
            root.impl_pickNode(pickRay, result);
            pickRay.getDirectionNoClone().normalize();

            Node targetNode = result.getIntersectedNode();

            if (targetNode != null) {
                targetNode = getPickableNode(targetNode);
            }

            return targetNode;
        }

        private Node getPickableNode(Node targetNode) {
            String pickIfMultiTouch = (String)targetNode.getProperties().get("pickIfMultiTouch");
            String isTouchTransparent = (String)targetNode.getProperties().get("isTouchTransparent");

            if ((pickIfMultiTouch == null || !pickIfMultiTouch.equals("false")) && (isTouchTransparent == null || !isTouchTransparent.equals("true"))) {
                return !(targetNode instanceof Text) || !(targetNode.getParent() instanceof Button) && !(targetNode.getParent() instanceof Label) ? targetNode : this.getPickableNode(targetNode.getParent());
            } else {
                return getPickableNode(targetNode.getParent());
            }
        }

        private void updateTouchPointXY(TuioTouchPoint touchPoint, Scene targetScene) {
            double sceneX;
            double sceneY;
            if (config.isUseIndirectInputDevice()) {
                sceneX = touchPoint.getScreenX() + scene.getWindow().getX() + scene.getX();
                sceneY = touchPoint.getScreenY() + scene.getWindow().getY() + scene.getY();
                touchPoint.setScreenX(sceneX);
                touchPoint.setScreenY(sceneY);
            }

            sceneX = touchPoint.getScreenX() - (targetScene.getWindow().getX() + targetScene.getX());
            sceneY = touchPoint.getScreenY() - (targetScene.getWindow().getY() + targetScene.getY());
            touchPoint.setSceneX(sceneX);
            touchPoint.setSceneY(sceneY);
        }

        private void addTuioFXCanvas(Scene scene) {
            if (!(scene.getRoot() instanceof TuioFXCanvas)) {
                TuioFXCanvas tuioFXCanvas = new TuioFXCanvas();
                Parent oldRoot = scene.getRoot();
                tuioFXCanvas.getChildren().addAll(oldRoot);
                scene.setRoot(tuioFXCanvas);
                oldRoot.setTranslateX(0.0D);
                oldRoot.setTranslateY(0.0D);
                oldRoot.getStyleClass().removeAll("root");
            }

        }
    }
}
