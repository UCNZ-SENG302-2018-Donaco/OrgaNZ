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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.Pane;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import org.tuiofx.widgets.controls.KeyButton;
import org.tuiofx.widgets.controls.KeyboardPane;
import org.tuiofx.widgets.event.KeyButtonEvent;

public final class MultitouchHandler {

    private static final Collection<FocusArea> focusAreas = new ArrayList<>();
    private static final List<CurrentTouch> touches = new ArrayList<>();
    private static final Timer physicsTimer = new Timer();

    private static Pane rootPane;
    private static PhysicsHandler physicsHandler;

    private MultitouchHandler() {
    }

    /**
     * Setup the event listeners for the root pane.
     */
    public static void initialise(Pane root, boolean useHackyMouseTouch) {
        rootPane = root;

        root.addEventFilter(TouchEvent.ANY, MultitouchHandler::handleTouchEvent);

        root.addEventFilter(MouseEvent.ANY, event -> {
            if (event.isSynthesized()) {
                event.consume();
            }
        });

        root.addEventFilter(ScrollEvent.ANY, event -> {
            if (event.isDirect()) {
                event.consume();
            }
        });
        root.addEventFilter(RotateEvent.ANY, Event::consume);

        if (useHackyMouseTouch) {
            HackyMouseTouch.initialise(root);
        }

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
     * Finds the keyboard pane this node belongs to, or Optional.empty() if the node doesn't belong to any keyboard
     * pane.
     */
    private static Optional<KeyboardPane> findKeyboard(Node node) {
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

        if (intersectNode instanceof KeyboardPane) {
            return Optional.of((KeyboardPane) intersectNode);
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
                    findKeyboard(touchPoint.getPickResult().getIntersectedNode()).orElse(null),
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
            if (node instanceof ToggleButton) {
                return Optional.of(node);
            }
            if (node instanceof Button) {
                return Optional.of(node);
            }
            if (node instanceof TextField) {
                return Optional.of(node);
            }
            if (node instanceof TextArea) {
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
            if (node instanceof ComboBox) {
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
     * Returns the virtualflow node if the touchPoint intersects it.
     */
    static Optional<VirtualFlow<?>> getVirtualFlow(Node node) {

        while (node != null && !Objects.equals(node, rootPane)) {
            if (node instanceof VirtualFlow<?>) {
                return Optional.of((VirtualFlow<?>) node);
            }

            node = node.getParent();
        }

        return Optional.empty();
    }

    /**
     * Removes the current touch from the list of touches.
     */
    static void removeCurrentTouch(TouchPoint touchPoint) {
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
        FocusArea focusArea = new FocusArea(pane);
        addPane(pane, focusArea);
    }

    /**
     * Adds a pane to the root pane. Also sets up the focus area.
     */
    public static void addPane(Pane pane, FocusArea focusArea) {
        pane.getProperties().put("focusArea", "true");

        focusAreas.add(focusArea);
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

        if (event.getEventType() == TouchEvent.TOUCH_RELEASED) {
            removeCurrentTouch(touchPoint);
        }

        currentTouch.getPane().ifPresent(pane -> {
            FocusArea focusArea = (FocusArea) pane.getUserData();
            focusArea.handleTouchEvent(event, currentTouch);
        });

        currentTouch.getKeyboardPane().ifPresent(keyboardPane -> {
            if (event.getEventType() == TouchEvent.TOUCH_RELEASED) {
                KeyButton keyButton = getKeyButton((Node) event.getTarget());
                if (keyButton != null) {
                    keyButton.fireEvent(new KeyButtonEvent(keyButton, KeyButtonEvent.SHORT_PRESSED));
                }
            }
        });

        event.consume();
    }

    private static KeyButton getKeyButton(Node target) {
        while (target != null && !(target instanceof KeyboardPane)) {
            if (target instanceof KeyButton) {
                return (KeyButton) target;
            }
            target = target.getParent();
        }
        return null;
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

    public static Pane getRootPane() {
        return rootPane;
    }

    public static void setPhysicsHandler(PhysicsHandler physicsHandler) {
        MultitouchHandler.physicsHandler = physicsHandler;
    }
}

