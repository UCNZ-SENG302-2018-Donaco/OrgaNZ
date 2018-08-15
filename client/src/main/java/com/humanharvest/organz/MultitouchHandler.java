package com.humanharvest.organz;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import com.sun.javafx.scene.NodeEventDispatcher;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
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

import com.humanharvest.organz.utilities.ReflectionUtils;
import org.tuiofx.widgets.skin.ChoiceBoxSkinAndroid;
import org.tuiofx.widgets.skin.KeyboardManager;
import org.tuiofx.widgets.skin.MTComboBoxListViewSkin;
import org.tuiofx.widgets.skin.MTContextMenuSkin;
import org.tuiofx.widgets.skin.OnScreenKeyboard;
import org.tuiofx.widgets.skin.TextAreaSkinAndroid;
import org.tuiofx.widgets.skin.TextFieldSkinAndroid;
import org.tuiofx.widgets.utils.Util;

public class MultitouchHandler {
    private final List<CurrentTouch> touches = new ArrayList<>();
    private final Pane rootPane;
    private final Node backdropPane;

    public MultitouchHandler(Pane rootPane) {
        this.rootPane = rootPane;
        backdropPane = rootPane.getChildren().get(0);

        rootPane.addEventFilter(TouchEvent.ANY, event -> {
            TouchPoint touchPoint = event.getTouchPoint();
            CurrentTouch currentTouch = getCurrentTouch(touchPoint);
            if (currentTouch.getPane().isPresent()) {
                currentTouch.setCurrentPanePoint(
                        currentTouch.getTransform().transform(touchPoint.getX(), touchPoint.getY()));
            }

            if (event.getEventType() == TouchEvent.TOUCH_PRESSED) {
                currentTouch.setCurrentScreenPoint(new Point2D(touchPoint.getX(), touchPoint.getY()));
                currentTouch.getPane().ifPresent(Node::toFront);
                currentTouch.getPane().ifPresent(pane -> {
                    currentTouch.getImportantElement().ifPresent(node -> {
                        NodeEventDispatcher eventDispatcher = (NodeEventDispatcher)node.getEventDispatcher();
                        eventDispatcher.dispatchCapturingEvent(event);
                    });
                    if (findPaneTouches(pane).size() == 1) {
                        FocusAreaHandler focusAreaHandler = (FocusAreaHandler)pane.getUserData();
                        focusAreaHandler.propagateEvent(event.getTarget());
                    }
                });
            } else if (event.getEventType() == TouchEvent.TOUCH_RELEASED) {
                currentTouch.getImportantElement().ifPresent(node -> {
                    NodeEventDispatcher eventDispatcher = (NodeEventDispatcher)node.getEventDispatcher();
                    eventDispatcher.dispatchCapturingEvent(event);
                });

                removeCurrentTouch(touchPoint);
            } else {
                handleCurrentTouch(touchPoint, currentTouch);
            }

            event.consume();
        });

        rootPane.addEventFilter(ScrollEvent.ANY, Event::consume);
        rootPane.addEventFilter(GestureEvent.ANY, Event::consume);
        rootPane.addEventFilter(RotateEvent.ANY, Event::consume);
    }

    private void handleCurrentTouch(TouchPoint touchPoint, CurrentTouch currentTouch) {
        currentTouch.getPane().ifPresent(pane -> {
            Point2D newTouchPoint = new Point2D(touchPoint.getX(), touchPoint.getY());

            List<CurrentTouch> paneTouches = findPaneTouches(pane);
            if (paneTouches.size() == 1) {
                if (!currentTouch.getImportantElement().isPresent()) {
                    Point2D delta = newTouchPoint.subtract(currentTouch.getCurrentScreenPoint());
                    currentTouch.getTransform().prepend(new Translate(delta.getX(), delta.getY()));
                    currentTouch.setCurrentScreenPoint(newTouchPoint);
                }

            } else if (paneTouches.size() == 2) {
                CurrentTouch otherTouch = getOtherTouch(currentTouch, paneTouches);

                double angleDelta = calculateAngleDelta(currentTouch, otherTouch, touchPoint);
                Point2D centre = min(currentTouch.getCurrentScreenPoint(), otherTouch.getCurrentScreenPoint())
                        .add(abs(currentTouch.getCurrentScreenPoint().subtract(otherTouch.getCurrentScreenPoint()))
                                .multiply(0.5));

                if (currentTouch.getLastCentre() != null) {
                    Point2D delta = centre.subtract(currentTouch.getLastCentre());
                    currentTouch.getTransform().prepend(new Translate(delta.getX(), delta.getY()));

                    double scaleDifference =
                            new Point2D(touchPoint.getX(), touchPoint.getY()).distance(
                                    otherTouch.getCurrentScreenPoint()) /
                                    currentTouch.getCurrentScreenPoint().distance(otherTouch.getCurrentScreenPoint());

                    currentTouch.getTransform()
                            .prepend(new Scale(scaleDifference, scaleDifference, centre.getX(), centre.getY()));
                }
                currentTouch.getTransform().prepend(new Rotate(Math.toDegrees(angleDelta), centre.getX(), centre.getY()));

                currentTouch.setLastCentre(centre);
                otherTouch.setLastCentre(centre);
                currentTouch.setCurrentScreenPoint(newTouchPoint);
            }
        });
    }

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

    private List<CurrentTouch> findPaneTouches(Pane pane) {
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

    private Optional<Pane> findPane(Node node) {
        Node intersectNode = node;
        while (!Objects.equals(intersectNode.getParent(), rootPane)) {
            intersectNode = intersectNode.getParent();
            if (intersectNode == null) {
                return Optional.empty();
            }
        }

        if (Objects.equals(intersectNode, backdropPane)) {
            return Optional.empty();
        }

        if (intersectNode instanceof Pane) {
            return Optional.of((Pane)intersectNode);
        }

        // Also has org.tuiofx.widgets.controls.KeyboardPane

        return Optional.empty();
    }

    private CurrentTouch getCurrentTouch(TouchPoint touchPoint) {
        while (touchPoint.getId() >= touches.size()) {
            touches.add(null);
        }

        CurrentTouch currentTouch = touches.get(touchPoint.getId());
        if (currentTouch == null) {
            currentTouch = new CurrentTouch(findPane(touchPoint.getPickResult().getIntersectedNode()), getImportantElement(touchPoint));
            touches.set(touchPoint.getId(), currentTouch);
        }

        return currentTouch;
    }

    private Optional<Node> getImportantElement(TouchPoint touchPoint) {
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

    private void removeCurrentTouch(TouchPoint touchPoint) {
        touches.set(touchPoint.getId(), null);
    }

    public static void setupPaneListener(Pane pane) {
        FocusAreaHandler handlerListener = new FocusAreaHandler(pane);
        pane.setUserData(handlerListener);

        addPaneListenerChildren(handlerListener, pane);
    }

    private static void addPaneListenerChildren(FocusAreaHandler handlerListener, Node node) {
        if (node instanceof Parent) {
            ((Parent)node).getChildrenUnmodifiable().addListener(handlerListener);
            for (Node child : ((Parent)node).getChildrenUnmodifiable()) {
                addPaneListenerChildren(handlerListener, child);
            }
        }
    }

    public Optional<FocusAreaHandler> getFocusAreaHandler(Node node) {
        Optional<Pane> pane = findPane(node);
        return pane.map(pane1 -> {
            return (FocusAreaHandler)pane1.getUserData();
        });
    }

    private static class CurrentTouch {
        private final Optional<Pane> pane;
        private final Optional<Node> importantElement;

        private Point2D currentScreenPoint;
        private Point2D currentPanePoint;
        private Affine transform;
        private Point2D lastCentre;

        public CurrentTouch(Optional<Pane> pane, Optional<Node> importantElement) {
            this.pane = pane;
            this.importantElement = importantElement;
            setupInitialTransforms();
        }

        private void setupInitialTransforms() {
            pane.ifPresent(pane -> {
                ObservableList<Transform> transforms = pane.getTransforms();
                if (transforms.isEmpty()) {
                    transform = new Affine();
                    transforms.add(transform);
                } else if (transforms.size() == 1) {
                    transform = (Affine)transforms.get(0);
                } else {
                    throw new RuntimeException();
                }
            });
        }

        public Optional<Pane> getPane() {
            return pane;
        }

        public Point2D getCurrentScreenPoint() {
            return currentScreenPoint;
        }

        public void setCurrentScreenPoint(Point2D currentScreenPoint) {
            this.currentScreenPoint = currentScreenPoint;
        }

        public Point2D getCurrentPanePoint() {
            return currentPanePoint;
        }

        public void setCurrentPanePoint(Point2D currentPanePoint) {
            this.currentPanePoint = currentPanePoint;
        }

        public Affine getTransform() {
            return transform;
        }

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
                keyboard = KeyboardManager.getInstance().getKeyboard(Util.getFocusAreaStartingNode((Node)skin.getSkinnable()));
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
                if (!(Boolean)isComboBoxOrButton.invoke(skin, t, skin.getSkinnable())) {
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

    public static class FocusAreaHandler implements InvalidationListener {
        private final Collection<Consumer<EventTarget>> skinHandlers = new ArrayList<>();
        private final Collection<Consumer<EventTarget>> popupHandlers = new ArrayList<>();
        private final Pane pane;
        private boolean outOfDate = true;

        public FocusAreaHandler(Pane pane) {
            this.pane = pane;
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
                    skinHandlers.add(new ChoiceBoxSkinConsumer((Skin<ChoiceBox<?>>)skin));
                }
            }

            if (node instanceof Parent) {
                for (Node child : ((Parent)node).getChildrenUnmodifiable()) {
                    findSkinHandlers(child);
                }
            }
        }

        public void addPopup(ContextMenu popupMenu, Consumer<EventTarget> eventHandler) {
            popupHandlers.add(eventHandler);
        }
    }
}
