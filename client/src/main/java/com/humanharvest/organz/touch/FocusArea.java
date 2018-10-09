package com.humanharvest.organz.touch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import com.humanharvest.organz.utilities.Tuple;

import com.sun.javafx.scene.NodeEventDispatcher;
import com.sun.javafx.scene.control.skin.VirtualFlow;
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

    private static final Logger LOGGER = Logger.getLogger(FocusArea.class.getName());

    private final Collection<Consumer<EventTarget>> skinHandlers = new ArrayList<>();
    private final Collection<Consumer<EventTarget>> popupHandlers = new ArrayList<>();
    private final Pane pane;
    // A chain of the last X seconds of touch events. Used to smooth out fluctuations in touch events.
    private final LinkedList<TimedPoint> eventPoints = new LinkedList<>();

    private boolean outOfDate = true;
    private boolean translatable = true;
    private boolean scalable = true;
    private boolean rotatable = true;
    private boolean collidable;

    private Affine transform;
    private List<CurrentTouch> paneTouches;
    private Point2D velocity = Point2D.ZERO;
    private boolean disableHinting;

    private Optional<VirtualFlow<?>> currentScrollingPane;

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

    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }

    public void addVelocity(Point2D velocityDelta) {
        velocity = velocity.add(velocityDelta);
    }

    public void setDisableHinting(boolean disableHinting) {
        this.disableHinting = disableHinting;
    }

    public void handleTouchEvent(TouchEvent event, CurrentTouch currentTouch) {

        paneTouches = MultitouchHandler.findPaneTouches(pane);

        if (event.getEventType() == TouchEvent.TOUCH_PRESSED) {
            onTouchPressed(event, currentTouch);
        } else if (event.getEventType() == TouchEvent.TOUCH_RELEASED) {
            onTouchReleased(event, currentTouch);
        } else {
            onTouchHeld(event, currentTouch);
        }
    }

    public boolean isTouched() {
        return paneTouches != null && !paneTouches.isEmpty();
    }

    protected void onTouchPressed(TouchEvent event, CurrentTouch currentTouch) {

        TouchPoint touchPoint = event.getTouchPoint();
        currentTouch.setCurrentScreenPoint(new Point2D(touchPoint.getX(), touchPoint.getY()));

        try {
            pane.toFront();
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "Runtime exception when setting pane to front", e);
        }

        OnScreenKeyboard<?> keyboard = KeyboardManager.getInstance().getKeyboard(pane);
        ReflectionUtils.<KeyboardPane>getField(keyboard.getSkin(), "keyboardPane").toFront();

        // Forwards the touch event to an important node.
        currentTouch.getImportantElement().ifPresent(node -> {
            NodeEventDispatcher eventDispatcher = (NodeEventDispatcher) node.getEventDispatcher();
            eventDispatcher.dispatchCapturingEvent(event);
        });

        if (paneTouches.size() == 1) {
            // Informs the focus area nodes of a touch event
            setLastPosition(System.nanoTime(), PointUtils.getCentreOfNode(pane));
            propagateEvent(event.getTarget());

            currentScrollingPane = MultitouchHandler.getVirtualFlow(touchPoint.getPickResult().getIntersectedNode());
        } else {
            currentScrollingPane = Optional.empty();
        }
    }

    protected void onTouchReleased(TouchEvent event, CurrentTouch currentTouch) {

        // Forwards the touch event to an important node.
        currentTouch.getImportantElement().ifPresent(node -> {
            NodeEventDispatcher eventDispatcher = (NodeEventDispatcher) node.getEventDispatcher();
            eventDispatcher.dispatchCapturingEvent(event);
        });

        if (paneTouches.isEmpty()) {
            pane.setCacheHint(CacheHint.QUALITY);
            setupVelocity(System.nanoTime(), PointUtils.getCentreOfNode(pane));
        }
    }

    /**
     * Handles a single new touch event. Will process both single touch events and multitouch events.
     *
     * @param currentTouch The state of the finger this event belongs to.
     */
    protected void onTouchHeld(TouchEvent event, CurrentTouch currentTouch) {

        TouchPoint touchPoint = event.getTouchPoint();
        Point2D touchPointPosition = new Point2D(touchPoint.getX(), touchPoint.getY());
        if (PointUtils.distance(touchPointPosition, currentTouch.getCurrentScreenPoint()) < 2) {
            return;
        }

        currentScrollingPane.ifPresent(virtualFlow -> {
            handleScrollEvent(virtualFlow, touchPointPosition, currentTouch.getCurrentScreenPoint());
        });

        setLastPosition(System.nanoTime(), PointUtils.getCentreOfNode(pane));

        // Find other touches belonging to this pane.
        if (paneTouches.size() == 1) {
            handleSingleTouch(touchPointPosition, currentTouch);

        } else if (paneTouches.size() == 2) {
            CurrentTouch otherTouch = getOtherTouch(currentTouch);
            handleDoubleTouch(touchPointPosition, touchPoint, currentTouch, otherTouch);
        }
    }

    /**
     * Scrolls the VirtualFlow node given the previous and new touch points.
     */
    private void handleScrollEvent(VirtualFlow<?> virtualFlow, Point2D newTouchPoint, Point2D lastTouchPoint) {

        // Gets the scale and angle out of the transformation matrix
        Tuple<Double, Double> decomposed = decomposeMatrix(transform);

        // The touch position delta
        Point2D delta = newTouchPoint.subtract(lastTouchPoint);

        // The length of the delta
        double deltaLength = PointUtils.length(delta);

        // Gets the angle of the touch delta, relative to the focus area
        double deltaAngle = Math.atan2(delta.getY(), delta.getX()) - decomposed.y;

        // The amount to scale the touch delta by
        double scale = 1 / decomposed.x * deltaLength;

        delta = new Point2D(Math.cos(deltaAngle) * scale, Math.sin(deltaAngle) * scale);

        // Adjust the VirtualFlow
        if (virtualFlow.isVertical()) {
            virtualFlow.adjustPixels(-delta.getY());
        } else {
            virtualFlow.adjustPixels(-delta.getX());
        }
    }

    /**
     * Retrieves the scale and angle from the matrix.
     * Assumes that it represents a 2d matrix with uniform scaling (ie, x=y=z),
     * and is only rotated around the Z co-ordinate.
     */
    private static Tuple<Double, Double> decomposeMatrix(Affine transform) {

        double scaleX = PointUtils.length(transform.getMxx(), transform.getMyx(), transform.getMzx());
        double scaleY = PointUtils.length(transform.getMxy(), transform.getMyy(), transform.getMzy());
        double scaleZ = PointUtils.length(transform.getMxz(), transform.getMyz(), transform.getMzz());

        Affine rotate = new Affine(
                transform.getMxx() / scaleX, transform.getMxy() / scaleY, transform.getMxz() / scaleZ, 0,
                transform.getMyx() / scaleX, transform.getMyy() / scaleY, transform.getMyz() / scaleZ, 0,
                transform.getMzx() / scaleX, transform.getMzy() / scaleY, transform.getMzz() / scaleZ, 0);

        double[] quaternion = matrixToQuaternion(rotate);
        double[] angles = quaternionToAxisAngle(quaternion);

        // Assume x/y axis is null
        double angle = angles[3] * Math.signum(angles[2]);

        // Assume scale is uniform
        return new Tuple(scaleX, angle);
    }

    /**
     * Converts a quaternion to an axis angle [x, y, z, angle]
     */
    private static double[] quaternionToAxisAngle(double[] quaternion) {
        // Code lovingly stolen from:
        // http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToAngle/

        // Assume normalised quaternion

        double angle = 2 * Math.acos(quaternion[3]);
        double s = Math.sqrt(1 - quaternion[3] * quaternion[3]);
        // assuming quaternion normalised then w is less than 1, so term
        // always positive.
        if (s < 0.001) {
            // test to avoid divide by zero, s is always positive due to sqrt
            // if s close to zero then direction of axis not important
            double x = quaternion[0];
            double y = quaternion[1];
            double z = quaternion[2];
            return new double[] { x, y, z, angle };
        } else {
            double x = quaternion[0] / s; // normalise axis
            double y = quaternion[1] / s;
            double z = quaternion[2] / s;
            return new double[] { x, y, z, angle };
        }
    }

    /**
     * Converts a matrix to a quaternion. Assumes it's only a rotation matrix.
     */
    private static double[] matrixToQuaternion(Affine affine) {

        // Code lovingly stolen from
        // http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/

        double tr = affine.getMxx() + affine.getMyy() + affine.getMzz();

        double qx;
        double qy;
        double qz;
        double qw;

        if (tr > 0) {
            double S = Math.sqrt(tr+1.0) * 2; // S=4*qw
            qw = 0.25 * S;
            qx = (affine.getMzy() - affine.getMyz()) / S;
            qy = (affine.getMxz() - affine.getMzx()) / S;
            qz = (affine.getMyx() - affine.getMxy()) / S;
        } else if (affine.getMxx() > affine.getMyy() && affine.getMxx() > affine.getMzz()) {
            double S = Math.sqrt(1.0 + affine.getMxx() - affine.getMyy() - affine.getMzz()) * 2; // S=4*qx
            qw = (affine.getMzy() - affine.getMyz()) / S;
            qx = 0.25 * S;
            qy = (affine.getMxy() + affine.getMyx()) / S;
            qz = (affine.getMxz() + affine.getMzx()) / S;
        } else if (affine.getMyy() > affine.getMzz()) {
            double S = Math.sqrt(1.0 + affine.getMyy() - affine.getMxx() - affine.getMzz()) * 2; // S=4*qy
            qw = (affine.getMxz() - affine.getMzx()) / S;
            qx = (affine.getMxy() + affine.getMyx()) / S;
            qy = 0.25 * S;
            qz = (affine.getMyz() + affine.getMzy()) / S;
        } else {
            double S = Math.sqrt(1.0 + affine.getMyy() - affine.getMxx() - affine.getMyy()) * 2; // S=4*qz
            qw = (affine.getMyx() - affine.getMxy()) / S;
            qx = (affine.getMxz() + affine.getMzx()) / S;
            qy = (affine.getMyz() + affine.getMzy()) / S;
            qz = 0.25 * S;
        }

        return new double[]{ qx, qy, qz, qw };
    }

    /**
     * Handles a touch event with two fingers on the pane. Will translate, rotate and scale the pane.
     */
    protected void handleDoubleTouch(
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
            if (translatable) {
                handleTranslate(centre, currentTouch.getLastCentre());
            }

            // Scale the pane
            if (scalable) {
                double scaleDelta =
                        new Point2D(touchPoint.getX(), touchPoint.getY()).distance(
                                otherTouch.getCurrentScreenPoint()) /
                                currentTouch.getCurrentScreenPoint().distance(otherTouch.getCurrentScreenPoint());

                handleScale(scaleDelta, centre);
            }
        }

        // Rotate the pane
        handleRotate(angleDelta, centre);

        // Update touch state
        currentTouch.setLastCentre(centre);
        otherTouch.setLastCentre(centre);
        currentTouch.setCurrentScreenPoint(touchPointPosition);
    }

    protected void handleTranslate(Point2D currentCentre, Point2D lastCentre) {
        Point2D delta = currentCentre.subtract(lastCentre);
        delta = handleBoundsCheck(delta);
        prependTransform(new Translate(delta.getX(), delta.getY()));
    }

    protected void handleScale(double scaleDelta, Point2D centre) {
        Affine oldTransform = new Affine(transform);

        Scale scaleTransform = new Scale(scaleDelta, scaleDelta, centre.getX(), centre.getY());
        prependTransform(scaleTransform);

        double currentMxx = transform.getMxx();
        double currentMxy = transform.getMxy();
        double scaleX = Math.sqrt(currentMxx * currentMxx + currentMxy * currentMxy);

        if (scaleX < 0.25 || scaleX > 2) {
            setTransform(oldTransform);
        }
    }

    protected void handleRotate(double angleDelta, Point2D centre) {
        // Rotate the pane
        if (rotatable) {
            prependTransform(new Rotate(Math.toDegrees(angleDelta), centre.getX(), centre.getY()));
        }
    }

    /**
     * Checks if the delta results in the pane being out of bounds.
     *
     * @param delta The desired delta based on touch events.
     * @return The new bounds to apply.
     */
    protected Point2D handleBoundsCheck(Point2D delta) {

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

    private CurrentTouch getOtherTouch(CurrentTouch currentTouch) {
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
        }

        currentTouch.setCurrentScreenPoint(touchPointPosition);
    }

    protected Collection<CurrentTouch> getPaneTouches() {
        return Collections.unmodifiableList(paneTouches);
    }

    public boolean isTranslatable() {
        return translatable;
    }

    public void setTranslatable(boolean translatable) {
        this.translatable = translatable;
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
}
