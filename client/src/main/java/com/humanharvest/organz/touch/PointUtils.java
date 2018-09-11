package com.humanharvest.organz.touch;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.Pane;

public final class PointUtils {

    private PointUtils() {
    }

    public static Point2D abs(Point2D point2D) {
        return new Point2D(Math.abs(point2D.getX()), Math.abs(point2D.getY()));
    }

    public static Point2D min(Point2D p1, Point2D p2) {
        return new Point2D(Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()));
    }

    public static double calculateAngle(Point2D point1, Point2D point2) {
        return Math.atan2(point2.getY() - point1.getY(), point2.getX() - point1.getX());
    }

    /**
     * Calculates the angle delta between two previous touches and a new touch.
     */
    public static double calculateAngleDelta(CurrentTouch currentTouch, CurrentTouch otherTouch,
            TouchPoint touchPoint) {

        double oldAngle = calculateAngle(
                currentTouch.getCurrentScreenPoint(),
                otherTouch.getCurrentScreenPoint());

        double newAngle = calculateAngle(
                new Point2D(touchPoint.getX(), touchPoint.getY()),
                otherTouch.getCurrentScreenPoint());

        return newAngle - oldAngle;
    }

    public static Point2D getCentreOfPane(Pane pane) {
        Bounds paneBounds = pane.getBoundsInParent();
        Point2D min = new Point2D(paneBounds.getMinX(), paneBounds.getMinY());
        Point2D max = new Point2D(paneBounds.getMaxX(), paneBounds.getMaxY());
        return min.add(max.subtract(min).multiply(0.5));
    }

    public static double distance(Point2D point1, Point2D point2D) {
        Point2D delta = point1.subtract(point2D);
        return Math.sqrt(delta.getX() * delta.getX() + delta.getY() * delta.getY());
    }
}