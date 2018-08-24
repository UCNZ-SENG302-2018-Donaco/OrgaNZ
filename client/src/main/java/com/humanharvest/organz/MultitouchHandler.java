package com.humanharvest.organz;

import com.humanharvest.organz.skin.MTDatePickerSkin;
import com.humanharvest.organz.utilities.ReflectionUtils;
import com.sun.javafx.scene.NodeEventDispatcher;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.control.Skinnable;
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
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.tuiofx.widgets.controls.KeyboardPane;
import org.tuiofx.widgets.skin.ChoiceBoxSkinAndroid;
import org.tuiofx.widgets.skin.KeyboardManager;
import org.tuiofx.widgets.skin.MTComboBoxListViewSkin;
import org.tuiofx.widgets.skin.MTContextMenuSkin;
import org.tuiofx.widgets.skin.OnScreenKeyboard;
import org.tuiofx.widgets.skin.TextAreaSkinAndroid;
import org.tuiofx.widgets.skin.TextFieldSkinAndroid;
import org.tuiofx.widgets.utils.Util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public final class MultitouchHandler {
    private static final List<CurrentTouch> touches = new ArrayList<>();
    private static Pane rootPane;

    private MultitouchHandler() {
    }

    /**
     * Setup the event listeners for the root pane.
     */
    public static void initialise(Pane root) {
        rootPane = root;

        root.addEventFilter(TouchEvent.ANY, MultitouchHandler::handleTouchEvent);

        root.addEventFilter(ScrollEvent.ANY, Event::consume);
        root.addEventFilter(GestureEvent.ANY, Event::consume);
        root.addEventFilter(RotateEvent.ANY, Event::consume);
    }

    /**
     * Handles a single new touch event. Will process both single touch events and multitouch events.
     *
     * @param touchPoint   The touch point from the new event.
     * @param currentTouch The state of the finger this event belongs to.
     * @param pane         The pane the finger is on.
     */
    private static void handleCurrentTouch(TouchPoint touchPoint, CurrentTouch currentTouch, Pane pane) {
        Point2D touchPointPosition = new Point2D(touchPoint.getX(), touchPoint.getY());

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

            FocusAreaHandler focusAreaHandler = (FocusAreaHandler)pane.getUserData();

            focusAreaHandler.prependTransform(new Translate(delta.getX(), delta.getY()));
            currentTouch.setCurrentScreenPoint(touchPointPosition);
        }
    }

    /**
     * Checks if the delta results in the pane being out of bounds.
     *
     * @param delta The desired delta based on touch events.
     * @param pane  The pane to bounds check.
     * @return The new bounds to apply.
     */
    private static Point2D handleBoundsCheck(Point2D delta, Pane pane) {
        Bounds paneBounds = pane.getBoundsInParent();
        Point2D min = new Point2D(paneBounds.getMinX(), paneBounds.getMinY());
        Point2D max = new Point2D(paneBounds.getMaxX(), paneBounds.getMaxY());
        Point2D centre = min.add(max.subtract(min).multiply(0.5));

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

        FocusAreaHandler focusAreaHandler = (FocusAreaHandler)pane.getUserData();

        // The angle between the old finger position and the new finger position.
        double angleDelta = calculateAngleDelta(currentTouch, otherTouch, touchPoint);

        // The centre between the two fingers in screen coordinates.
        Point2D centre = min(currentTouch.getCurrentScreenPoint(), otherTouch.getCurrentScreenPoint())
                .add(abs(currentTouch.getCurrentScreenPoint().subtract(otherTouch.getCurrentScreenPoint()))
                        .multiply(0.5));

        // Only process if we have touch history (ie, not a new touch)
        if (currentTouch.getLastCentre() != null) {
            // Translate the pane
            Point2D delta = centre.subtract(currentTouch.getLastCentre());
            delta = handleBoundsCheck(delta, pane);
            focusAreaHandler.prependTransform(new Translate(delta.getX(), delta.getY()));

            // Scale the pane
            double scaleDifference =
                    new Point2D(touchPoint.getX(), touchPoint.getY()).distance(
                            otherTouch.getCurrentScreenPoint()) /
                            currentTouch.getCurrentScreenPoint().distance(otherTouch.getCurrentScreenPoint());

            Affine oldTransform = new Affine(focusAreaHandler.getTransform());
            focusAreaHandler.prependTransform(new Scale(scaleDifference, scaleDifference, centre.getX(), centre.getY()));
            double scaleX = Math.sqrt(focusAreaHandler.getTransform().getMxx() * focusAreaHandler.getTransform().getMxx()
                    + focusAreaHandler.getTransform().getMxy() * focusAreaHandler.getTransform().getMxy());

            if (scaleX < 0.25 || scaleX > 2) {
                focusAreaHandler.setTransform(oldTransform);
            }
        }

        // Rotate the pane
        focusAreaHandler.prependTransform(new Rotate(Math.toDegrees(angleDelta), centre.getX(), centre.getY()));

        // Update touch state
        currentTouch.setLastCentre(centre);
        otherTouch.setLastCentre(centre);
        currentTouch.setCurrentScreenPoint(touchPointPosition);
    }

    /**
     * Calculates the angle delta between two previous touches and a new touch.
     */
    private static double calculateAngleDelta(
            CurrentTouch currentTouch,
            CurrentTouch otherTouch,
            TouchPoint touchPoint) {
        double oldAngle = calculateAngle(currentTouch.getCurrentScreenPoint(),
                otherTouch.getCurrentScreenPoint());
        double newAngle = calculateAngle(
                new Point2D(touchPoint.getX(), touchPoint.getY()),
                otherTouch.getCurrentScreenPoint());
        return newAngle - oldAngle;
    }

    private static CurrentTouch getOtherTouch(CurrentTouch currentTouch, List<? extends CurrentTouch> paneTouches) {
        if (Objects.equals(paneTouches.get(0), currentTouch)) {
            return paneTouches.get(1);
        } else {
            return paneTouches.get(0);
        }
    }

    private static Point2D abs(Point2D point2D) {
        return new Point2D(Math.abs(point2D.getX()), Math.abs(point2D.getY()));
    }

    private static Point2D min(Point2D p1, Point2D p2) {
        return new Point2D(Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()));
    }

    private static double calculateAngle(Point2D point1, Point2D point2) {
        return Math.atan2(point2.getY() - point1.getY(), point2.getX() - point1.getX());
    }

    /**
     * Find all touches this pane owns.
     */
    private static List<CurrentTouch> findPaneTouches(Pane pane) {
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
    private static Optional<Pane> findPane(Node node) {
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

        if (intersectNode instanceof Pane && intersectNode.getUserData() instanceof FocusAreaHandler) {
            return Optional.of((Pane)intersectNode);
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
                    findPane(touchPoint.getPickResult().getIntersectedNode()),
                    getImportantElement(touchPoint));
            touches.set(touchPoint.getId(), currentTouch);
        }

        return currentTouch;
    }

    /**
     * Returns an important (ie, text, button, list, etc) node if the touchPoint intersects it.
     */
    private static Optional<Node> getImportantElement(TouchPoint touchPoint) {
        Node node = touchPoint.getPickResult().getIntersectedNode();

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
     * Setup a listener for a pane, this watches the pane's heirachy to find nodes that need to be informed of state
     * changes.
     */
    public static void setupPaneListener(Pane pane) {
        FocusAreaHandler handlerListener = new FocusAreaHandler(pane);
        pane.setUserData(handlerListener);

        addPaneListenerChildren(handlerListener, pane);
    }

    /**
     * Recursivelly adds the focusAreaHandler to the children.
     */
    private static void addPaneListenerChildren(FocusAreaHandler focusAreaHandler, Node node) {
        if (node instanceof Parent) {
            ((Parent) node).getChildrenUnmodifiable().addListener(focusAreaHandler);
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                addPaneListenerChildren(focusAreaHandler, child);
            }
        }
    }

    /**
     * Returns the focus area handler for the node.
     */
    public static Optional<FocusAreaHandler> getFocusAreaHandler(Node node) {
        Optional<Pane> pane = findPane(node);
        return pane.map(pane1 -> {
            return (FocusAreaHandler) pane1.getUserData();
        });
    }

    public static void addPane(Pane pane) {
        pane.getProperties().put("focusArea", "true");
        rootPane.getChildren().add(pane);
    }

    public static void removePane(Pane pane) {
        rootPane.getChildren().remove(pane);
    }

    private static void handleTouchEvent(TouchEvent event) {
        TouchPoint touchPoint = event.getTouchPoint();
        CurrentTouch currentTouch = getCurrentTouch(touchPoint);

        if (event.getEventType() == TouchEvent.TOUCH_PRESSED) {
            currentTouch.setCurrentScreenPoint(new Point2D(touchPoint.getX(), touchPoint.getY()));
            currentTouch.getPane().ifPresent(pane -> {
                pane.toFront();

                OnScreenKeyboard<?> keyboard = KeyboardManager.getInstance().getKeyboard(pane);
                try {
                    ReflectionUtils.<KeyboardPane>getField(keyboard.getSkin(), "keyboardPane").toFront();
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
            currentTouch.getPane().ifPresent(pane -> {
                // Forwards the touch event to an important node.
                currentTouch.getImportantElement().ifPresent(node -> {
                    NodeEventDispatcher eventDispatcher = (NodeEventDispatcher) node.getEventDispatcher();
                    eventDispatcher.dispatchCapturingEvent(event);
                });
                if (findPaneTouches(pane).size() == 1) {
                    // Informs the focus area nodes of a touch event
                    FocusAreaHandler focusAreaHandler = (FocusAreaHandler) pane.getUserData();
                    focusAreaHandler.propagateEvent(event.getTarget());
                }
            });
        } else if (event.getEventType() == TouchEvent.TOUCH_RELEASED) {
            // Forwards the touch event to an important node.
            currentTouch.getImportantElement().ifPresent(node -> {
                NodeEventDispatcher eventDispatcher = (NodeEventDispatcher) node.getEventDispatcher();
                eventDispatcher.dispatchCapturingEvent(event);
            });

            removeCurrentTouch(touchPoint);
        } else {
            currentTouch.getPane().ifPresent(pane -> {
                handleCurrentTouch(touchPoint, currentTouch, pane);
            });
        }

        event.consume();
    }

    /**
     * The state of a finger touching the screen.
     */
    private static class CurrentTouch {
        private final Optional<Pane> pane;
        private final Optional<Node> importantElement;

        private Point2D currentScreenPoint;
        private Point2D lastCentre;

        public CurrentTouch(Optional<Pane> pane, Optional<Node> importantElement) {
            this.pane = pane;
            this.importantElement = importantElement;
        }

        /**
         * Returns the pane this finger controls.
         */
        public Optional<Pane> getPane() {
            return pane;
        }

        /**
         * Returns the current point of the finger in screen coordinates.
         */
        public Point2D getCurrentScreenPoint() {
            return currentScreenPoint;
        }

        /**
         * Updates the current point of the finger.
         */
        public void setCurrentScreenPoint(Point2D currentScreenPoint) {
            this.currentScreenPoint = currentScreenPoint;
        }

        /**
         * Returns the last centre point between this finger and another one.
         */
        public Point2D getLastCentre() {
            return lastCentre;
        }

        public void setLastCentre(Point2D lastCentre) {
            this.lastCentre = lastCentre;
        }

        public Optional<Node> getImportantElement() {
            return importantElement;
        }
    }

    private static final class TextFieldSkinConsumer implements Consumer<EventTarget> {
        private final OnScreenKeyboard<?> keyboard;
        private final Method detachKeyboard;
        private final Skin<?> skin;

        public TextFieldSkinConsumer(Skin<?> skin) {
            this.skin = skin;
            try {
                keyboard = ReflectionUtils.getField(skin, "keyboard");
                detachKeyboard = ReflectionUtils.getMethodReference(skin, "detachKeyboard", OnScreenKeyboard.class, EventTarget.class);
            } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void accept(EventTarget t) {
            try {
                detachKeyboard.invoke(skin, keyboard, t);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final class TextAreaSkinConsumer implements Consumer<EventTarget> {
        private final OnScreenKeyboard<?> keyboard;
        private final Method detachKeyboard;
        private final Skin<?> skin;

        public TextAreaSkinConsumer(Skin<?> skin) {
            this.skin = skin;
            try {
                keyboard = KeyboardManager.getInstance().getKeyboard(Util.getFocusAreaStartingNode((Node) skin.getSkinnable()));
                detachKeyboard = ReflectionUtils.getMethodReference(skin, "detachKeyboard", OnScreenKeyboard.class, EventTarget.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void accept(EventTarget t) {
            try {
                detachKeyboard.invoke(skin, keyboard, t);
            } catch (IllegalAccessException | InvocationTargetException e) {
                // Sometimes an exception is thrown from the keyboard.
                throw new RuntimeException(e);
            }
        }
    }

    private static final class ComboBoxListSkinConsumer implements Consumer<EventTarget> {
        private final Method isComboBoxOrButton;
        private final Method handleAutoHidingEvents;
        private final Skin<?> skin;

        public ComboBoxListSkinConsumer(Skin<?> skin) {
            try {
                isComboBoxOrButton = ReflectionUtils.getMethodReference(skin, "isComboBoxOrButton", EventTarget.class, ComboBoxBase.class);
                handleAutoHidingEvents = ReflectionUtils.getMethodReference(skin, "handleAutoHidingEvents");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            this.skin = skin;
        }

        @Override
        public void accept(EventTarget t) {
            try {
                if (!(Boolean) isComboBoxOrButton.invoke(skin, t, skin.getSkinnable())) {
                    handleAutoHidingEvents.invoke(skin);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final class ContextMenuSkinConsumer implements Consumer<EventTarget> {
        private final Method handleAutoHidingEvents;
        private final Skin<?> skin;

        public ContextMenuSkinConsumer(Skin<?> skin) {
            try {
                handleAutoHidingEvents = ReflectionUtils.getMethodReference(skin, "handleAutoHidingEvents", Event.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            this.skin = skin;
        }

        @Override
        public void accept(EventTarget t) {
            try {
                handleAutoHidingEvents.invoke(skin, new Event(null, t, null));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final class ChoiceBoxSkinConsumer implements Consumer<EventTarget> {
        private final Skin<? extends ChoiceBox<?>> skin;

        public ChoiceBoxSkinConsumer(Skin<? extends ChoiceBox<?>> skin) {
            this.skin = skin;
        }

        @Override
        public void accept(EventTarget t) {
            if (!Objects.equals(t, skin.getSkinnable())) {
                skin.getSkinnable().hide();
            }
        }
    }

    private static final class DatePickerSkinConsumer implements Consumer<EventTarget> {
        private final Skin<? extends DatePicker> skin;

        public DatePickerSkinConsumer(Skin<? extends DatePicker> skin) {
            this.skin = skin;
        }

        @Override
        public void accept(EventTarget t) {
            if (!Objects.equals(t, skin.getSkinnable())) {
                skin.getSkinnable().hide();
            }
        }
    }

    public static class FocusAreaHandler implements InvalidationListener {
        private final Collection<Consumer<EventTarget>> skinHandlers = new ArrayList<>();
        private final Collection<Consumer<EventTarget>> popupHandlers = new ArrayList<>();
        private final Pane pane;
        private boolean outOfDate = true;

        private Affine transform;

        public FocusAreaHandler(Pane pane) {
            this.pane = pane;
            setupInitialTransforms();
        }

        /**
         * Sets up or retrieves an affine transformation from the pane.
         */
        private void setupInitialTransforms() {
            transform = new Affine();
        }

        @Override
        public void invalidated(Observable observable) {
            outOfDate = true;
        }

        public void propagateEvent(EventTarget target) {
            refresh();

            for (Consumer<EventTarget> consumer : skinHandlers) {
                consumer.accept(target);
            }

            for (Consumer<EventTarget> consumer : popupHandlers) {
                consumer.accept(target);
            }
        }

        private void refresh() {
            if (outOfDate) {
                skinHandlers.clear();

                findSkinHandlers(pane);

                outOfDate = true;
            }
        }

        private void findSkinHandlers(Node node) {
            if (node instanceof Skinnable) {
                Skin<?> skin = ((Skinnable) node).getSkin();

                if (skin instanceof TextFieldSkinAndroid) {
                    skinHandlers.add(new TextFieldSkinConsumer(skin));
                } else if (skin instanceof MTComboBoxListViewSkin) {
                    skinHandlers.add(new ComboBoxListSkinConsumer(skin));
                } else if (skin instanceof MTContextMenuSkin) {
                    skinHandlers.add(new ContextMenuSkinConsumer(skin));
                } else if (skin instanceof TextAreaSkinAndroid) {
                    skinHandlers.add(new TextAreaSkinConsumer(skin));
                } else if (skin instanceof ChoiceBoxSkinAndroid) {
                    skinHandlers.add(new ChoiceBoxSkinConsumer((Skin<ChoiceBox<?>>) skin));
                } else if (skin instanceof MTDatePickerSkin) {
                    skinHandlers.add(new DatePickerSkinConsumer((Skin<DatePicker>) skin));
                }
            }

            if (node instanceof Parent) {
                for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                    findSkinHandlers(child);
                }
            }
        }

        public void addPopup(PopupControl popupMenu, Consumer<EventTarget> eventHandler) {
            popupHandlers.add(eventHandler);
        }

        /**
         * Returns the affine transformation of the pane this finger controls.
         */
        public Affine getTransform() {
            return transform;
        }

        /**
         * Prepends the transform onto the current transformation.
         */
        public void prependTransform(Transform transformDelta) {
            transform.prepend(transformDelta);
            updatePaneTransform();
        }

        /**
         * Sets the current transformation matrix to a new one.
         */
        public void setTransform(Affine newTransform) {
            transform = newTransform;
            updatePaneTransform();
        }

        private void updatePaneTransform() {
            List<Transform> transforms = pane.getTransforms();
            if (transforms.size() == 1 && Objects.equals(transforms.get(0), transform)) {
                return;
            }

            if (!transforms.isEmpty()) {
                transforms.clear();
            }
            transforms.add(transform);
        }
    }
}
