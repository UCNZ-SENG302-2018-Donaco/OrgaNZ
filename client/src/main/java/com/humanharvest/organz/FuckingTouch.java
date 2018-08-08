package com.humanharvest.organz;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.input.GestureEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.Pane;

public class FuckingTouch {
    private final List<CurrentTouch> touches = new ArrayList<>();
    private final Pane rootPane;

    public FuckingTouch(Pane rootPane) {
        this.rootPane = rootPane;
        rootPane.addEventFilter(MouseEvent.ANY, event -> {
            if (event.isSynthesized()) {
                event.consume();
            }
        });

        rootPane.addEventFilter(TouchEvent.ANY, event -> {
            event.consume();

            TouchPoint touchPoint = event.getTouchPoint();
            CurrentTouch currentTouch = getCurrentTouch(touchPoint);
            if (event.getEventType() == TouchEvent.TOUCH_PRESSED) {
                currentTouch.pane = findPane(touchPoint);
                currentTouch.originalPointX = touchPoint.getX();
                currentTouch.originalPointY = touchPoint.getY();
                currentTouch.setRelativeXY(event.getTouchPoint().getPickResult());
                currentTouch.pane.ifPresent(Node::toFront);
            } else if (event.getEventType() == TouchEvent.TOUCH_RELEASED) {
                removeCurrentTouch(touchPoint);
            } else {
                handleCurrentTouch(touchPoint, currentTouch);
            }

            System.out.println(currentTouch);
        });

        rootPane.addEventFilter(ScrollEvent.ANY, event -> {
            event.consume();
        });

        rootPane.addEventFilter(GestureEvent.ANY, event -> {
            event.consume();
        });

        rootPane.addEventFilter(RotateEvent.ANY, event -> {
            event.consume();
        });

//        rootPane.addEventFilter(Event.ANY, event -> {
//            event.consume();
//            System.out.println(event);
//        });
    }

    private void handleCurrentTouch(TouchPoint touchPoint, CurrentTouch currentTouch) {
        if (!currentTouch.pane.isPresent()) {
            return;
        }

        Pane pane = currentTouch.pane.get();

        List<CurrentTouch> paneTouches = findPaneTouches(pane);
        if (paneTouches.size() == 1) {
            double deltaX = touchPoint.getX() - currentTouch.originalPointX;
            double deltaY = touchPoint.getY() - currentTouch.originalPointY;
            pane.setTranslateX(pane.getTranslateX() + deltaX);
            pane.setTranslateY(pane.getTranslateY() + deltaY);
            currentTouch.originalPointX = touchPoint.getX();
            currentTouch.originalPointY = touchPoint.getY();

        } else if (paneTouches.size() == 2) {

        } else {

        }
    }

    private List<CurrentTouch> findPaneTouches(Pane pane) {
        List<CurrentTouch> results = new ArrayList<>();
        for (CurrentTouch currentTouch : touches) {
            if (currentTouch != null) {
                Optional<Pane> touchPane = currentTouch.pane;
                if (touchPane.isPresent() && Objects.equals(touchPane.get(), pane)) {
                    results.add(currentTouch);
                }
            }
        }
        return results;
    }

    private Optional<Pane> findPane(TouchPoint touchPoint) {
        Node intersectNode = touchPoint.getPickResult().getIntersectedNode();
        while (!Objects.equals(intersectNode.getParent(), rootPane)) {
            intersectNode = intersectNode.getParent();
            if (intersectNode == null) {
                return Optional.empty();
            }
        }
        return Optional.of((Pane)intersectNode);
    }

    private CurrentTouch getCurrentTouch(TouchPoint touchPoint) {
        while (touchPoint.getId() >= touches.size()) {
            touches.add(null);
        }

        CurrentTouch currentTouch = touches.get(touchPoint.getId());
        if (currentTouch == null) {
            currentTouch = new CurrentTouch();
            touches.set(touchPoint.getId(), currentTouch);
        }

        return currentTouch;
    }

    private void removeCurrentTouch(TouchPoint touchPoint) {
        touches.set(touchPoint.getId(), null);
    }

    private static class CurrentTouch {
        public Optional<Pane> pane;
        public double originalPointX;
        public double originalPointY;
        public double originalRelativeX;
        public double originalRelativeY;

        @Override
        public String toString() {
            return "CurrentTouch{" +
                    "pane=" + pane +
                    ", originalPointX=" + originalPointX +
                    ", originalPointY=" + originalPointY +
                    ", originalRelativeX=" + originalRelativeX +
                    ", originalRelativeY=" + originalRelativeY +
                    '}';
        }

        public void setRelativeXY(PickResult pickResult) {
            if (pane.isPresent()) {
                Pane realPane = pane.get();
                double currentX = pickResult.getIntersectedPoint().getX();
                double currentY = pickResult.getIntersectedPoint().getY();
                Node currentNode = pickResult.getIntersectedNode();

                while (!Objects.equals(currentNode, realPane)) {
                    currentX += currentNode.getLayoutX();
                    currentY += currentNode.getLayoutY();
                    currentNode = currentNode.getParent();
                }

                originalRelativeX = currentX;
                originalRelativeY = currentY;
            }
        }
    }
}
