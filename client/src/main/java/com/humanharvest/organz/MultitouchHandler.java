package com.humanharvest.organz;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.GestureEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.Pane;
import javafx.scene.transform.*;

public class MultitouchHandler {
    private final List<CurrentTouch> touches = new ArrayList<>();
    private final Pane rootPane;

    public MultitouchHandler(Pane rootPane) {
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
            currentTouch.setRelativeXY(event.getTouchPoint().getPickResult());
            if (event.getEventType() == TouchEvent.TOUCH_PRESSED) {
                currentTouch.pane = findPane(touchPoint);
                setupInitialTransforms(currentTouch);
                currentTouch.currentScreenPoint = new Point2D(touchPoint.getX(), touchPoint.getY());
                currentTouch.pane.ifPresent(Node::toFront);
            } else if (event.getEventType() == TouchEvent.TOUCH_RELEASED) {
                removeCurrentTouch(touchPoint);
            } else {
                handleCurrentTouch(touchPoint, currentTouch);
            }
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

        Point2D newTouchPoint = new Point2D(touchPoint.getX(), touchPoint.getY());
        List<CurrentTouch> paneTouches = findPaneTouches(pane);
        if (paneTouches.size() == 1) {
            Point2D delta = newTouchPoint.subtract(currentTouch.currentScreenPoint);

            currentTouch.transform.append(new Translate(delta.getX(), delta.getY()));

            currentTouch.currentScreenPoint = newTouchPoint;

        } else if (paneTouches.size() == 2) {
            CurrentTouch otherTouch;
            if (Objects.equals(paneTouches.get(0), currentTouch)) {
                otherTouch = paneTouches.get(1);
            } else {
                otherTouch = paneTouches.get(0);
            }

            double oldAngle = calculateAngle(currentTouch.currentScreenPoint, otherTouch.currentScreenPoint);
            double newAngle = calculateAngle(new Point2D(touchPoint.getX(), touchPoint.getY()), otherTouch.currentScreenPoint);
            double angleDelta = newAngle - oldAngle;
            Point2D centre = min(currentTouch.currentPanePoint, otherTouch.currentPanePoint)
                    .add(abs(currentTouch.currentPanePoint.subtract(otherTouch.currentPanePoint)).multiply(0.5));

//            currentTouch.rotate.setAngle(currentTouch.rotate.getAngle() + Math.toDegrees(angleDelta));

            currentTouch.currentScreenPoint = newTouchPoint;
        } else {

        }
    }

    private static Point2D abs(Point2D point2D) {
        return new Point2D(Math.abs(point2D.getX()), Math.abs(point2D.getY()));
    }

    private static Point2D min(Point2D p1, Point2D p2) {
        return new Point2D(Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()));
    }

    private double calculateAngle(Point2D point1, Point2D point2) {
        return Math.atan2(point2.getY() - point1.getY(), point2.getX() - point1.getX());
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

    private void setupInitialTransforms(CurrentTouch currentTouch) {
        currentTouch.pane.ifPresent(pane -> {
            ObservableList<Transform> transforms = pane.getTransforms();
            if (transforms.isEmpty()) {
                currentTouch.transform = new Affine();
                transforms.add(currentTouch.transform);
            } else if (transforms.size() == 1) {
                currentTouch.transform = (Affine)transforms.get(0);
            } else {
                throw new RuntimeException();
            }
        });
    }

    private CurrentTouch getCurrentTouch(TouchPoint touchPoint) {
        while (touchPoint.getId() >= touches.size()) {
            touches.add(null);
        }

        CurrentTouch currentTouch = touches.get(touchPoint.getId());
        if (currentTouch == null) {
            currentTouch = new CurrentTouch(findPane(touchPoint));
            touches.set(touchPoint.getId(), currentTouch);
        }

        return currentTouch;
    }

    private void removeCurrentTouch(TouchPoint touchPoint) {
        touches.set(touchPoint.getId(), null);
    }

    private static class CurrentTouch {
        private Optional<Pane> pane;
        public Point2D currentScreenPoint;
        public Point2D currentPanePoint;
        public Affine transform;

        public CurrentTouch(Optional<Pane> pane) {
            this.pane = pane;
        }

        public void setRelativeXY(PickResult pickResult) {
            pane.ifPresent(realPane -> {
                Point2D newScreenPoint = new Point2D(
                        pickResult.getIntersectedPoint().getX(),
                        pickResult.getIntersectedPoint().getY()
                );
                Node currentNode = pickResult.getIntersectedNode();

                while (!Objects.equals(currentNode, realPane)) {
                    newScreenPoint = newScreenPoint.add(currentNode.getLayoutX(), currentNode.getLayoutY());
                    currentNode = currentNode.getParent();
                }

                currentPanePoint = newScreenPoint;
            });
        }
    }
}
