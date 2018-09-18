package com.humanharvest.organz.touch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.GestureEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import com.humanharvest.organz.utilities.ReflectionUtils;

import com.sun.javafx.scene.NodeEventDispatcher;
import org.tuiofx.widgets.controls.KeyboardPane;
import org.tuiofx.widgets.skin.KeyboardManager;
import org.tuiofx.widgets.skin.OnScreenKeyboard;

public final class MultitouchHandler {

    private static final Collection<FocusArea> focusAreas = new ArrayList<>();
    private static final List<CurrentTouch> touches = new ArrayList<>();
    private static final Timer physicsTimer = new Timer();

    private static Pane rootPane;
    private static Pane canvas;
    private static PhysicsHandler physicsHandler;

    private MultitouchHandler() {
    }

    /**
     * Setup the event listeners for the root pane.
     */
    public static void initialise(Pane root) {
        rootPane = root;
        canvas = (Pane) rootPane.getChildren().get(0);

        root.addEventFilter(TouchEvent.ANY, MultitouchHandler::handleTouchEvent);

        root.addEventFilter(ScrollEvent.ANY, Event::consume);
        root.addEventFilter(GestureEvent.ANY, Event::consume);
        root.addEventFilter(RotateEvent.ANY, Event::consume);

//        HackyMouseTouch.initialise(root);

        physicsHandler = new PhysicsHandler(rootPane);

        physicsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(MultitouchHandler::processPhysics);
            }
        }, 0, PhysicsHandler.PHYSICS_MILLISECOND_PERIOD);
    }

    private static void processPhysics() {
        physicsHandler.processPhysics();
    }

    /**
     * Handles a single new touch event. Will process both single touch events and multitouch events.
     *
     * @param touchPoint The touch point from the new event.
     * @param currentTouch The state of the finger this event belongs to.
     * @param pane The pane the finger is on.
     */
    private static void handleCurrentTouch(TouchPoint touchPoint, CurrentTouch currentTouch, Pane pane) {
        Point2D touchPointPosition = new Point2D(touchPoint.getX(), touchPoint.getY());
        if (PointUtils.distance(touchPointPosition, currentTouch.getCurrentScreenPoint()) < 2) {
            return;
        }

        FocusArea focusArea = (FocusArea) pane.getUserData();
        focusArea.setLastPosition(System.nanoTime(), PointUtils.getCentreOfPane(pane));

        // Find other touches belonging to this pane.
        List<CurrentTouch> paneTouches = findPaneTouches(pane);
        if (paneTouches.size() == 1) {
            handleSingleTouch(touchPointPosition, currentTouch, pane);

        } else if (paneTouches.size() == 2) {
            CurrentTouch otherTouch = getOtherTouch(currentTouch, paneTouches);
            handleDoubleTouch(touchPointPosition, touchPoint, currentTouch, otherTouch, pane);
        }
    }

    /**
     * Handles a touch event with a single finger on a pane. Will only translate the pane.
     */
    private static void handleSingleTouch(Point2D touchPointPosition, CurrentTouch currentTouch, Pane pane) {
        if (!currentTouch.getImportantElement().isPresent()) {
            Point2D delta = touchPointPosition.subtract(currentTouch.getCurrentScreenPoint());
            delta = handleBoundsCheck(delta, pane);

            FocusArea focusArea = (FocusArea) pane.getUserData();

            if (focusArea.isTranslatable()) {
                focusArea.prependTransform(new Translate(delta.getX(), delta.getY()));
            }
            currentTouch.setCurrentScreenPoint(touchPointPosition);
        }
    }

    /**
     * Checks if the delta results in the pane being out of bounds.
     *
     * @param delta The desired delta based on touch events.
     * @param pane The pane to bounds check.
     * @return The new bounds to apply.
     */
    private static Point2D handleBoundsCheck(Point2D delta, Pane pane) {
        Point2D centre = PointUtils.getCentreOfPane(pane);

        if (centre.getX() + delta.getX() < 0) {
            delta = new Point2D(-centre.getX(), delta.getY());
        }

        if (centre.getY() + delta.getY() < 0) {
            delta = new Point2D(delta.getX(), -centre.getY());
        }

        if (centre.getX() + delta.getX() > rootPane.getWidth()) {
            delta = new Point2D(rootPane.getWidth() - centre.getX(), delta.getY());
        }

        if (centre.getY() + delta.getY() > rootPane.getHeight()) {
            delta = new Point2D(delta.getX(), rootPane.getHeight() - centre.getY());
        }

        return delta;
    }

    /**
     * Handles a touch event with two fingers on the pane. Will translate, rotate and scale the pane.
     */
    private static void handleDoubleTouch(
            Point2D touchPointPosition,
            TouchPoint touchPoint,
            CurrentTouch currentTouch,
            CurrentTouch otherTouch,
            Pane pane) {

        FocusArea focusArea = (FocusArea) pane.getUserData();

        // The angle between the old finger position and the new finger position.
        double angleDelta = PointUtils.calculateAngleDelta(currentTouch, otherTouch, touchPoint);

        // The centre between the two fingers in screen coordinates.
        Point2D centre;
        if (focusArea.isTranslatable()) {
            centre = PointUtils.min(currentTouch.getCurrentScreenPoint(), otherTouch.getCurrentScreenPoint())
                    .add(PointUtils.abs(currentTouch.getCurrentScreenPoint()
                            .subtract(otherTouch.getCurrentScreenPoint()))
                            .multiply(0.5));
        } else {
            centre = PointUtils.getCentreOfPane(pane);
        }

        // Only process if we have touch history (ie, not a new touch)
        if (currentTouch.getLastCentre() != null) {
            // Translate the pane
            Point2D delta = centre.subtract(currentTouch.getLastCentre());
            delta = handleBoundsCheck(delta, pane);
            if (focusArea.isTranslatable()) {
                focusArea.prependTransform(new Translate(delta.getX(), delta.getY()));
            }

            // Scale the pane
            if (focusArea.isScalable()) {
                double scaleDifference =
                        new Point2D(touchPoint.getX(), touchPoint.getY()).distance(
                                otherTouch.getCurrentScreenPoint()) /
                                currentTouch.getCurrentScreenPoint().distance(otherTouch.getCurrentScreenPoint());

                Affine oldTransform = new Affine(focusArea.getTransform());

                Scale scaleTransform = new Scale(scaleDifference, scaleDifference, centre.getX(), centre.getY());
                focusArea.prependTransform(scaleTransform);

                double currentMxx = focusArea.getTransform().getMxx();
                double currentMxy = focusArea.getTransform().getMxy();
                double scaleX = Math.sqrt(currentMxx * currentMxx + currentMxy * currentMxy);

                if (scaleX < 0.25 || scaleX > 2) {
                    focusArea.setTransform(oldTransform);
                }
            }
        }

        // Rotate the pane
        if (focusArea.isRotatable()) {
            focusArea.prependTransform(new Rotate(Math.toDegrees(angleDelta), centre.getX(), centre.getY()));
        }

        // Update touch state
        currentTouch.setLastCentre(centre);
        otherTouch.setLastCentre(centre);
        currentTouch.setCurrentScreenPoint(touchPointPosition);
    }

    private static CurrentTouch getOtherTouch(CurrentTouch currentTouch, List<? extends CurrentTouch> paneTouches) {
        if (Objects.equals(paneTouches.get(0), currentTouch)) {
            return paneTouches.get(1);
        } else {
            return paneTouches.get(0);
        }
    }

    /**
     * Find all touches this pane owns.
     */
    public static List<CurrentTouch> findPaneTouches(Pane pane) {
        List<CurrentTouch> results = new ArrayList<>();
        for (CurrentTouch currentTouch : touches) {
            if (currentTouch != null) {
                Optional<Pane> touchPane = currentTouch.getPane();
                if (touchPane.isPresent() && Objects.equals(touchPane.get(), pane)) {
                    results.add(currentTouch);
                }
            }
        }
        return results;
    }

    /**
     * Finds the pane this node belongs to, or Optional.empty() if the node doesn't belong to any pane.
     */
    static Optional<Pane> findPane(Node node) {
        if (node == null) {
            return Optional.empty();
        }

        Node intersectNode = node;
        // Traverse the node parent history, until the parent doesn't exist or is the root pane.
        while (!Objects.equals(intersectNode.getParent(), rootPane)) {
            intersectNode = intersectNode.getParent();
            if (intersectNode == null) {
                return Optional.empty();
            }
        }

        if (intersectNode instanceof Pane && intersectNode.getUserData() instanceof FocusArea) {
            return Optional.of((Pane) intersectNode);
        }

        // Also has org.tuiofx.widgets.controls.KeyboardPane

        return Optional.empty();
    }

    /**
     * Gets or creates a current touch state from a given TouchPoint.
     */
    private static CurrentTouch getCurrentTouch(TouchPoint touchPoint) {
        while (touchPoint.getId() >= touches.size()) {
            touches.add(null);
        }

        CurrentTouch currentTouch = touches.get(touchPoint.getId());
        if (currentTouch == null) {
            currentTouch = new CurrentTouch(
                    findPane(touchPoint.getPickResult().getIntersectedNode()).orElse(null),
                    getImportantElement(touchPoint.getPickResult().getIntersectedNode()).orElse(null));
            touches.set(touchPoint.getId(), currentTouch);
        }

        return currentTouch;
    }

    /**
     * Returns an important (ie, text, button, list, etc) node if the touchPoint intersects it.
     */
    static Optional<Node> getImportantElement(Node node) {

        while (node != null && !Objects.equals(node, rootPane)) {
            if (node instanceof Button) {
                return Optional.of(node);
            }
            if (node instanceof TextField) {
                return Optional.of(node);
            }
            if (node instanceof ListView) {
                return Optional.of(node);
            }
            if (node instanceof DatePicker) {
                return Optional.of(node);
            }
            if (node instanceof MenuBar) {
                return Optional.of(node);
            }
            if (node instanceof TitledPane) {
                return Optional.of(node);
            }
            if (node instanceof TableView) {
                return Optional.of(node);
            }
            if (node instanceof ChoiceBox) {
                return Optional.of(node);
            }
            if (node instanceof Slider) {
                return Optional.of(node);
            }

            // Pagination buttons
            if (Objects.equals(
                    node.getClass().getName(),
                    "com.sun.javafx.scene.control.skin.PaginationSkin$NavigationControl")) {
                return Optional.of(node);
            }

            node = node.getParent();
        }

        return Optional.empty();
    }

    /**
     * Removes the current touch from the list of touches.
     */
    private static void removeCurrentTouch(TouchPoint touchPoint) {
        touches.set(touchPoint.getId(), null);
    }

    /**
     * Recursivelly adds the focusArea to the children.
     */
    private static void addPaneListenerChildren(FocusArea focusArea, Node node) {
        if (node instanceof Parent) {
            ((Parent) node).getChildrenUnmodifiable().addListener(focusArea);
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                addPaneListenerChildren(focusArea, child);
            }
        }
    }

    /**
     * Returns the focus area handler for the node.
     */
    public static Optional<FocusArea> getFocusAreaHandler(Node node) {
        Optional<Pane> pane = findPane(node);
        return pane.map(pane1 -> {
            return (FocusArea) pane1.getUserData();
        });
    }

    /**
     * Adds a pane to the root pane. Also sets up the focus area.
     */
    public static void addPane(Pane pane) {
        pane.getProperties().put("focusArea", "true");
        FocusArea focusArea = new FocusArea(pane);

        focusAreas.add(focusArea);
        pane.getScene().getWindow();
        pane.setUserData(focusArea);

        addPaneListenerChildren(focusArea, pane);

        rootPane.getChildren().add(pane);
    }

    public static void removePane(Pane pane) {
        rootPane.getChildren().remove(pane);
        FocusArea focusArea = (FocusArea) pane.getUserData();
        focusAreas.remove(focusArea);
    }

    private static void handleTouchEvent(TouchEvent event) {
        TouchPoint touchPoint = event.getTouchPoint();
        CurrentTouch currentTouch = getCurrentTouch(touchPoint);

        if (event.getEventType() == TouchEvent.TOUCH_PRESSED) {
            currentTouch.setCurrentScreenPoint(new Point2D(touchPoint.getX(), touchPoint.getY()));
            currentTouch.getPane().ifPresent(pane -> {
                pane.toFront();

                OnScreenKeyboard<?> keyboard = KeyboardManager.getInstance().getKeyboard(pane);
                ReflectionUtils.<KeyboardPane>getField(keyboard.getSkin(), "keyboardPane").toFront();
            });
            currentTouch.getPane().ifPresent(pane -> {
                // Forwards the touch event to an important node.
                currentTouch.getImportantElement().ifPresent(node -> {
                    NodeEventDispatcher eventDispatcher = (NodeEventDispatcher) node.getEventDispatcher();
                    eventDispatcher.dispatchCapturingEvent(event);
                });
                if (findPaneTouches(pane).size() == 1) {
                    // Informs the focus area nodes of a touch event
                    FocusArea focusArea = (FocusArea) pane.getUserData();
                    focusArea.setLastPosition(System.nanoTime(), PointUtils.getCentreOfPane(pane));
                    focusArea.propagateEvent(event.getTarget());
                }
            });
        } else if (event.getEventType() == TouchEvent.TOUCH_RELEASED) {
            // Forwards the touch event to an important node.
            currentTouch.getImportantElement().ifPresent(node -> {
                NodeEventDispatcher eventDispatcher = (NodeEventDispatcher) node.getEventDispatcher();
                eventDispatcher.dispatchCapturingEvent(event);
            });

            removeCurrentTouch(touchPoint);

            currentTouch.getPane().ifPresent(pane -> {
                if (findPaneTouches(pane).isEmpty()) {
                    pane.setCacheHint(CacheHint.QUALITY);
                    FocusArea focusArea = (FocusArea) pane.getUserData();
                    focusArea.setupVelocity(System.nanoTime(), PointUtils.getCentreOfPane(pane));
                }
            });
        } else {
            currentTouch.getPane().ifPresent(pane -> {
                handleCurrentTouch(touchPoint, currentTouch, pane);
            });
        }

        event.consume();
    }

    /**
     * Called when the stage is closing.
     * Cleans up all resources.
     */
    public static void stageClosing() {
        physicsTimer.cancel();
    }

    /**
     * Retrieves a readonly collection of the current focus areas.
     */
    public static Collection<FocusArea> getFocusAreas() {
        return Collections.unmodifiableCollection(focusAreas);
    }

    public static Pane getCanvas() {
        return canvas;
    }

    public static Pane getRootPane() {
        return rootPane;
    }

    public static void setPhysicsHandler(PhysicsHandler physicsHandler) {
        MultitouchHandler.physicsHandler = physicsHandler;
    }
}

