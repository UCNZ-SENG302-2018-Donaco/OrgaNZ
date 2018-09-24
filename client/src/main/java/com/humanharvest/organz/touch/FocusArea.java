package com.humanharvest.organz.touch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Skin;
import javafx.scene.control.Skinnable;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import com.humanharvest.organz.skin.MTDatePickerSkin;
import com.humanharvest.organz.utilities.ReflectionException;
import com.humanharvest.organz.utilities.ReflectionUtils;

import com.sun.javafx.scene.NodeEventDispatcher;
import org.tuiofx.widgets.controls.KeyboardPane;
import org.tuiofx.widgets.skin.ChoiceBoxSkinAndroid;
import org.tuiofx.widgets.skin.KeyboardManager;
import org.tuiofx.widgets.skin.MTComboBoxListViewSkin;
import org.tuiofx.widgets.skin.MTContextMenuSkin;
import org.tuiofx.widgets.skin.OnScreenKeyboard;
import org.tuiofx.widgets.skin.TextAreaSkinAndroid;
import org.tuiofx.widgets.skin.TextFieldSkinAndroid;
import org.tuiofx.widgets.utils.Util;

public class FocusArea implements InvalidationListener {

    private final Collection<Consumer<EventTarget>> skinHandlers = new ArrayList<>();
    private final Collection<Consumer<EventTarget>> popupHandlers = new ArrayList<>();
    private final Pane pane;
    private boolean outOfDate = true;
    private boolean translatable = true;
    private boolean scalable = true;
    private boolean rotatable = true;

    private Affine transform;

    // A chain of the last X seconds of touch events. Used to smooth out fluctuations in touch events.
    private final LinkedList<TimedPoint> eventPoints = new LinkedList<>();
    private Point2D velocity = Point2D.ZERO;
    private boolean collidable;
    private boolean disableHinting;

    public FocusArea(Pane pane) {
        this.pane = pane;
        setupInitialTransforms();
        pane.setCache(true);
        pane.setCacheHint(CacheHint.QUALITY);
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

    public void addPopup(Consumer<EventTarget> eventHandler) {
        popupHandlers.add(eventHandler);
    }

    /**
     * Returns the affine transformation of the pane this finger controls.
     */
    public Affine getTransform() {
        return transform;
    }

    /**
     * Sets the current transformation matrix to a new one.
     */
    public void setTransform(Affine newTransform) {
        transform = newTransform;
        updatePaneTransform();
    }

    /**
     * Prepends the transform onto the current transformation.
     */
    public void prependTransform(Transform transformDelta) {
        transform.prepend(transformDelta);
        updatePaneTransform();
    }

    private void updatePaneTransform() {
        if (!disableHinting) {
            pane.setCacheHint(CacheHint.SPEED);
        }
        List<Transform> transforms = pane.getTransforms();
        if (transforms.size() == 1 && Objects.equals(transforms.get(0), transform)) {
            return;
        }

        if (!transforms.isEmpty()) {
            transforms.clear();
        }
        transforms.add(transform);
    }

    /**
     * Adds a touch event with a time, to calculate velocity.
     */
    public void setLastPosition(long nanoTime, Point2D centre) {
        eventPoints.add(new TimedPoint(nanoTime, centre));

        long oldTime = nanoTime - 100_000_000; // 0.1 Seconds
        while (eventPoints.peekFirst().getTime() < oldTime) {
            eventPoints.removeFirst();
        }
    }

    /**
     * Sets the velocity to a calculated value.
     */
    public void setupVelocity(long nanoTime, Point2D centre) {
        TimedPoint timedPoint = eventPoints.peekFirst();
        double timeDelta = (nanoTime - timedPoint.getTime()) / 1000000000.0;
        Point2D averagePosition = getAveragePosition();
        Point2D positionDelta = centre.subtract(averagePosition);
        velocity = new Point2D(positionDelta.getX(), positionDelta.getY());

        if (PointUtils.distance(Point2D.ZERO, velocity) < PhysicsHandler.MIN_VELOCITY_THRESHOLD * 4) {
            velocity = Point2D.ZERO;
        }

        velocity = velocity.multiply(1 / timeDelta);

        eventPoints.clear();
    }

    private Point2D getAveragePosition() {
        double inverseTotal = 1.0 / eventPoints.size();
        Point2D position = Point2D.ZERO;
        for (TimedPoint timedPoint : eventPoints) {
            position = position.add(timedPoint.getPosition().multiply(inverseTotal));
        }

        return position;
    }

    public Pane getPane() {
        return pane;
    }

    public Point2D getVelocity() {
        return velocity;
    }

    public void addVelocity(Point2D velocityDelta) {
        velocity = velocity.add(velocityDelta);
    }

    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }

    public void setDisableHinting(boolean disableHinting) {
        this.disableHinting = disableHinting;
    }

    public void handleTouchEvent(TouchEvent event, CurrentTouch currentTouch) {

        TouchPoint touchPoint = event.getTouchPoint();

        if (event.getEventType() == TouchEvent.TOUCH_PRESSED) {
            currentTouch.setCurrentScreenPoint(new Point2D(touchPoint.getX(), touchPoint.getY()));

            pane.toFront();

            OnScreenKeyboard<?> keyboard = KeyboardManager.getInstance().getKeyboard(pane);
            ReflectionUtils.<KeyboardPane>getField(keyboard.getSkin(), "keyboardPane").toFront();

            // Forwards the touch event to an important node.
            currentTouch.getImportantElement().ifPresent(node -> {
                NodeEventDispatcher eventDispatcher = (NodeEventDispatcher) node.getEventDispatcher();
                eventDispatcher.dispatchCapturingEvent(event);
            });
            if (MultitouchHandler.findPaneTouches(pane).size() == 1) {
                // Informs the focus area nodes of a touch event
                setLastPosition(System.nanoTime(), PointUtils.getCentreOfNode(pane));
                propagateEvent(event.getTarget());
            }
        } else if (event.getEventType() == TouchEvent.TOUCH_RELEASED) {
            // Forwards the touch event to an important node.
            currentTouch.getImportantElement().ifPresent(node -> {
                NodeEventDispatcher eventDispatcher = (NodeEventDispatcher) node.getEventDispatcher();
                eventDispatcher.dispatchCapturingEvent(event);
            });

            if (MultitouchHandler.findPaneTouches(pane).isEmpty()) {
                pane.setCacheHint(CacheHint.QUALITY);
                setupVelocity(System.nanoTime(), PointUtils.getCentreOfNode(pane));
            }
        } else {
            handleCurrentTouch(touchPoint, currentTouch);
        }
    }

    /**
     * Handles a single new touch event. Will process both single touch events and multitouch events.
     *
     * @param touchPoint The touch point from the new event.
     * @param currentTouch The state of the finger this event belongs to.
     */
    private void handleCurrentTouch(TouchPoint touchPoint, CurrentTouch currentTouch) {
        Point2D touchPointPosition = new Point2D(touchPoint.getX(), touchPoint.getY());
        if (PointUtils.distance(touchPointPosition, currentTouch.getCurrentScreenPoint()) < 2) {
            return;
        }

        setLastPosition(System.nanoTime(), PointUtils.getCentreOfNode(pane));

        // Find other touches belonging to this pane.
        List<CurrentTouch> paneTouches = MultitouchHandler.findPaneTouches(pane);
        if (paneTouches.size() == 1) {
            handleSingleTouch(touchPointPosition, currentTouch);

        } else if (paneTouches.size() == 2) {
            CurrentTouch otherTouch = getOtherTouch(currentTouch, paneTouches);
            handleDoubleTouch(touchPointPosition, touchPoint, currentTouch, otherTouch);
        }
    }

    /**
     * Handles a touch event with two fingers on the pane. Will translate, rotate and scale the pane.
     */
    private void handleDoubleTouch(
            Point2D touchPointPosition,
            TouchPoint touchPoint,
            CurrentTouch currentTouch,
            CurrentTouch otherTouch) {

        // The angle between the old finger position and the new finger position.
        double angleDelta = PointUtils.calculateAngleDelta(currentTouch, otherTouch, touchPoint);

        // The centre between the two fingers in screen coordinates.
        Point2D centre;
        if (translatable) {
            centre = PointUtils.min(currentTouch.getCurrentScreenPoint(), otherTouch.getCurrentScreenPoint())
                    .add(PointUtils.abs(currentTouch.getCurrentScreenPoint()
                            .subtract(otherTouch.getCurrentScreenPoint()))
                            .multiply(0.5));
        } else {
            centre = PointUtils.getCentreOfNode(pane);
        }

        // Only process if we have touch history (ie, not a new touch)
        if (currentTouch.getLastCentre() != null) {
            // Translate the pane
            Point2D delta = centre.subtract(currentTouch.getLastCentre());
            delta = handleBoundsCheck(delta);
            if (translatable) {
                prependTransform(new Translate(delta.getX(), delta.getY()));
            }

            // Scale the pane
            if (scalable) {
                double scaleDifference =
                        new Point2D(touchPoint.getX(), touchPoint.getY()).distance(
                                otherTouch.getCurrentScreenPoint()) /
                                currentTouch.getCurrentScreenPoint().distance(otherTouch.getCurrentScreenPoint());

                Affine oldTransform = new Affine(transform);

                Scale scaleTransform = new Scale(scaleDifference, scaleDifference, centre.getX(), centre.getY());
                prependTransform(scaleTransform);

                double currentMxx = transform.getMxx();
                double currentMxy = transform.getMxy();
                double scaleX = Math.sqrt(currentMxx * currentMxx + currentMxy * currentMxy);

                if (scaleX < 0.25 || scaleX > 2) {
                    setTransform(oldTransform);
                }
            }
        }

        // Rotate the pane
        if (rotatable) {
            prependTransform(new Rotate(Math.toDegrees(angleDelta), centre.getX(), centre.getY()));
        }

        // Update touch state
        currentTouch.setLastCentre(centre);
        otherTouch.setLastCentre(centre);
        currentTouch.setCurrentScreenPoint(touchPointPosition);
    }

    /**
     * Checks if the delta results in the pane being out of bounds.
     *
     * @param delta The desired delta based on touch events.
     * @return The new bounds to apply.
     */
    private Point2D handleBoundsCheck(Point2D delta) {

        Pane rootPane = MultitouchHandler.getRootPane();

        Point2D centre = PointUtils.getCentreOfNode(pane);

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

    private static CurrentTouch getOtherTouch(CurrentTouch currentTouch, List<? extends CurrentTouch> paneTouches) {
        if (Objects.equals(paneTouches.get(0), currentTouch)) {
            return paneTouches.get(1);
        } else {
            return paneTouches.get(0);
        }
    }

    /**
     * Handles a touch event with a single finger on a pane. Will only translate the pane.
     */
    private void handleSingleTouch(Point2D touchPointPosition, CurrentTouch currentTouch) {
        if (!currentTouch.getImportantElement().isPresent()) {
            Point2D delta = touchPointPosition.subtract(currentTouch.getCurrentScreenPoint());
            delta = handleBoundsCheck(delta);

            if (translatable) {
                prependTransform(new Translate(delta.getX(), delta.getY()));
            }
            currentTouch.setCurrentScreenPoint(touchPointPosition);
        }
    }

    private static final class TextFieldSkinConsumer implements Consumer<EventTarget> {

        private final OnScreenKeyboard<?> keyboard;
        private final Method detachKeyboard;
        private final Skin<?> skin;

        public TextFieldSkinConsumer(Skin<?> skin) {
            this.skin = skin;
            keyboard = ReflectionUtils.getField(skin, "keyboard");
            detachKeyboard = ReflectionUtils.getMethodReference(skin, "detachKeyboard",
                    OnScreenKeyboard.class, EventTarget.class);
        }

        @Override
        public void accept(EventTarget t) {
            try {
                detachKeyboard.invoke(skin, keyboard, t);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ReflectionException(e);
            }
        }
    }

    private static final class TextAreaSkinConsumer implements Consumer<EventTarget> {

        private final OnScreenKeyboard<?> keyboard;
        private final Method detachKeyboard;
        private final Skin<?> skin;

        public TextAreaSkinConsumer(Skin<?> skin) {
            this.skin = skin;
            keyboard = KeyboardManager.getInstance()
                    .getKeyboard(Util.getFocusAreaStartingNode((Node) skin.getSkinnable()));
            detachKeyboard = ReflectionUtils
                    .getMethodReference(skin, "detachKeyboard", OnScreenKeyboard.class, EventTarget.class);
        }

        @Override
        public void accept(EventTarget t) {
            try {
                detachKeyboard.invoke(skin, keyboard, t);
            } catch (IllegalAccessException | InvocationTargetException e) {
                // Sometimes an exception is thrown from the keyboard.
                throw new ReflectionException(e);
            }
        }
    }

    private static final class ComboBoxListSkinConsumer implements Consumer<EventTarget> {

        private final Method isComboBoxOrButton;
        private final Method handleAutoHidingEvents;
        private final Skin<?> skin;

        public ComboBoxListSkinConsumer(Skin<?> skin) {
            isComboBoxOrButton = ReflectionUtils.getMethodReference(skin, "isComboBoxOrButton",
                    EventTarget.class, ComboBoxBase.class);
            handleAutoHidingEvents = ReflectionUtils.getMethodReference(skin, "handleAutoHidingEvents");
            this.skin = skin;
        }

        @Override
        public void accept(EventTarget t) {
            try {
                if (!(Boolean) isComboBoxOrButton.invoke(skin, t, skin.getSkinnable())) {
                    handleAutoHidingEvents.invoke(skin);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ReflectionException(e);
            }
        }
    }

    private static final class ContextMenuSkinConsumer implements Consumer<EventTarget> {

        private final Method handleAutoHidingEvents;
        private final Skin<?> skin;

        public ContextMenuSkinConsumer(Skin<?> skin) {
            handleAutoHidingEvents = ReflectionUtils
                    .getMethodReference(skin, "handleAutoHidingEvents", Event.class);
            this.skin = skin;
        }

        @Override
        public void accept(EventTarget t) {
            try {
                handleAutoHidingEvents.invoke(skin, new Event(null, t, null));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ReflectionException(e);
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

    private static class TimedPoint {
        private final long time;
        private final Point2D position;

        public TimedPoint(long time, Point2D position) {
            this.time = time;
            this.position = position;
        }

        public long getTime() {
            return time;
        }

        public Point2D getPosition() {
            return position;
        }
    }

    public boolean isTranslatable() {
        return translatable;
    }

    /**
     * Returns if this focus area can be collided with.
     */
    public boolean isCollidable() {
        return collidable;
    }

    /**
     * Sets if this focus area can be collided with.
     */
    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }

    public void setTranslatable(boolean translatable) {
        this.translatable = translatable;
    }

    public boolean isScalable() {
        return scalable;
    }

    public void setScalable(boolean scalable) {
        this.scalable = scalable;
    }

    public boolean isRotatable() {
        return rotatable;
    }

    public void setRotatable(boolean rotatable) {
        this.rotatable = rotatable;
    }
}
