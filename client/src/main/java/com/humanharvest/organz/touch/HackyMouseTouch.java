package com.humanharvest.organz.touch;

import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

public final class HackyMouseTouch {

    private static double mousePosX;
    private static double mousePosY;
    private static boolean isMouseScroll = true;

    private HackyMouseTouch() {
    }

    public static void initialise(Pane root) {
        root.addEventFilter(MouseEvent.MOUSE_DRAGGED, HackyMouseTouch::handleMouseDragEvent);
        root.addEventFilter(MouseEvent.MOUSE_PRESSED, HackyMouseTouch::handleMouseClickedEvent);

        root.addEventFilter(ScrollEvent.SCROLL_STARTED, event -> isMouseScroll = false);
        root.addEventFilter(ScrollEvent.SCROLL_FINISHED, event -> isMouseScroll = true);
        root.addEventFilter(ScrollEvent.SCROLL, HackyMouseTouch::handleMouseScrollEvent);
    }

    private static void handleMouseClickedEvent(MouseEvent event) {
        if (!event.isSynthesized()) {
            mousePosX = event.getSceneX();
            mousePosY = event.getSceneY();
        }
    }

    private static void handleMouseDragEvent(MouseEvent event) {
        if (!event.isPrimaryButtonDown() || event.isSynthesized()) {
            return;
        }

        Node selectedNode = event.getPickResult().getIntersectedNode();
        Optional<Pane> optionalPane = MultitouchHandler.findPane(selectedNode);
        Optional<Node> importantElement = MultitouchHandler.getImportantElement(selectedNode);
        if (optionalPane.isPresent() && !importantElement.isPresent() &&
                MultitouchHandler.getFocusAreaHandler(selectedNode).orElseThrow(IllegalArgumentException::new)
                        .isTranslatable()) {
            Pane pane = optionalPane.get();
            pane.setTranslateX(pane.getTranslateX() + event.getSceneX() - mousePosX);
            pane.setTranslateY(pane.getTranslateY() + event.getSceneY() - mousePosY);
            mousePosX = event.getSceneX();
            mousePosY = event.getSceneY();
        }
    }

    private static void handleMouseScrollEvent(ScrollEvent event) {
        if (isMouseScroll) {
            Optional<Pane> optionalPane = MultitouchHandler.findPane(event.getPickResult().getIntersectedNode());
            if (optionalPane.isPresent()) {
                Pane pane = optionalPane.get();
                pane.setScaleX(pane.getScaleX() + event.getDeltaY() * 0.001);
                pane.setScaleY(pane.getScaleY() + event.getDeltaY() * 0.001);
            }
        }
    }
}
