package com.humanharvest.organz.utilities.view;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TuioFXUtils {

    /**
     * Configure a Pane with touch properties.
     * Gives the pane a unique focus area, so touch events will not interfere with any other elements.
     * Sets a drop shadow on the pane.
     * Adds mouse click and drag functionality.
     * Adds touch drag, resize, and rotate functionality.
     * @param pane The pane to apply configuration to.
     */
    public static void setupPaneWithTouchFeatures(Pane pane) {
        pane.getProperties().put("focusArea", "true");

        pane.setStyle("   -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 10, 10);"
                + "-fx-background-color: grey");

        setupMouseDrag(pane);
        setupTouch(pane);
    }

    private static void setupTouch(Pane pane) {
        pane.setOnScroll(event -> {
            //TODO: Remove debug text
            System.out.println(String.format("Scrolled: EventType: %s, TouchCount: %s, Target: %s, Source: %s, Direct: %s",
                    event.getEventType(), event.getTouchCount(), event.getTarget(), event.getSource(), event.isDirect()));

            //Prevent dragging on sliders as we want to be able to drag on them to change their value
            //TODO: Check if there is a better way to check if an element is a slider. We cannot just check Slider
            // and RangeSlider as there are skin parts maybe?
            Class<? extends EventTarget> clazz = event.getTarget().getClass();
            if (clazz.getName().contains("Slider")) {
                System.out.print("Scrolled on slider, ignoring, specifically was: ");
                System.out.println(clazz);
                return;
            }
            pane.toFront();
            pane.setTranslateX(pane.getTranslateX() + event.getDeltaX());
            pane.setTranslateY(pane.getTranslateY() + event.getDeltaY());

        });

        pane.setOnTouchPressed(event -> {
            pane.toFront();
            System.out.println(String.format("Touched: EventType: %s, TouchCount: %s, Target: %s, Source: %s", event
                    .getEventType(), event.getTouchCount(), event.getTarget(), event.getSource()));
        });

        pane.setOnRotate(event -> {
            pane.setRotate(pane.getRotate() + event.getAngle());
        });

        pane.setOnZoom(event -> {
            pane.setScaleX(pane.getScaleX() * event.getZoomFactor());
            pane.setScaleY(pane.getScaleY() * event.getZoomFactor());
        });

        setTransparentNodes(pane);
    }

    /**
     * For a given Node, add the isTouchTransparent property if they are not focus traversable.
     * Note that the given node itself does NOT have the isTouchTransparent property set.
     * @param givenNode The node to check.
     */
    public static void setTransparentNodes(Node givenNode) {
        if (givenNode instanceof Parent) {
            for (Node node : getAllDescendants((Parent) givenNode)) {
                if (!node.isFocusTraversable()) {
                    node.getProperties().put("isTouchTransparent", "true");
                }
            }
        }
    }

    /**
     * For a given Parent, get all it's descendants. Note that this function does not include the given root
     * @param root The parent to get all descendant nodes
     * @return A List of Nodes, may be empty but will not be null
     */
    private static List<Node> getAllDescendants(Parent root) {
        List<Node> nodes = new ArrayList<>();
        addAllDescendants(root, nodes);
        return nodes;
    }

    private static void addAllDescendants(Parent parent, List<Node> nodes) {
        // We must handle all cases where Parent.getChildrenUnmodifiable does NOT return the correct list of all
        // children. This happens for Parents that extend Control. We must handle each differently as each has a
        // different method to get their children.
        //
        // For every exceptional case, check if the parent matches that case, and if so, cast the Parent to that
        // Object, and get the children via it's appropriate getter.
        List<Node> children = new ArrayList<>();
        if (parent instanceof TitledPane) {
            TitledPane pane = (TitledPane) parent;
            children.add(pane.getContent());
        } else if (parent instanceof SplitPane) {
            SplitPane pane = (SplitPane) parent;
            children.addAll(pane.getItems());
        } else if (parent instanceof Accordion) {
            Accordion pane = (Accordion) parent;
            children.addAll(pane.getPanes());
        } else if (parent instanceof ScrollPane) {
            ScrollPane pane = (ScrollPane) parent;
            children.add(pane.getContent());
        } else if (parent instanceof TabPane) {
            TabPane pane = ((TabPane) parent);
            List<Tab> tabs = pane.getTabs();
            List<Node> tabNodes = tabs.stream().map(Tab::getContent).collect(Collectors.toList());
            children.addAll(tabNodes);
        } else {
            children.addAll(parent.getChildrenUnmodifiable());
        }

        //Now that we have the children, loop through them and recurse on each child that is a Parent to something
        for (Node node : children) {
            nodes.add(node);
            if (node instanceof Parent) {
                addAllDescendants((Parent) node, nodes);
            }
        }
    }

    private static void setupMouseDrag(Pane pane) {

        //TODO: Enable mouse drag
        /*

        pane.setOnMousePressed(event -> {
            System.out.println("p");
            EventTarget t = event.getTarget();
            System.out.println(t);
            pane.toFront();
            startDragX = event.getSceneX();
            startDragY = event.getSceneY();
        });

        pane.setOnMouseDragged(event -> {
            Class<? extends EventTarget> clazz = event.getTarget().getClass();
            if (clazz.getName().contains("Slider")) {
                return;
            }
            pane.toFront();
            //TODO: Not hardcode res and not have static startDrag vars
            pane.setTranslateX(withinRange(0, 1920 - pane.getWidth(), pane.getTranslateX() + event.getSceneX() - startDragX));
            pane.setTranslateY(withinRange(0, 1080 - pane.getHeight(), pane.getTranslateY() + event.getSceneY() - startDragY));
            startDragX = event.getSceneX();
            startDragY = event.getSceneY();
        });

        */
    }

    private static double withinRange(double min, double max, double value) {
        return Math.min(Math.max(value, min), max);
    }
}
